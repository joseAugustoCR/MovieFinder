package com.example.app.ui.auth.login

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.Navigation

import com.example.app.R
import com.example.app.SessionManager
import com.example.app.api.*
import com.example.app.base.*
import com.example.app.di.ViewModelProviderFactory
import com.example.app.ui.MainActivity
import com.example.app.ui.auth.AuthFragment
import com.example.app.utils.*
import com.example.app.utils.extensions.*
import com.example.app.utils.navigation.NavigationResult
import com.example.app.utils.navigation.NavigationResultListener
import com.facebook.*
import com.facebook.appevents.AppEventsConstants
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.github.ajalt.timberkt.d
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.onesignal.OneSignal
import kotlinx.android.synthetic.main.loading_state.*
import kotlinx.android.synthetic.main.login_fragment.*
import org.json.JSONException
import org.json.JSONObject


import javax.inject.Inject

class LoginFragment : BaseFragment(), NavigationResultListener {
    override fun onNavigationResult(result: NavigationResult) {
        if(result.requestCode == REQUEST_REGISTER && result.resultCode == NAVIGATION_RESULT_OK){
            navigateBackWithResult(NAVIGATION_RESULT_OK, isMainHost = true, data = bundleOf("type" to getAuthFragment().args.type), rCode = REQUEST_LOGIN)
        }
    }

