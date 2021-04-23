package com.example.app.ui.main.sale

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Paint
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.example.app.R
import com.example.app.api.FirestoreCollections
import com.example.app.api.FirestoreDocuments
import com.example.app.api.Sale
import com.example.app.base.BaseFragment
import com.example.app.di.ViewModelProviderFactory
import com.example.app.ui.main.splash.SplashViewModel
import com.example.app.utils.extensions.load
import com.example.app.utils.extensions.loadOnExternalBrowser
import com.example.app.utils.extensions.snack
import com.google.firebase.firestore.ktx.toObject
import kotlinx.android.synthetic.main.fragment_contact.*
import kotlinx.android.synthetic.main.sale_fragment.*
import java.text.NumberFormat
import java.util.*
import javax.inject.Inject

class SaleFragment : BaseFragment() {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    var sale:Sale?=null

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    companion object {
        fun newInstance() = SaleFragment()
    }

    private lateinit var viewModel: SaleViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sale_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, providerFactory).get(SaleViewModel::class.java)
        setLayout()
        setListeners()
        setData()
    }

    fun setData(){
        db
            .collection(FirestoreCollections.SALE)
            .document(FirestoreDocuments.SALE_ACTIVE)
            .get()
            .addOnSuccessListener { document ->
                if(document != null){
                    sale = document.toObject<Sale>()
                    sale?.let {
                        try {
                            title.text = it.title
                            desc.text = Html.fromHtml(it.desc.toString())
                            originalPriceValue.text = currencyFormat.format(it.original_price)
                            newPriceValue.text = currencyFormat.format(it.new_price)
                            img.load(it.image.toString(), crop = false)
                            actionBtn.text = it.button_text
                            val actionUrl = it.button_action.toString()
                            if(actionUrl.contains("api.whatsapp.com/send", ignoreCase = true)){
                                actionBtn.setOnClickListener {
                                    openWhats()
                                }
                            }else{
                                actionBtn.setOnClickListener {
                                    actionUrl.toString().loadOnExternalBrowser(requireActivity())
                                }
                            }
                        } catch (e: Exception) {
                        }
                        loadingLL.visibility = View.GONE
                    }
                }
            }
            .addOnFailureListener {
                actionBtn.snack("Ops, algo deu errado", R.color.errorColor, {})

            }
    }

    fun openWhats(){
        val isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp")
        if (isWhatsappInstalled) {
            val sendIntent = Intent("android.intent.action.MAIN")
            sendIntent.action = Intent.ACTION_VIEW
            sendIntent.setPackage("com.whatsapp")

            sendIntent.data = Uri.parse(sale?.button_action.toString())
            try {
                startActivity(sendIntent)
            } catch (e: Exception) {
                whatsappBtn.snack("Não foi possível abrir o Whatsapp. Entre em contato: 54 99940-6699", {})

            }
        } else {
            val uri = Uri.parse("market://details?id=com.whatsapp")
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            Toast.makeText(requireContext(), "WhatsApp not Installed",
                Toast.LENGTH_SHORT).show()
            try {
                startActivity(goToMarket)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "WhatsApp not Installed",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun whatsappInstalledOrNot(uri: String): Boolean {
        val pm = requireActivity().packageManager
        var app_installed = false
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            app_installed = true
        } catch (e: PackageManager.NameNotFoundException) {
            app_installed = false
        }

        return app_installed
    }

    fun setListeners(){
//        actionBtn.setOnClickListener {
//            sale?.button_action.toString().loadOnExternalBrowser(requireActivity())
//        }
    }

    fun setLayout(){
        originalPriceValue.paintFlags = originalPriceValue.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG

    }

}