package com.example.app.ui.auth.register

import aioria.com.br.kotlinbaseapp.utils.files.FileUtils
import aioria.com.br.kotlinbaseapp.utils.forms.BrPhoneNumberFormatter
import aioria.com.br.kotlinbaseapp.utils.forms.CpfCnpjMask
import aioria.com.br.kotlinbaseapp.utils.forms.CpfMask
import aioria.com.br.kotlinbaseapp.utils.forms.FormValidatorUtil
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.media.ExifInterface
import android.media.ThumbnailUtils
import android.net.Uri
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.navigation.ui.NavigationUI

import com.example.app.R
import com.example.app.api.*
import com.example.app.base.*
import com.example.app.di.ViewModelProviderFactory
import com.example.app.utils.extensions.*
import com.example.app.utils.navigation.NavigationResult
import com.github.ajalt.timberkt.d
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.analytics.FirebaseAnalytics
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder
import com.yarolegovich.lovelydialog.LovelyChoiceDialog
import kotlinx.android.synthetic.main.appbar.*
import kotlinx.android.synthetic.main.register_fragment.*
import kotlinx.android.synthetic.main.register_fragment.backBtn
import kotlinx.android.synthetic.main.register_fragment.cardSelectedImage
import kotlinx.android.synthetic.main.register_fragment.createForm
import kotlinx.android.synthetic.main.register_fragment.customToolbar
import kotlinx.android.synthetic.main.register_fragment.editBtn
import kotlinx.android.synthetic.main.register_fragment.editImageBtn
import kotlinx.android.synthetic.main.register_fragment.emptyImageLayout
import kotlinx.android.synthetic.main.register_fragment.errorLayout
import kotlinx.android.synthetic.main.register_fragment.loadingLayout
import kotlinx.android.synthetic.main.register_fragment.selectedImage
import kotlinx.android.synthetic.main.register_fragment.terms
import kotlinx.android.synthetic.main.register_fragment.termsCheck
import kotlinx.android.synthetic.main.register_fragment.termsLayout
import kotlinx.android.synthetic.main.register_fragment.titleTxt
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class RegisterFragment : BaseFragment() {

    companion object {
        fun newInstance() = RegisterFragment()
    }

    private lateinit var viewModel: RegisterViewModel
    @Inject lateinit var providerFactory: ViewModelProviderFactory
    var mCurrentPhotoPath:String?=null
    var mNewPhotoPath:String?=null
    var birthdayCalendar:Calendar? = null
    val statesArray = arrayOf("AC","AL","AP","AM","BA","CE","DF","ES","GO","MA","MT","MS","MG","PA","PB","PR","PE","PI","RJ","RN","RS","RO","RR","SC","SP","SE","TO")



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.register_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, providerFactory).get(RegisterViewModel::class.java)

        setupToolbar()
        setupLayout()
        setListeners()
        initObservers()
        setFacebookData()
        setData()
    }

    fun setupToolbar(){
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        NavigationUI.setupWithNavController(toolbar, navController)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Editar perfil"
        setHasOptionsMenu(true)
    }


    fun setupLayout(){
        if(sessionManager.isLogged() == false) {
            emptyImageLayout.visibility = View.GONE
            appBar.visibility = View.GONE
            titleTxt.text = Html.fromHtml("Seja <strong>bem-vindo!</strong><br>Vamos <strong>criar</strong> o seu cadastro no Vakinha.")
            terms.text =
                Html.fromHtml("Li e concordo com as <strong><a href='https://www.vakinha.com.br/termos'>Condições e Termos de Uso</a></strong>")
            terms.setMovementMethod(LinkMovementMethod.getInstance());
            FirebaseAnalytics.getInstance(requireActivity())
                .setCurrentScreen(requireActivity(), "Registro", null)

        }else{
            registerBtn.visibility =View.GONE
            termsLayout.visibility = View.GONE
            editBtn.visibility = View.VISIBLE
            customToolbar.visibility = View.GONE
            titleTxt.visibility = View.GONE

            cepInput.visibility = View.VISIBLE
            neighborInput.visibility = View.VISIBLE
            addressInput.visibility = View.VISIBLE
            cnpjInput.visibility = View.VISIBLE
            passwordInput.visibility = View.GONE
            emailInput.visibility = View.GONE
            phoneInput.visibility = View.GONE
            cpfInput.visibility = View.GONE
            val user = sessionManager.getAuthUser().value?.data
            FirebaseAnalytics.getInstance(requireActivity())
                .setCurrentScreen(requireActivity(), "Editar Perfil", null)

        }
    }


    fun initObservers(){
        viewModel.observeRegister().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it.status == Resource.Status.SUCCESS) {
                sessionManager.login(it.data)
                d{it.data.toString()}
//                navigateBackWithResult(NAVIGATION_RESULT_OK, rCode = REQUEST_REGISTER)
                navController.popBackStack()

            }else if(it.status == Resource.Status.LOADING) {
                loadingLayout.visibility = View.VISIBLE
                errorLayout.visibility = View.GONE
                createForm.visibility = View.GONE

            }else if(it.status == Resource.Status.ERROR){
                loadingLayout.visibility = View.GONE
                createForm.visibility = View.VISIBLE
                createForm.snack(getErrorMsg(it), R.color.colorSnackError, {})
            }
        })



