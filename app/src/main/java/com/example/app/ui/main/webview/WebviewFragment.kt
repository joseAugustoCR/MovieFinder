package com.example.app.ui.main.webview


import android.annotation.TargetApi
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.fragment.app.Fragment
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProviders

import com.example.app.R
import com.example.app.base.*
import com.example.app.di.ViewModelProviderFactory
import com.example.app.utils.navigation.NavigationResult
import com.example.app.utils.navigation.NavigationResultListener
import com.github.ajalt.timberkt.d
import kotlinx.android.synthetic.main.fragment_webview.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class WebviewFragment : BaseFragment(), NavigationResultListener {
    override fun onNavigationResult(result: NavigationResult) {

    }

//    val args:WebviewFragmentArgs by navArgs()
    var handler = Handler()

    var isLoaded:Boolean = false



    @Inject lateinit var providerFactory: ViewModelProviderFactory
    lateinit var viewModel: WebviewViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_webview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this, providerFactory).get(WebviewViewModel::class.java)
        initWebView()
        initObservers()
        if(isLoaded == false) {
            handler.postDelayed({
                loadWebView()
            }, 500)
        }
    }


    fun loadWebView(){
            webview.loadUrl("https://menume.com.br/c?lacucinadinelly")
    }

    fun initObservers(){

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear();
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.add){
        }

        return super.onOptionsItemSelected(item)
    }

    fun initWebView(){
        webview.getSettings().setDomStorageEnabled(true)
        webview.getSettings().setPluginState(WebSettings.PluginState.ON)
        webview.getSettings().setJavaScriptEnabled(true)
        webview.getSettings().loadWithOverviewMode = true
        webview.getSettings().setAllowFileAccess(true);
        webview.setBackgroundColor(Color.WHITE)
        webview.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH)
        webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webview.getSettings().setMediaPlaybackRequiresUserGesture(false)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webview.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        } else {
            webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW)
        }

        webview.setWebViewClient(object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if(url.toString().contains("excluir://")){

                    AlertDialog.Builder(requireContext()).setTitle("Tem certeza que deseja excluir essa novidade?")
                        .setPositiveButton("Sim", DialogInterface.OnClickListener { dialog, which ->
                            val newsIdIndex = url.toString().lastIndexOf("/")
                            val newsId = url.toString().substring(newsIdIndex+1)
                            d{"news ID = $newsId"}

                        })
                        .setNegativeButton("Não", DialogInterface.OnClickListener { dialog, which ->
                        })
                        .show()

                    return true
                }
                return false
            }

            @TargetApi(Build.VERSION_CODES.N)
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                if(request.url.toString().contains("excluir://")){

                    AlertDialog.Builder(requireContext()).setTitle("Tem certeza que deseja excluir essa novidade?")
                        .setPositiveButton("Sim", DialogInterface.OnClickListener { dialog, which ->
                            val newsIdIndex = request.url.toString().lastIndexOf("/")
                            val newsId = request.url.toString().substring(newsIdIndex+1)
                            d{"news ID = $newsId"}

                        })
                        .setNegativeButton("Não", DialogInterface.OnClickListener { dialog, which ->
                        })
                        .show()

                    return true
                }
                return false
            }

            override fun onPageFinished(view: WebView, url: String) {
                try {
                    handler.postDelayed({
                        loadingLL.setVisibility(View.GONE)
                    }, 1400)
                } catch (e: Exception) {
                }
            }

        })

    }

    override fun onStop() {
        handler.removeCallbacksAndMessages(null)
        super.onStop()
    }

//    fun setupToolbar(){
//        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
//        NavigationUI.setupWithNavController(toolbar, navController)
//        (requireActivity() as AppCompatActivity).supportActionBar?.title = args.title
//        setHasOptionsMenu(true)
//        }
//
//    }


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
        var calendar = Calendar.getInstance()
        calendar?.timeInMillis = date.time
        val dateFormat = SimpleDateFormat("dd/MM/yyyy");
        try {
            val dateTime = dateFormat.format(date)
            return dateTime

        } catch (e : java.lang.Exception) {
            e.printStackTrace();
        }
        return ""
    }







}
