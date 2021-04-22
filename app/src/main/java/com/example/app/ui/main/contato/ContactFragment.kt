package com.example.app.ui.main.contato

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.app.R
import com.example.app.utils.extensions.loadOnBrowser
import com.example.app.utils.extensions.loadOnExternalBrowser
import com.example.app.utils.extensions.snack
import com.github.ajalt.timberkt.Timber
import kotlinx.android.synthetic.main.fragment_contact.*
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ContactFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ContactFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }


    fun setListeners(){
        mapBtn.setOnClickListener {
            openMap()
        }

        instagramBtn.setOnClickListener {
            "https://instagram.com/la_cucina_di_nelly".loadOnExternalBrowser(requireActivity())
        }

        whatsappBtn.setOnClickListener {
            openContact()
        }

        rateBtn.setOnClickListener {
            openRate()
        }
    }


    fun openRate(){
        "https://g.page/la-cucina-di-nelly?share".loadOnExternalBrowser(requireActivity())
    }

    fun openMap(){
        var uri = String.format(Locale.ENGLISH, "geo:%f,%f?z=%d&q=%f,%f (%s)",  -29.167048378333035 , -51.51235135426038,
            1,  -29.167048378333035, -51.51235135426038, "Di Nelly")

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        if(intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent);
        }else{
            val uriGoogleMaps = Uri.parse("market://details?id=com.google.android.apps.maps")
            val goToMarket = Intent(Intent.ACTION_VIEW, uriGoogleMaps)
//            Toast.makeText(this, "Google maps not Installed",
//                    Toast.LENGTH_SHORT).show()
            try {
                startActivity(goToMarket)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Maps not Installed",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ContactFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ContactFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }



    fun openContact(type:String = "regular"){


        val isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp")
        if (isWhatsappInstalled) {
            val sendIntent = Intent("android.intent.action.MAIN")
            sendIntent.action = Intent.ACTION_VIEW
            sendIntent.setPackage("com.whatsapp")
            val url = "https://api.whatsapp.com/send?phone=$555499406699&text=Olá, estou entrando em contato pelo aplicativo."
            sendIntent.data = Uri.parse(url)
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

}