//        viewModel.observeEdit().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
//            if(it.status == Resource.Status.SUCCESS) {
//                sessionManager.updateUser(it.data)
//                d{it.data.toString()}
//                navigateBackWithResult(NAVIGATION_RESULT_OK, rCode = REQUEST_EDIT_PROFILE)
//
//            }else if(it.status == Resource.Status.LOADING) {
//                loadingLayout.visibility = View.VISIBLE
//                errorLayout.visibility = View.GONE
//                createForm.visibility = View.GONE
//
//            }else if(it.status == Resource.Status.ERROR){
//                loadingLayout.visibility = View.GONE
//                createForm.visibility = View.VISIBLE
//                createForm.snack(getErrorMsg(it), R.color.errorColor, {})
//            }
//        })
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear();
//        inflater.inflate(R.menu.edit_profile, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if(item.itemId == R.id.save){
//            requireActivity().hideKeyboard()
//            if(validate()) {
//                val dateFormatter =  SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//                editProfile()
//
//            }
//        }
        return super.onOptionsItemSelected(item)
    }


    fun setData(){
        val user = sessionManager.getAuthUser().value?.data
        user?.let {
            nameEdt.setText(it.name)
//            emailEdt.setText(it.email)
//            if(it.city != null) cityEdt.setText(it.city)
//            if(it.address != null) addressEdt.setText(it.address)
//            if(it.state != null) stateEdt.setText(it.state)
//            if(it.birth != null) birthdayEdt.setText(dateToString(it.birth))
//            if(it.cep != null) cepEdt.setText(it.cep)
//            if(it.neighborhood != null) neighborEdt.setText(it.neighborhood)
//            if(it.cnpj != null) cnpjEdt.setText(it.cnpj)
//            if(it.cpf != null) cpfEdt.setText(it.cpf)
//            if(it.phone != null) phoneEdt.setText(it.phone)

//            selectedImage.load(it.avatar.toString(), true, true, placeholder = false)
            emptyImageLayout.visibility = View.GONE
            cardSelectedImage.visibility = View.VISIBLE
            editImageBtn.visibility = View.VISIBLE

        }
    }



    fun validate():Boolean{
        var isValid = true
        if(nameEdt.text.toString().isEmpty()){
            isValid = false
            nameInput.error = "Informe seu nome completo"
        }

        if(emailEdt.text.toString().isValidEmail() == false){
            isValid = false
            emailInput.error = "Informe um e-mail válido"
        }

        if(FormValidatorUtil.isValidCPF(CpfMask.unmask(cpfEdt.text.toString())) == false && sessionManager.isLogged() == false){
            isValid = false
            cpfInput.error = "Informe um CPF válido"
        }

        if(phoneEdt.text.toString().length <13){
            isValid = false
            phoneInput.error = "Informe seu telefone"
        }

        if(FormValidatorUtil.isPasswordValid(passwordEdt.text.toString()) == false && sessionManager.isLogged() == false){
            isValid = false
            passwordInput.error = "Digite uma senha (8 caracteres)"
        }

        // if it's edit doesn't need to accept the terms
        if(termsCheck.isChecked == false && isValid && sessionManager.isLogged() == false){
            isValid = false
            termsCheck.snack("Você precisa aceitar os termos", R.color.colorSnackError, {})
        }

        return isValid
    }

    fun setListeners(){

        backBtn.setOnClickListener {
            navController.navigateUp()
        }

        registerBtn.setOnClickListener {
            requireActivity().hideKeyboard()
            if(validate()){
                setNavigationResult(
                    NavigationResult(
                        REQUEST_REGISTER,
                        NAVIGATION_RESULT_OK,
                        bundleOf("isRegistered" to true)
                    )
                )
                navController.popBackStack()
                val dateFormatter =  SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

//                val request = RegisterRequest(
//                    UserRegistration(
//                        name = nameEdt.text.toString(),
//                        email = emailEdt.text.toString(),
//                        phone = phoneEdt.text.toString(),
//                        city =  cityEdt.text.toString(),
//                        state = stateEdt.text.toString(),
//                        app = true,
//                        password = passwordEdt.text.toString(),
//                        passwordConfirmation = passwordEdt.text.toString(),
//                        cpf = cpfEdt.text.toString(),
//                        birth = if(birthdayCalendar == null) "" else dateFormatter.format(birthdayCalendar?.timeInMillis),
//                        termsOfUse = true,
//                        provider = if(args.facebookData != null) "facebook" else null,
//                        uid = if(args.facebookData != null) args.facebookData?.uid else null,
//                        remote_avatar_url = if(args.facebookData?.image != null) args.facebookData?.image else null
//                    )
//                )

//                viewModel.register(request)
            }
        }

        emptyImageLayout.setOnClickListener{
            openGalery()
        }

        editImageBtn.setOnClickListener {
            openGalery()
        }

        cpfEdt.addTextChangedListener(CpfMask.insert(cpfEdt))
        cnpjEdt.addTextChangedListener(CpfCnpjMask.insert(cpfEdt))
        phoneEdt.addTextChangedListener(BrPhoneNumberFormatter(WeakReference<TextInputEditText>(phoneEdt)))
        nameEdt.afterTextChanged(nameInput){}
        emailEdt.afterTextChanged(emailInput){}
        cpfEdt.afterTextChanged(cpfInput){}
        birthdayEdt.afterTextChanged(birthdayInput){}
        phoneEdt.afterTextChanged(phoneInput){}
        cityEdt.afterTextChanged(cityInput){}
        passwordEdt.afterTextChanged(passwordInput){}
        stateEdt.afterTextChanged(stateInput){}

        birthdayEdt.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                datePicker()
            }
        }
        birthdayEdt.setOnClickListener {
            datePicker()
        }
        birthdayEdt.keyListener = null

        stateEdt.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                showStates()
            }
        }
        stateEdt.setOnClickListener {
            showStates()
        }
        stateEdt.keyListener = null



    }


    fun editProfile(){
        val dateFormatter =  SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        val request = User(
            name = nameEdt.text.toString()
//            city =  cityEdt.text.toString(),
//            state = stateEdt.text.toString(),
//            address = addressEdt.text.toString(),
//            cep = cepEdt.text.toString(),
//            neighborhood = neighborEdt.text.toString(),
//            cnpj = cnpjEdt.text.toString(),
//            app = true,
////            password = passwordEdt.text.toString(),
////            passwordConfirmation = passwordEdt.text.toString(),
//            birth = if(birthdayCalendar == null) "" else dateFormatter.format(birthdayCalendar?.timeInMillis),
//            avatar = mNewPhotoPath,
//            id = sessionManager.getAuthUser().value?.data?.id


        )

        viewModel.edit(request)


    }


    fun setFacebookData(){
//        if(args.facebookData == null) return
//        nameEdt.setText(if(args.facebookData?.name != null) args.facebookData?.name.toString() else "")
//        emailEdt.setText(if(args.facebookData?.email != null) args.facebookData?.email.toString() else "")
//        selectedImage.load(args.facebookData?.image.toString(), true, true, placeholder = false)
//        emptyImageLayout.visibility = View.GONE
//        cardSelectedImage.visibility = View.VISIBLE
//        editImageBtn.visibility = View.GONE

    }

    fun datePicker() {
        val calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        val dateFormatter =  SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SpinnerDatePickerDialogBuilder()
            .context(requireContext())
            .callback(object  :
                com.tsongkha.spinnerdatepicker.DatePickerDialog.OnDateSetListener {
                override fun onDateSet(
                    view: com.tsongkha.spinnerdatepicker.DatePicker?,
                    year: Int,
                    monthOfYear: Int,
                    dayOfMonth: Int
                ) {
                    birthdayCalendar = Calendar.getInstance();
                    birthdayCalendar?.set(year, monthOfYear, dayOfMonth);
                    birthdayCalendar?.set(year, monthOfYear, dayOfMonth);
                    birthdayEdt.setText(dateFormatter.format(birthdayCalendar?.getTime()!!))

                }

            })
            .spinnerTheme(R.style.NumberPickerStyle)
            .showTitle(true)
            .showDaySpinner(true)
            .defaultDate(calendar.get(Calendar.YEAR) - 20, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            .maxDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            .build()
            .show()
    }


    fun showStates(){
        requireActivity().hideKeyboard()
        LovelyChoiceDialog(requireContext())
            .setTopTitle("Selecione um estado")
            .setTopTitleColor(Color.WHITE)
            .setTopColorRes(R.color.colorPrimary)
            .setItems(statesArray, LovelyChoiceDialog.OnItemSelectedListener<String> {
                    position, item ->
                stateEdt.setText(item.toString())
                requireActivity().hideKeyboard()
            })
            .show()
    }


    private fun openGalery() {
        askPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA) {

            if(it.isAccepted) {
                LovelyChoiceDialog(requireContext())
                    .setTopTitle("Adicionar foto")
                    .setTopTitleColor(Color.WHITE)
                    .setTopColorRes(R.color.colorPrimary)
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
                selectedImage.snack(
                    "Você precisa conceder as permissões para enviar uma imagem."
                    , R.color.colorSnackError, {})
            }

        }.onDeclined {
            selectedImage.snack(
                "Você precisa conceder as permissões para enviar uma imagem."
                , R.color.colorSnackError, {})
        }
    }

    fun chooseFromGallery(){
        askPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA) {

            if(it.isAccepted == false){
                selectedImage.snack(
                    "Você precisa conceder as permissões para enviar uma imagem."
                    , R.color.colorSnackError, {})
                return@askPermission
            }
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent,
                    "Selecione uma imagem"), REQUEST_CODE_SELECT_PICTURE
            )

        }.onDeclined {
            selectedImage.snack(
                "Você precisa conceder as permissões para enviar uma imagem."
                , R.color.colorSnackError, {})
        }

    }

    fun copyStreamToFile(inputStream: InputStream, outputFile: File) {
        inputStream.use { input ->
            val outputStream = FileOutputStream(outputFile)
            outputStream.use { output ->
                val buffer = ByteArray(4 * 1024) // buffer size
                while (true) {
                    val byteCount = input.read(buffer)
                    if (byteCount < 0) break
                    output.write(buffer, 0, byteCount)
                }
                output.flush()
            }
        }
    }

    fun takePicture(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            var photoFile: File? = null
            try{
                photoFile = createImageFile("")
            }catch (e:java.lang.Exception){
                e.printStackTrace()
            }
            if(photoFile != null){
                val photoURI = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().getPackageName() + ".provider",
                    photoFile
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(intent, REQUEST_TAKE_PICTURE)
            }
        }
    }


    @Throws(IOException::class)
    private fun createImageFile(elementID: String): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        //        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        val storageDir = File(
            requireContext().getExternalFilesDir(
                Environment.DIRECTORY_PICTURES
            ), "Camera"
        )
        try {
            storageDir.mkdirs()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val image = File.createTempFile(
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            storageDir      /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.absolutePath
        return image
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_SELECT_PICTURE) {
            val uri = data?.data
            if (uri == null) return
            val realPath = FileUtils.getRealPath(requireActivity(), uri)
            if(realPath == null){
                cardSelectedImage.snack("Não é possível utilizar esta imagem.", R.color.colorSnackError, {})
                return;
            }
            var file:File? = null
            if(realPath.startsWith("content://com.google.android.apps.photos.content")){
                file = File.createTempFile(System.currentTimeMillis().toString() + ".jpg", null, requireActivity().cacheDir)
                val inputStream = requireActivity().contentResolver.openInputStream(Uri.parse(realPath))
                if(inputStream != null){
                    copyStreamToFile(inputStream = inputStream, outputFile = file)
                }else{
                    cardSelectedImage.snack("Não é possível utilizar esta imagem.", R.color.colorSnackError, {})
                    return;
                }

            }else{
                file = File(realPath)
            }
            mCurrentPhotoPath = file?.getAbsolutePath()
            selectedImage.load(mCurrentPhotoPath.toString(), true, true, placeholder = false)
            emptyImageLayout.visibility = View.GONE
            cardSelectedImage.visibility = View.VISIBLE
            editImageBtn.visibility = View.VISIBLE
            resizeJpeg()
//            uploadWithTransferUtility()

            return

        }else if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_TAKE_PICTURE){
            selectedImage.load(mCurrentPhotoPath.toString(), true, true, placeholder = false)
            emptyImageLayout.visibility = View.GONE
            cardSelectedImage.visibility = View.VISIBLE
            editImageBtn.visibility = View.VISIBLE
            resizeJpeg()
        }
    }

    fun getErrorMsg(data:Resource<User>) : String{
        val errors = data.errors
        var errorMsg: String = "Erro ao registrar. Tente novamente."

        if(sessionManager.isLogged()) {
            errorMsg = "Erro ao editar perfil. Tente novamente."
        }
        if(errors == null) return errorMsg

        return errorMsg
    }


    fun stringToDate(date:String) : Date?{
        val format = SimpleDateFormat("yyyy-MM-dd")
        try {
            return  format.parse(date)
        } catch (e : java.lang.Exception) {
        }
        return null
    }

    fun dateToString(oldDate:String?):String{
        if(oldDate == null) return ""
        val date = stringToDate(oldDate)
        if(date == null) return ""
        birthdayCalendar = Calendar.getInstance()
        birthdayCalendar?.timeInMillis = date.time
        val dateFormat = SimpleDateFormat("dd/MM/yyyy");
        try {
            val dateTime = dateFormat.format(date)
            return dateTime

        } catch (e : java.lang.Exception) {
            e.printStackTrace();
        }
        return ""
    }


    private fun resizeJpeg() {

        val imgFile = File(mCurrentPhotoPath)
        if (imgFile.exists()) {

            val o = BitmapFactory.Options()
            var out: Bitmap? = null
            try {
                out = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(imgFile.getAbsolutePath(), o), 640, 640, ThumbnailUtils.OPTIONS_RECYCLE_INPUT)
            } catch (e: OutOfMemoryError) {
                System.gc()
                o.inSampleSize = 2
                out = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(imgFile.getAbsolutePath(), o), 400, 400, ThumbnailUtils.OPTIONS_RECYCLE_INPUT)
            }

            val matrix = Matrix()

            var exif: ExifInterface? = null
            try {
                exif = ExifInterface(mCurrentPhotoPath.toString())

                val orientation = exif!!.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)

                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                    else -> {
                    }
                }

            } catch (e: IOException) {
                e.printStackTrace()
            }

            out = Bitmap.createBitmap(out!!, 0, 0, out.width, out.height,
                matrix, true)


            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val imageFileName = timeStamp + "_new" + ".jpg"
            val storageDir = File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera")

            try {
                storageDir.mkdirs()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            mNewPhotoPath = storageDir.absolutePath + "/" + imageFileName
            val file = File(mNewPhotoPath)

            val fOut: FileOutputStream
            try {
                fOut = FileOutputStream(file)
                out!!.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
                fOut.flush()
                fOut.close()
                out.recycle()
            } catch (e: Exception) {
                if (out != null && !out.isRecycled) {
                    out.recycle()
                    out = null
                }
            }

        }
    }


}
