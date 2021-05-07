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
import com.example.app.ui.MainActivity
import com.example.app.ui.auth.AuthFragment
import com.example.app.utils.*
import com.example.app.utils.extensions.*
import com.example.app.utils.navigation.NavigationResult
import com.example.app.utils.navigation.NavigationResultListener
import com.facebook.*
import com.github.ajalt.timberkt.d
import kotlinx.android.synthetic.main.login_fragment.*
import kotlinx.android.synthetic.main.login_fragment.backBtn
import kotlinx.android.synthetic.main.login_fragment.emailEdt
import kotlinx.android.synthetic.main.login_fragment.emailInput
import kotlinx.android.synthetic.main.login_fragment.loadingLayout
import kotlinx.android.synthetic.main.login_fragment.passwordInput
import kotlinx.android.synthetic.main.login_fragment.registerBtn


import javax.inject.Inject

class LoginFragment : BaseFragment(), NavigationResultListener {
    override fun onNavigationResult(result: NavigationResult) {
        d{result.toString()}
        if(result.requestCode == REQUEST_REGISTER && result.resultCode == NAVIGATION_RESULT_OK){
        }
    }

    @Inject lateinit var providerFactory: ViewModelProviderFactory
    private lateinit var viewModel: LoginViewModel
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
        setLayout()
        setupLoadingLayout()
        setListeners()
        setObservers()
    }

    fun setLayout(){
        registerBtn.visibility = if(Constants.REGISTER_ENABLED == true)  View.VISIBLE else View.GONE
        backBtn.visibility = if(getAuthFragment()?.hasBackstack() == true) View.VISIBLE else View.GONE

    }

    fun setObservers(){
        viewModel.observeLogin().observe(viewLifecycleOwner, Observer {
            d{"login response"}
            if(it.status == Resource.Status.SUCCESS) {
                d{it.data.toString()}
                sessionManager.login(it.data)
                (requireActivity() as MainActivity).getUser()
                getAuthFragment()?.setAuthNavigationResult(
                    NavigationResult(REQUEST_LOGIN, NAVIGATION_RESULT_OK, bundleOf("isLogged" to true))
                )
                getAuthFragment()?.finishAuth()

            }else if(it.status == Resource.Status.LOADING) {
                loadingLayout.visibility = View.VISIBLE

            }else if(it.status == Resource.Status.ERROR){
                loadingLayout.visibility = View.GONE
                loginBtn.snack(getErrorMsg(it), R.color.colorSnackError, {})
            }
        })
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
                viewModel.login(LoginRequest(
                    email = emailEdt.text.toString(),
                    password = password.text.toString()
                ))
            }
        }


        forgotPasswordBtn.setOnClickListener {
            val url = "https://www.vakinha.com.br/users/password/new"
            url.loadOnBrowser(requireActivity())
        }

        backBtn.setOnClickListener {
            getAuthFragment()?.finishAuth()
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



    private fun getAuthFragment():AuthFragment?{
        return parentFragment?.parentFragment as? AuthFragment?
    }




}
