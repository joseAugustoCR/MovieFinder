package aioria.com.br.kotlinbaseapp.utils.files

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.loader.content.CursorLoader
import java.io.*
import java.util.*


object FileUtils {


    //todo get file extension
    //todo get file created at

    @Throws(IOException::class)
    fun copyFile(src: File, dst: File) {
        val inChannel = FileInputStream(src).channel
        val outChannel = FileOutputStream(dst).channel
        try {
            inChannel!!.transferTo(0, inChannel.size(), outChannel)
        } finally {
            inChannel?.close()
            outChannel?.close()
        }
    }

    fun getFileName(uri: Uri?): String? {
        if (uri == null) return null
        var fileName: String? = null
        val path = uri.path
        val cut = path?.lastIndexOf('/')
        if (cut != null && cut != -1) {
            fileName = path.substring(cut + 1)
        }
        return fileName
    }

    fun getFileName(file: File?): String? {
        if (file == null) return null
        var fileName: String? = null
        val path = file.path
        val cut = path.lastIndexOf('/')
        if (cut != -1) {
            fileName = path.substring(cut + 1)
        }
        return fileName
    }

    fun getMimeType(uri: Uri, context: Context): String? {
        var mimeType: String? = null
        if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            val cr = context.contentResolver
            mimeType = cr.getType(uri)
        } else {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString())
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase())
        }
        return mimeType
    }


    fun deleteFile(file: File?): Boolean {
        if (file == null) return false
        return if (file.exists()) {
            file.delete()
        } else false

    }


    fun deleteRecursive(fileOrDirectory: File) {
        if (!fileOrDirectory.exists()) return

        if (fileOrDirectory.isDirectory)
            for (child in fileOrDirectory.listFiles()!!)
                deleteRecursive(child)

        fileOrDirectory.delete()
    }


    fun deleteFile(path: String): Boolean {
        val file = File(path)
        return if (file.exists()) {
            file.delete()
        } else false
    }

    fun getFolderSize(f: File): Long {
        var size: Long = 0
        if (f.isDirectory) {
            for (file in f.listFiles()!!) {
                size += getFolderSize(file)
            }
        } else {
            size = f.length()
        }
        return size
    }


    fun getFilesFromFolder(folderPath: String, extensions: Array<String>): ArrayList<File> {

        //        final String[] EXTENSIONS = new String[]{
        //                "png", "bmp", "jpg" // and other formats you need
        //        };
        // filter to identify images based on their extensions
        val IMAGE_FILTER = FilenameFilter { dir, name ->
            for (ext in extensions) {
                if (name.endsWith(".$ext")) {
                    return@FilenameFilter true
                }
            }
            false
        }

        val file = File(folderPath)
        val fileArray = file.listFiles(IMAGE_FILTER)
        var filesList = ArrayList<File>()
        if (fileArray != null && fileArray.size > 0) {
            filesList = ArrayList(Arrays.asList(*fileArray))
        }

        Collections.reverse(filesList)

        return filesList

    }


    @Throws(IOException::class)
    fun createFile(dirPath: String, fileName: String, suffix: String): File {
        val storageDir: File
        storageDir = File(dirPath)

        try {
            storageDir.mkdirs()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return File.createTempFile(
                fileName, /* prefix */
                suffix, /* suffix */
                storageDir      /* directory */
        )
    }


    fun getRealPath(context: Context, fileUri: Uri): String? {
        val realPath: String?
        // SDK < API11
        if (Build.VERSION.SDK_INT < 11) {
            realPath = FileUtils.getRealPathFromURI_BelowAPI11(context, fileUri)
        } else if (Build.VERSION.SDK_INT < 19) {
            realPath = FileUtils.getRealPathFromURI_API11to18(context, fileUri)
        } else {
            realPath = FileUtils.getRealPathFromURI_API19(context, fileUri)
        }// SDK > 19 (Android 4.4) and up
        // SDK >= 11 && SDK < 19
        return realPath
    }

    @SuppressLint("NewApi")
    fun getRealPathFromURI_API11to18(context: Context, contentUri: Uri): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        var result: String? = null

        val cursorLoader = CursorLoader(context, contentUri, proj, null, null, null)
        val cursor = cursorLoader.loadInBackground()

        if (cursor != null) {
            val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor!!.moveToFirst()
            result = cursor!!.getString(column_index)
            cursor!!.close()
        }
        return result
    }

    fun getRealPathFromURI_BelowAPI11(context: Context, contentUri: Uri): String {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(contentUri, proj, null, null, null)
        var column_index = 0
        var result = ""
        if (cursor != null) {
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            result = cursor.getString(column_index)
            cursor.close()
            return result
        }
        return result
    }

    @SuppressLint("NewApi")
    fun getRealPathFromURI_API19(context: Context, uri: Uri): String? {

        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                // This is for checking Main Memory
                return if ("primary".equals(type, ignoreCase = true)) {
                    if (split.size > 1) {
                        Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                    } else {
                        Environment.getExternalStorageDirectory().toString() + "/"
                    }
                    // This is for checking SD Card
                } else {
                    "storage" + "/" + docId.replace(":", "/")
                }

            } else if (isDownloadsDocument(uri)) {
                val fileName = getFilePath(context, uri)
                if (fileName != null) {
                    return Environment.getExternalStorageDirectory().toString() + "/Download/" + fileName
                }

                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }

                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])

                return getDataColumn(context, contentUri, selection, selectionArgs)
            }// MediaProvider
            // DownloadsProvider
        } else if ("content".equals(uri.scheme!!, ignoreCase = true)) {

            // Return the remote address
            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(context, uri, null, null)

        } else if ("file".equals(uri.scheme!!, ignoreCase = true)) {
            return uri.path
        }// File
        // MediaStore (and general)

        return null
    }

    fun getDataColumn(context: Context, uri: Uri?, selection: String?,
                      selectionArgs: Array<String>?): String? {

        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)

        try {
            cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor!!.moveToFirst()) {
                val index = cursor!!.getColumnIndexOrThrow(column)
                return cursor!!.getString(index)
            }
        } finally {
            if (cursor != null)
                cursor!!.close()
        }
        return null
    }


    fun getFilePath(context: Context, uri: Uri): String? {

        var cursor: Cursor? = null
        val projection = arrayOf(MediaStore.MediaColumns.DISPLAY_NAME)

        try {
            cursor = context.contentResolver.query(uri, projection, null, null, null)
            if (cursor != null && cursor!!.moveToFirst()) {
                val index = cursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                return cursor!!.getString(index)
            }
        } finally {
            if (cursor != null)
                cursor!!.close()
        }
        return null
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

}
