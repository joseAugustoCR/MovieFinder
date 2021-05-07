package com.example.app.ui.main.cast_tv

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.ui.NavigationUI
import com.example.app.R
import com.example.app.api.Resource
import com.example.app.base.BaseFragment
import com.example.app.di.ViewModelProviderFactory
import com.example.app.utils.extensions.afterTextChanged
import com.example.app.utils.extensions.hideKeyboard
import com.example.app.utils.extensions.snack
import kotlinx.android.synthetic.main.cast_t_v_fragment.*
import kotlinx.android.synthetic.main.collapsing_appbar_regular.*
import javax.inject.Inject

class CastTVFragment : BaseFragment() {
    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
    var key:String?=null

    companion object {
        fun newInstance() = CastTVFragment()
    }

    private lateinit var viewModel: CastTVViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.cast_t_v_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this, providerFactory).get(CastTVViewModel::class.java)
        setupToolbar()
        setObservers()
        setListeners()
        setLayout()

    }

    fun setupToolbar(){
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        NavigationUI.setupWithNavController(toolbar, navController)
        collapsingToolbar.title = "Transmitir para TV"
        setHasOptionsMenu(true)
    }


    fun setLayout(){
        val castURL = sessionManager.getConfigs()?.cast_url.toString()
        message.text = "Para assistir suas aulas na televisão, acesse ${castURL} no navegador da sua Smart TV e digite abaixo o código que irá aparecer na TV:"
    }

    fun setListeners(){
        codeEdt.afterTextChanged (codeInput) {
            key = it
        }

        sendBtn.setOnClickListener {
            if(validate()) {
                requireActivity().hideKeyboard()
                viewModel.connect(codeEdt.text.toString())
            }
        }

        codeEdt.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                if(validate()) {
                    requireActivity().hideKeyboard()
                    viewModel.connect(codeEdt.text.toString())
                }
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }

    fun validate() : Boolean{
        if(codeEdt.text.toString().isNullOrEmpty()){
            codeInput.error = "Digite o código"
            return false
        }else{
            return true
        }
    }

    fun setObservers(){
        viewModel.observeConnect().observe(viewLifecycleOwner, Observer {
            if (it.status == Resource.Status.SUCCESS) {
                if(it.data?.con == true){
                    loadingLayout.snack("Conectado!", R.color.colorSnackSuccess, {})
                    sessionManager.setCastKey(key)
                    navController.popBackStack()

                }else{
                    loadingLayout.visibility = View.GONE
                    loadingLayout.snack("Código inválido", R.color.colorSnackError, {})
                }


            } else if (it.status == Resource.Status.LOADING) {
                loadingLayout.visibility = View.VISIBLE

            } else if (it.status == Resource.Status.ERROR) {
                loadingLayout.visibility = View.GONE
                loadingLayout.snack("Código inválido", R.color.colorSnackError, {})
            }
        })
    }

}