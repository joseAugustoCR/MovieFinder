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
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.navigation.ui.NavigationUI
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList

import com.example.app.R
import com.example.app.api.*
import com.example.app.base.*
import com.example.app.di.ViewModelProviderFactory
import com.example.app.ui.MainActivity
import com.example.app.ui.auth.AuthFragment
import com.example.app.utils.Constants
import com.example.app.utils.TERMS_ACCEPTED
import com.example.app.utils.extensions.*
import com.example.app.utils.navigation.NavigationResult
import com.github.ajalt.timberkt.d
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.analytics.FirebaseAnalytics
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder
import com.yarolegovich.lovelydialog.LovelyChoiceDialog
import kotlinx.android.synthetic.main.appbar.*
import kotlinx.android.synthetic.main.appbar.appBar
import kotlinx.android.synthetic.main.appbar.toolbar
import kotlinx.android.synthetic.main.collapsing_appbar_regular.*
import kotlinx.android.synthetic.main.register_fragment.*

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

    var imageFile:File?=null
    var resizedFile:File?=null
    var objectKey:String?=null


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
        setLayout()
        setListeners()
        initObservers()
    }

    fun setupToolbar(){
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        NavigationUI.setupWithNavController(toolbar, navController)
        collapsingToolbar.title = if(sessionManager.isLogged())"Editar perfil" else "Cadastro"
        setHasOptionsMenu(true)
    }


    fun setLayout(){
        if(sessionManager.isLogged()){
            emailInput.visibility = View.GONE
            phoneInput.visibility = View.GONE
            passwordInput.visibility = View.GONE
            nameEdt.setText(sessionManager.getUser()?.name)
            if(sessionManager.getUser()?.image.isNullOrEmpty() == false){
                userImg.load(sessionManager.getUser()?.image.toString(), isRoundImage = true, placeholder = R.drawable.ic_star)
                changeImage.text = "Trocar imagem"
            }
        }
    }


    fun initObservers(){
        viewModel.observeRegister().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it.status == Resource.Status.SUCCESS) {
                sessionManager.login(it.data)
                d{it.data.toString()}
                (requireActivity() as MainActivity).getUser()
                setNavigationResult(
                    NavigationResult(
                        REQUEST_REGISTER,
                        NAVIGATION_RESULT_OK,
                        bundleOf("isRegistered" to true)
                    )
                )
                getAuthFragment()?.finishAuth()

            }else if(it.status == Resource.Status.LOADING) {
                progressLayout.visibility = View.VISIBLE
                progress_msg.text = if(sessionManager.isLogged()) "Atualizando seu cadastro.\nAguarde..." else "Criando seu cadastro.\nAguarde..."


            }else if(it.status == Resource.Status.ERROR){
                progressLayout.visibility = View.GONE
                val errorMsg = if(it.errors?.errorMsg.isNullOrEmpty() == false){
                    it.errors?.errorMsg.toString()
                }else{
                    "Ops, algo deu errado. Tente novamente"
                }
                progressLayout.snack(errorMsg, R.color.colorSnackError, {})
            }
        })

    }



    fun validate():Boolean{
        nameInput.error = null;
        emailInput.error = null;
        phoneInput.error = null;
        passwordEdt.error = null;

        if(progressUpload.visibility == View.VISIBLE){
            progressUpload.snack("Aguarde o envio da imagem", R.color.gray, {})
            return false
        }


        if(nameEdt.text.toString().isEmpty()){
            nameInput.error = "Digite o seu nome"
            return false
        }

        if(sessionManager.isLogged() == false && (emailEdt.text.toString().isEmpty() || !emailEdt.text.toString().isValidEmail()) ){
            emailInput.error = "Digite um e-mail válido"
            return false
        }

        if(sessionManager.isLogged() == false && phoneEdt.text.toString().length < 10){
            phoneInput.error = "Digite um telefone válido"
            return false
        }

        if(sessionManager.isLogged() == false && passwordEdt.text.toString().isEmpty()){
            passwordInput.error = "Digite uma senha"
            return false
        }

        return true
    }


    fun setListeners(){

        changeImage.setOnClickListener {
            chooseImage()
        }



        registerBtn.setOnClickListener {
            requireActivity().hideKeyboard()
            if(validate()){
                var request = RegisterRequest(name = nameEdt.text.toString(),
                    email = if(sessionManager.isLogged()) null else emailEdt.text.toString(),
                    password = if(sessionManager.isLogged()) null else passwordEdt.text.toString(),
                    phonenumber = if(sessionManager.isLogged()) null else phoneEdt.text.toString().replace(Regex("[^0-9]"), ""),
                    image = if(objectKey == null) null else "perfil/$objectKey",
                    terms_accepted = sharedPreferences.getObject(TERMS_ACCEPTED, Boolean::class.java) ?: false
                )

                viewModel.register(request)
            }
        }


        phoneEdt.addTextChangedListener(BrPhoneNumberFormatter(WeakReference<TextInputEditText>(phoneEdt)))
        nameEdt.afterTextChanged(nameInput){}
        emailEdt.afterTextChanged(emailInput){}
        phoneEdt.afterTextChanged(phoneInput){}
        passwordEdt.afterTextChanged(passwordInput){}


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
                userImg.snack(
                    "Você precisa conceder as permissões para enviar uma imagem."
                    , R.color.gray, {})
            }

        }.onDeclined {
            userImg.snack(
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
                userImg.snack("Não é possível utilizar esta imagem.", R.color.gray, {})
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
            userImg?.snack("Arquivo não encontrado", R.color.colorSnackError, {})
            return
        }

        val observer = transferUtility.upload(
            Constants.S3_BUCKET_PROFILE,  /* The bucket to upload to */
            objectKey,  /* The key for the uploaded object */
            resizedFile,
            CannedAccessControlList.PublicRead
        )

        progressUpload.visibility = View.VISIBLE


        observer.setTransferListener(object :
            TransferListener {
            override fun onStateChanged(
                id: Int,
                state: TransferState
            ) {
                try {
                    if (state == TransferState.COMPLETED) {
                        d{"image uploaded!"}
                        progressUpload.visibility = View.GONE
                        changeImage.text = "Trocar imagem"
                        userImg?.loadFile(resizedFile, isRoundImage = true)

                    } else if (state == TransferState.FAILED) {
                        objectKey = null
                        progressUpload?.visibility = View.GONE
                        userImg?.snack("Erro ao fazer upload da imagem", R.color.colorSnackError, {})

                    }
                } catch (e: java.lang.Exception) {

                    objectKey = null
                    progressUpload?.visibility = View.GONE
                    userImg?.snack("Erro ao fazer upload da imagem", R.color.colorSnackError, {})
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
                progressUpload?.visibility = View.GONE
                userImg?.snack("Erro ao fazer upload da imagem", R.color.colorSnackError, {})
            }
        })
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




    private fun getAuthFragment(): AuthFragment?{
        return parentFragment?.parentFragment as? AuthFragment?
    }

}