    @Inject lateinit var providerFactory: ViewModelProviderFactory
    private lateinit var viewModel: LoginViewModel
    @Inject lateinit var sharedPreferences: SharedPreferencesManager
    val callbackManager = CallbackManager.Factory.create()
    var basicPermission = listOf("email", "public_profile")
    var profileTracker:ProfileTracker?=null
    var fbAccessToken:String?=null
    var inflatedView:View? = null
    var alreadyHandledFB:Boolean = false



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if(inflatedView  == null) {
            inflatedView = inflater.inflate(R.layout.login_fragment, container, false)
        }else{
            val parent = inflatedView?.parent
            if(parent != null){
                (parent as ViewGroup).removeView(inflatedView)
            }
        }
        return inflatedView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this, providerFactory).get(LoginViewModel::class.java)
        setupLoadingLayout()
        setListeners()
        setObservers()
        initFacebookLogin()
    }

    fun initFacebookLogin(){
        LoginManager.getInstance().logOut()
        fbLoginBtn.setFragment(this)
        fbLoginBtn.setPermissions(basicPermission)
        fbLoginBtn.registerCallback(callbackManager, object :FacebookCallback<LoginResult>{
            override fun onSuccess(result: LoginResult?) {
                result?.accessToken

                if(Profile.getCurrentProfile() == null){
                    profileTracker = object  :ProfileTracker(){
                        override fun onCurrentProfileChanged(
                            oldProfile: Profile?,
                            currentProfile: Profile?
                        ) {
                            profileTracker?.stopTracking()
                            Profile.setCurrentProfile(currentProfile)
                            fbAccessToken = result?.accessToken?.token
                            alreadyHandledFB = false
//                            viewModel.loginFacebook(FacebookLoginRequest(token = result?.accessToken?.token))
                        }
                    }
                }else{
                    Profile.getCurrentProfile()
                    fbAccessToken = result?.accessToken?.token
                    alreadyHandledFB = false
//                    viewModel.loginFacebook(FacebookLoginRequest(token = result?.accessToken?.token))
                }


            }

            override fun onCancel() {
                loadingLayout.visibility = View.GONE
            }

            override fun onError(error: FacebookException?) {
                LoginManager.getInstance().logOut()
                loadingLayout.visibility = View.GONE
                loginBtn.snack("Ocorreu um problema ao entrar com o Facebook", R.color.errorColor, {})
            }

        })
    }

    private fun fetchUserInfo() {
        val accessToken = AccessToken.getCurrentAccessToken()
        if (accessToken != null)
        {
            val request = GraphRequest.newMeRequest(
            accessToken
            ) { me, response ->
                if (Profile.getCurrentProfile() != null) {
                    Log.i("FACEBOOK NAME: ", Profile.getCurrentProfile().name)
                    getFacebookEmail()

                } else {
                    fetchUserInfo()
                }
            }
            val parameters = Bundle()
            GraphRequest.executeBatchAsync(request)
        }
        else
        {

        }
    }

    fun goToRegisterWithFB(fbEmail:String?=null){
        val name = Profile.getCurrentProfile()?.name
        val image = Profile.getCurrentProfile()?.getProfilePictureUri(400,400)
        val uid = Profile.getCurrentProfile().id

//        val facebookData = FacebookData(name = name, image = image.toString(), email = fbEmail, uid = uid)
//        navigateForResult(REQUEST_REGISTER, LoginFragmentDirections.actionLoginFragmentToRegisterFragment(facebookData))

    }


     fun getFacebookEmail() {
        val request = GraphRequest.newMeRequest(
        AccessToken.getCurrentAccessToken()
        ) { jsonObject, response ->
            try {
                if (jsonObject != null) {
                    val emailFB = jsonObject!!.getString("email")
                    goToRegisterWithFB(fbEmail = emailFB)
                }else{
                    goToRegisterWithFB(fbEmail = "")
                }
                Log.d("FACEBOOK", "email!")
            } catch (e: JSONException) {
                e.printStackTrace()
                goToRegisterWithFB(fbEmail = "")
            }
        }
                 val parameters = Bundle()
        parameters.putString("fields", "email")
                 request.parameters = parameters
        request.executeAsync()
    }

    fun setObservers(){
        viewModel.observeLogin().observe(viewLifecycleOwner, Observer {
            d{"login response"}
            if(it.status == Resource.Status.SUCCESS) {
                d{it.data.toString()}
                sessionManager.login(it.data)
                navigateBackWithResult(NAVIGATION_RESULT_OK, isMainHost = true, data = bundleOf("type" to getAuthFragment().args.type
                 ), rCode = REQUEST_LOGIN)

            }else if(it.status == Resource.Status.LOADING) {
                loadingLayout.visibility = View.VISIBLE

            }else if(it.status == Resource.Status.ERROR){
                loadingLayout.visibility = View.GONE
                loginBtn.snack(getErrorMsg(it), R.color.errorColor, {})
            }
        })

//        viewModel.observeFacebookLogin().observe(viewLifecycleOwner, Observer {
//            if(alreadyHandledFB) return@Observer
//            if(it.status == Resource.Status.SUCCESS) {
//                d{it.data.toString()}
//                sessionManager.login(it.data)
//                navigateBackWithResult(NAVIGATION_RESULT_OK, isMainHost = true, data = bundleOf("type" to getAuthFragment().args.type), rCode = REQUEST_LOGIN)
//
//
//            }else if(it.status == Resource.Status.LOADING) {
//                loadingLayout.visibility = View.VISIBLE
//
//            }else if(it.status == Resource.Status.ERROR){
//                loadingLayout.visibility = View.GONE
//                if(it.statusCode == 401){
//                    if(Profile.getCurrentProfile() != null) {
//                        alreadyHandledFB = true
//                        getFacebookEmail()
//                    }else{
//                        registerBtn.snack("Não foi possível cadastrar com o Facebook", R.color.errorColor, {})
//                    }
//                    return@Observer
//                }else{
//                    LoginManager.getInstance().logOut()
//                }
//                loginBtn.snack(getErrorMsg(it), R.color.errorColor, {})
//            }
//        })
    }

    fun getErrorMsg(data:Resource<User>) : String{
        val errors = data.errors
        var errorMsg:String = "Não foi possível acessar sua conta. Verifique seus dados."
        if(errors == null) return errorMsg

        return errorMsg
    }


    fun setupLoadingLayout(){
//        loadingBg.setBackgroundResource(R.drawable.bg_splash)
//        loadingProgressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE,android.graphics.PorterDuff.Mode.MULTIPLY)
//        loadingMsg.setTextColor(Color.WHITE)
    }


    fun setListeners(){
        registerBtn.setOnClickListener {
            navigateForResult(REQUEST_REGISTER, LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
        }

        emailEdt.afterTextChanged(emailInput){}
        password.afterTextChanged(passwordInput){}
        loginBtn.setOnClickListener {
            requireActivity().hideKeyboard()
            if(validade()){
                val status = OneSignal.getPermissionSubscriptionState()
                var oneSignalUserId = if(sharedPreferences.getObject(ONESIGNAL_ID, String::class.java).isNullOrEmpty()) status?.subscriptionStatus?.userId else sharedPreferences.getObject(ONESIGNAL_ID, String::class.java)

//                val request = UserRequest(user = User(password = password.text.toString(), email = emailEdt.text.toString(),
//                    appVersion = BuildConfig.VERSION_CODE,
//                    osVersion = Build.VERSION.RELEASE,
//                    deviceId = Settings.Secure.getString(requireContext()?.getContentResolver(),
//                        Settings.Secure.ANDROID_ID),
//                    onesignalId = oneSignalUserId
//                ))
//                d{ Gson().toJson(request) }
//                viewModel.login(request)
            }
        }

        customFbLoginBtn.setOnClickListener {
            fbLoginBtn.performClick()
        }

        forgotPasswordBtn.setOnClickListener {
            val url = "https://www.vakinha.com.br/users/password/new"
            url.loadOnBrowser(requireActivity())
        }

        backBtn.setOnClickListener {
            navigateBackWithResult(NAVIGATION_RESULT_CANCELED, isMainHost = true, data = bundleOf("type" to getAuthFragment().args.type))
        }
    }


    fun validade() : Boolean{
        var valid = true
        if(emailEdt.text.toString().isValidEmail() == false){
            emailInput.error = "Informe seu e-mail"
            valid = false
        }

        if(password.text.toString().isEmpty()){
            passwordInput.error = "Informe sua senha"
            valid = false
        }

        return valid

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        d{"login fragment result"}
    }

    private fun getAuthFragment():AuthFragment{
        return parentFragment?.parentFragment as AuthFragment
    }




}
