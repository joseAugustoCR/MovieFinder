package com.example.app.base

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.Navigator
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

open class BaseFragment : DaggerFragment() {
    val navController by lazy { Navigation.findNavController(view!!) }
    @Inject  lateinit var sessionManager: SessionManager


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

}