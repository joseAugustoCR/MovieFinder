package com.example.app.ui.terms

import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.app.R
import com.example.app.api.Resource
import com.example.app.base.BaseFragment
import com.example.app.di.ViewModelProviderFactory
import com.example.app.ui.MainActivity
import com.example.app.utils.extensions.snack
import kotlinx.android.synthetic.main.terms_fragment.*
import okhttp3.ResponseBody
import javax.inject.Inject

class TermsFragment : BaseFragment() {
    val handler = Handler()
    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
    companion object {
        fun newInstance() = TermsFragment()
    }

    private lateinit var viewModel: TermsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.terms_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this, providerFactory).get(TermsViewModel::class.java)
        setObservers()
        setListeners()
        viewModel.getTerms(sessionManager.getConfigs()?.terms.toString())

    }

    fun setData(data:ResponseBody?){
        data ?: return
        terms.text = Html.fromHtml(data.string())
    }

    fun setListeners(){
        acceptBtn.setOnClickListener {
            viewModel.accept()
        }
    }

    fun setObservers(){
        viewModel.observeTerms().observe(viewLifecycleOwner, Observer {
            if (it.status == Resource.Status.SUCCESS) {
                loadingLL.visibility = View.GONE
                setData(it.data?.body())

            } else if (it.status == Resource.Status.LOADING) {
                loadingLL.visibility = View.VISIBLE


            } else if (it.status == Resource.Status.ERROR) {
                loadingLL.visibility = View.GONE
                loadingLL.snack("Ops, algo deu errado. Tente novamente", R.color.colorSnackError, {})
                handler.postDelayed({
                    navController.popBackStack()
                }, 1000)

            }
        })

        viewModel.observeTerms().observe(viewLifecycleOwner, Observer {
            if (it.status == Resource.Status.SUCCESS) {
                sessionManager.setTermsAccepted()
                (requireActivity() as MainActivity).getUser()
                navController.popBackStack()

            } else if (it.status == Resource.Status.LOADING) {
                loadingLL.visibility = View.VISIBLE

            } else if (it.status == Resource.Status.ERROR) {
                loadingLL.visibility = View.GONE
                loadingLL.snack("Ops, algo deu errado. Tente novamente", R.color.colorSnackError, {})
                handler.postDelayed({
                    navController.popBackStack()
                }, 1000)

            }
        })

    }

    override fun onStop() {
        handler.removeCallbacksAndMessages(null)
        super.onStop()
    }

}