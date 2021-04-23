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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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



open class BaseFragment : DaggerFragment() {

    lateinit var db:FirebaseFirestore
    private var rootView: View? = null
    var hasInitializedRootView = false

    val navController by lazy { Navigation.findNavController(requireView()) }
    @Inject  lateinit var sessionManager: SessionManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
    }

    fun getPersistentView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?, layout: Int): View? {
        if (rootView == null) {
//             Inflate the layout for this fragment
            rootView = inflater?.inflate(layout,container,false)
        } else {
            // Do not inflate the layout again.
            // The returned View of onCreateView will be added into the fragment.
            // However it is not allowed to be added twice even if the parent is same.
            // So we must remove rootView from the existing parent view group
            // (it will be added back).
            (rootView?.getParent() as? ViewGroup)?.removeView(rootView)
        }

        return rootView
    }


    /* workaround  to achieve startActivityForResult behavior with navigation component
    isMainHost = set true to force navigation related with the most external navHos, otherwise will use the first upper navHost in the hierarchy
    */
    private val requestCode: Int
        get() = arguments?.getInt(ARGUMENT_NAVIGATION_REQUEST_CODE, REQUEST_CODE_NOT_SET)
            ?: REQUEST_CODE_NOT_SET

    private fun navigateBackWithResult(
        @IdRes destination: Int, result: NavigationResult,
        isMainHost: Boolean = false
    ): Boolean {
        var childFragmentManager: FragmentManager? = null
        var mNavController = navController
        if (isMainHost) {
            mNavController = Navigation.findNavController(requireActivity(), R.id.navHostFragment)
            childFragmentManager =
                requireActivity().supportFragmentManager.findFragmentById(R.id.navHostFragment)
                    ?.childFragmentManager
        } else {
            childFragmentManager = parentFragment?.childFragmentManager
        }
        var backStackListener: FragmentManager.OnBackStackChangedListener by Delegates.notNull()
        backStackListener = FragmentManager.OnBackStackChangedListener {
            (childFragmentManager?.fragments?.get(0) as? NavigationResultListener)?.onNavigationResult(
                result
            )
            childFragmentManager?.removeOnBackStackChangedListener(backStackListener)
        }
        childFragmentManager?.addOnBackStackChangedListener(backStackListener)
        val backStackPopped = if (destination == DESTINATION_NOT_SET) {
            mNavController.popBackStack()
        } else {
            mNavController.popBackStack(destination, true)
        }
        if (!backStackPopped) {
            childFragmentManager?.removeOnBackStackChangedListener(backStackListener)
        }
        return backStackPopped
    }

    protected fun navigateBackWithResult(
        resultCode: Int,
        data: Bundle? = null,
        isMainHost: Boolean = false,
        rCode: Int = requestCode
    ): Boolean =
        navigateBackWithResult(
            DESTINATION_NOT_SET,
            NavigationResult(rCode, resultCode, data),
            isMainHost
        )

    protected fun navigateBackWithResult(
        @IdRes destination: Int, resultCode: Int,
        data: Bundle? = null,
        isMainHost: Boolean = false
    ): Boolean =
        navigateBackWithResult(
            destination,
            NavigationResult(requestCode, resultCode, data),
            isMainHost
        )


    private fun navigateForResult(
        @IdRes resId: Int, requestCode: Int, args: Bundle? = null, navOptions: NavOptions? = null,
        navigatorExtras: Navigator.Extras? = null
    ) {
        val argsWithRequestCode = (args ?: Bundle()).apply {
            putInt(ARGUMENT_NAVIGATION_REQUEST_CODE, requestCode)
        }
        navController.navigate(resId, argsWithRequestCode, navOptions, navigatorExtras)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setAnalytics()
    }

    protected fun navigateForResult(
        requestCode: Int, navDirections: NavDirections, navOptions: NavOptions? = null
    ) {
        navigateForResult(
            resId = navDirections.actionId,
            requestCode = requestCode,
            args = navDirections.arguments,
            navOptions = navOptions
        )
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
        rootView = null
        super.onDestroy()
    }

    fun safeNavigate(navController: NavController, destination: NavDirections) = with(navController) {
        currentDestination?.getAction(destination.actionId)
            ?.let {
                navigate(destination)
            }
    }



}