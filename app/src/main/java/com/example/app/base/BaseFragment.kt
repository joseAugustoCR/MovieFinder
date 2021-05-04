package com.example.app.base

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.TypefaceSpan
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentManager
import androidx.navigation.*
import com.example.app.R
import com.example.app.SessionManager
import com.example.app.utils.navigation.NavigationResult
import com.example.app.utils.navigation.NavigationResultListener
import com.github.ajalt.timberkt.d
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.android.support.DaggerFragment
import javax.inject.Inject
import kotlin.properties.Delegates

private const val ARGUMENT_NAVIGATION_REQUEST_CODE = "NAVIGATION_REQUEST_CODE"
const val DESTINATION_NOT_SET = -1
const val REQUEST_CODE_NOT_SET = -1
const val NAVIGATION_RESULT_CANCELED = 0
const val NAVIGATION_RESULT_OK = -1

val REQUEST_LOGIN = 1
val REQUEST_CODE_SELECT_PICTURE = 5
val REQUEST_REGISTER = 8
val REQUEST_TAKE_PICTURE = 10

const val NAVIGATION_RESULT_KEY = "navigation_result"



open class BaseFragment : DaggerFragment(), NavigationResultListener {

    val navController by lazy { Navigation.findNavController(requireView()) }
    @Inject  lateinit var sessionManager: SessionManager

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setAnalytics()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<NavigationResult?>(
            NAVIGATION_RESULT_KEY)?.observe(
            viewLifecycleOwner){
            it ->
            if(it != null){
                onNavigationResult(it)
            }
        }

    }

    fun setNavigationResult(result: NavigationResult){
        navController.previousBackStackEntry?.savedStateHandle?.set(NAVIGATION_RESULT_KEY, result)
    }



    fun setAnalytics() {
        var screenName: String = ""

        when (this.javaClass.getSimpleName()) {

        }

        d { "analytics " + screenName }
        if (screenName.isNotEmpty()) {
            FirebaseAnalytics.getInstance(requireActivity())
                .setCurrentScreen(requireActivity(), screenName, null)
        }
    }


    fun applyFontToMenuItem(menuItem: MenuItem) {
        try {
            var customFontId = R.font.montserrat_medium
            var menuTitle = menuItem.getTitle().toString();
            var typeface = ResourcesCompat.getFont(requireActivity(), customFontId);
            var spannableString = SpannableString(menuTitle);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                var typefaceSpan: TypefaceSpan?=null
                if( typeface != null) {
                    typefaceSpan = TypefaceSpan(typeface)
                }else {
                    typefaceSpan = TypefaceSpan("sans-serif");
                }
                spannableString.setSpan(typefaceSpan, 0, menuTitle?.length,
                    Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            } else {
                var customTypefaceSpan:Typeface?=null
                if(typeface != null){
                    customTypefaceSpan = Typeface.createFromAsset(activity?.assets, "quicksand_bold.ttf")
                }else{
                    customTypefaceSpan = Typeface.defaultFromStyle(Typeface.NORMAL)
                }
                spannableString.setSpan(customTypefaceSpan, 0, menuTitle.length,
                    Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            }
            menuItem.setTitle(spannableString);
        } catch (e: Exception) {
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onNavigationResult(result: NavigationResult) {

    }

    fun safeNavigate(navController: NavController, destination: NavDirections) = with(navController) {
        currentDestination?.getAction(destination.actionId)
            ?.let {
                navigate(destination)
            }
    }

}