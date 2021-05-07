package com.example.app.ui.main.community.create_post

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.example.app.R
import com.example.app.api.Post
import com.example.app.api.Resource
import com.example.app.base.*
import com.example.app.di.InjectingSavedStateViewModelFactory
import com.example.app.ui.main.community.CommunityFragment
import com.example.app.utils.Constants
import com.example.app.utils.extensions.*
import com.example.app.utils.navigation.NavigationResult
import com.github.ajalt.timberkt.d
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.yarolegovich.lovelydialog.LovelyChoiceDialog
import kotlinx.android.synthetic.main.create_post_fragment.*
import kotlinx.android.synthetic.main.create_post_fragment.emptyLayout
import kotlinx.android.synthetic.main.create_post_fragment.loadingLayout
import java.io.File
import java.util.*
import javax.inject.Inject

class CreatePostFragment : BaseFragment() {

    var imageFile:File?=null
    var resizedFile:File?=null
    var objectKey:String?=null
    var alreadyLoadUri:Boolean = false
    val args:CreatePostFragmentArgs by navArgs()

    companion object {
        fun newInstance() = CreatePostFragment()
    }
    @Inject
    lateinit var savedStateFactory: InjectingSavedStateViewModelFactory

    private lateinit var viewModel: CreatePostViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.create_post_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, savedStateFactory.create(this))[CreatePostViewModel::class.java]
        setUserData()
        setListeners()
        setObservers()
        publishBtn.isEnabled = false
        if(args.uri != null && alreadyLoadUri == false){
            setImageUri(args.uri)
            alreadyLoadUri = true
        }
    }


    fun setObservers(){
        viewModel.observeCreate().observe(viewLifecycleOwner, Observer {
            if (it.status == Resource.Status.SUCCESS) {
                setNavigationResult(
                    NavigationResult(
                        REQUEST_CREATE_POST,
                        resultCode = NAVIGATION_RESULT_OK,
                        bundleOf(
                            CommunityFragment.KEY_POST to it.data,
                        )
                    )
                )
                navController.popBackStack()

            } else if (it.status == Resource.Status.LOADING) {
                loadingLayout.visibility = View.VISIBLE


            } else if (it.status == Resource.Status.ERROR) {
                loadingLayout.visibility = View.GONE
                loadingLayout.snack("Ops, algo deu errado", R.color.colorSnackError, {})

            }
        })
    }

    fun setUserData(){
        val user = sessionManager.getUser()
        userName.text = user?.name.toString()
        userAvatar.load(user?.image.toString(), isRoundImage = true, placeholder = R.drawable.ic_star)

        if(user?.verified == true){
            userName.setCompoundDrawablesWithIntrinsicBounds(null, null,
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_star), null)
        }

    }


    fun validate() : Boolean{
        if(descEdt.text.toString().trim().isEmpty()){
            publishBtn.snack("Adicione uma legenda", R.color.colorSnackError, {})
            return false
        }

        if(loadingUpload.visibility == View.VISIBLE){
            publishBtn.snack("Aguarde o carregamento da imagem", R.color.colorSnackError, {})
            return false
        }
        return true
    }

    fun setListeners(){

        descEdt.doAfterTextChanged {
            if(it.toString().trim().isEmpty()){
                publishBtn.isEnabled = false
            }else{
                publishBtn.isEnabled = true
            }
        }


        publishBtn.setOnClickListener {
            if(validate()){
                requireActivity().hideKeyboard()
                val request = Post(
                    type = if(objectKey != null) Post.TYPE_IMAGE else Post.TYPE_TEXT,
                    image = "posts/"+objectKey,
                    desc = descEdt.text.toString()
                )
                viewModel.create(request)
            }
        }

        postImage.setOnClickListener {
            chooseImage()
        }

        backBtn.setOnClickListener {
            navController.popBackStack()
        }
    }


    fun chooseImage(){
        askPermission(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA) {
            if(it.isAccepted) {
                LovelyChoiceDialog(requireContext())
                    .setTopTitle("Adicionar imagem")
                    .setTopTitleColor(ContextCompat.getColor(requireContext(), R.color.colorLovelyChoiceTitleText))
                    .setTopColorRes(R.color.colorLovelyChoiceTop)
                    .setItems(
                        arrayOf("Da câmera", "Escolher da galeria"),
                        LovelyChoiceDialog.OnItemSelectedListener<String> { position, item ->
                            requireActivity().hideKeyboard()
                            when (position) {
                                0 -> {
                                    takePicture()
                                }
                                1 -> {
                                    chooseFromGallery()
                                }
                            }
                        })
                    .show()

            }else{
                postImage.snack(
                    "Você precisa conceder as permissões para enviar uma imagem."
                    , R.color.gray, {})
            }

        }.onDeclined {
            postImage.snack(
                "Você precisa conceder as permissões para enviar uma imagem."
                , R.color.gray, {})
        }
    }


    fun chooseFromGallery(){

        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent,
                "Selecione uma imagem"), REQUEST_SELECT_PICTURE
        )
    }

    fun takePicture(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            imageFile = requireContext().createTempImageFile()
            imageFile?.let{
                val photoURI = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().packageName + ".provider",
                    it
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                val requestCode = REQUEST_TAKE_PICTURE
                startActivityForResult(intent, requestCode)
            }
        }
    }

    fun setImageUri(uri: Uri?){
        uri ?: return
        loadingUpload.visibility = View.VISIBLE
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        if(inputStream != null) {
            val imageFile = requireContext().createTempImageFile()
            imageFile?.copyStreamToFile(inputStream = inputStream)
            val resizedFile = imageFile?.resizeImage(requireContext())
//            uploadImage()
        }else{
            loadingUpload.visibility = View.GONE
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_SELECT_PICTURE) {
            val uri = data?.data
            if (uri == null) return
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            if(inputStream != null) {
                imageFile = requireContext().createTempImageFile()
                imageFile?.copyStreamToFile(inputStream = inputStream)
                resizedFile = imageFile?.resizeImage(requireContext())
                uploadImage()
            }else{
                postImage.snack("Não é possível utilizar esta imagem.", R.color.gray, {})
                return;
            }
            return
        }

        if (requestCode == REQUEST_TAKE_PICTURE) {
            if (resultCode == Activity.RESULT_OK) {
                resizedFile = imageFile?.resizeImage(requireContext())
                uploadImage()
            }
        }
    }


    fun uploadImage(){
        val transferUtility = TransferUtility.builder()
            .context(requireContext())
            .awsConfiguration(AWSMobileClient.getInstance().configuration)
            .s3Client(AmazonS3Client(AWSMobileClient.getInstance().credentialsProvider))
            .build()

        objectKey =
            System.currentTimeMillis().toString() + "_" + UUID.randomUUID()
                .toString().substring(0, 6) + ".jpg"


        if (resizedFile?.exists() == false) {
            d{ "file doesnt exists"}
            postImage?.snack("Arquivo não encontrado", R.color.colorSnackError, {})
            return
        }

        val observer = transferUtility.upload(
            Constants.S3_BUCKET_POSTS,  /* The bucket to upload to */
            objectKey,  /* The key for the uploaded object */
            resizedFile,
            CannedAccessControlList.PublicRead
        )

        loadingUpload.visibility = View.VISIBLE


        observer.setTransferListener(object :
            TransferListener {
            override fun onStateChanged(
                id: Int,
                state: TransferState
            ) {
                try {
                    if (state == TransferState.COMPLETED) {
                        d{"image uploaded!"}
                        loadingUpload.visibility = View.GONE
                        emptyLayout.visibility = View.GONE
//                        changeImage.text = "Trocar imagem"
                        postImage.loadFile(resizedFile)

                    } else if (state == TransferState.FAILED) {
                        objectKey = null
                        emptyLayout?.visibility = View.VISIBLE
                        loadingUpload?.visibility = View.GONE
                        postImage?.snack("Erro ao fazer upload da imagem", R.color.colorSnackError, {})

                    }
                } catch (e: java.lang.Exception) {

                    objectKey = null
                    emptyLayout?.visibility = View.VISIBLE
                    loadingUpload?.visibility = View.GONE
                    postImage?.snack("Erro ao fazer upload da imagem", R.color.colorSnackError, {})
                    e.printStackTrace()
                }
            }

            override fun onProgressChanged(
                id: Int,
                bytesCurrent: Long,
                bytesTotal: Long
            ) {
                Log.d("UploadProgress", "changed")
                d{"progress changed!"}

            }

            override fun onError(id: Int, ex: java.lang.Exception) {

                objectKey = null
                loadingUpload?.visibility = View.GONE
                emptyLayout?.visibility = View.VISIBLE
                postImage?.snack("Erro ao fazer upload da imagem", R.color.colorSnackError, {})
            }
        })
    }




}