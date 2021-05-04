package com.example.app.ui.auth.login

import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer

import com.example.app.R
import com.example.app.api.*
import com.example.app.base.*
import com.example.app.di.ViewModelProviderFactory
import com.example.app.ui.auth.AuthFragment
import com.example.app.utils.*
import com.example.app.utils.extensions.*
import com.example.app.utils.navigation.NavigationResult
import com.example.app.utils.navigation.NavigationResultListener
import com.facebook.*
import com.github.ajalt.timberkt.d
import kotlinx.android.synthetic.main.login_fragment.*


import javax.inject.Inject

class LoginFragment : BaseFragment(), NavigationResultListener {
    override fun onNavigationResult(result: NavigationResult) {
        d{result.toString()}
        if(result.requestCode == REQUEST_REGISTER && result.resultCode == NAVIGATION_RESULT_OK){
        }
    }

    @Inject lateinit var providerFactory: ViewModelProviderFactory
    private lateinit var viewModel: LoginViewModel
    @Inject lateinit var sharedPreferences: SharedPreferencesManager
    val callbackManager = CallbackManager.Factory.create()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this, providerFactory).get(LoginViewModel::class.java)
        setupLoadingLayout()
        setListeners()
        setObservers()
    }

    fun setObservers(){
        viewModel.observeLogin().observe(viewLifecycleOwner, Observer {
            d{"login response"}
            if(it.status == Resource.Status.SUCCESS) {
                d{it.data.toString()}
                sessionManager.login(it.data)
//                navigateBackWithResult(NAVIGATION_RESULT_OK, isMainHost = true, data = bundleOf("type" to getAuthFragment().args.type
//                 ), rCode = REQUEST_LOGIN)

            }else if(it.status == Resource.Status.LOADING) {
                loadingLayout.visibility = View.VISIBLE

            }else if(it.status == Resource.Status.ERROR){
                loadingLayout.visibility = View.GONE
                loginBtn.snack(getErrorMsg(it), R.color.colorSnackError, {})
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
            safeNavigate(navController, LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
//            navigateForResult(REQUEST_REGISTER, LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
        }

        emailEdt.afterTextChanged(emailInput){}
        password.afterTextChanged(passwordInput){}
        loginBtn.setOnClickListener {
            requireActivity().hideKeyboard()
            if(validade()){
                getAuthFragment().setAuthNavigationResult(
                    NavigationResult(REQUEST_LOGIN, NAVIGATION_RESULT_OK, bundleOf("isLogged" to true))
                )
                getAuthFragment().finishAuth()
//                val status = OneSignal.getPermissionSubscriptionState()
//                var oneSignalUserId = if(sharedPreferences.getObject(ONESIGNAL_ID, String::class.java).isNullOrEmpty()) status?.subscriptionStatus?.userId else sharedPreferences.getObject(ONESIGNAL_ID, String::class.java)

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
        }

        forgotPasswordBtn.setOnClickListener {
            val url = "https://www.vakinha.com.br/users/password/new"
            url.loadOnBrowser(requireActivity())
        }

        backBtn.setOnClickListener {
            navController.navigateUp()
//            navigateBackWithResult(NAVIGATION_RESULT_CANCELED, isMainHost = true, data = bundleOf("type" to getAuthFragment().args.type))
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
