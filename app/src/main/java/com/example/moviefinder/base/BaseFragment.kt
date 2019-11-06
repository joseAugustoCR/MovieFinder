package com.example.moviefinder.base

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.Navigator
import com.example.moviefinder.R
import com.example.moviefinder.utils.navigation.NavigationResult
import com.example.moviefinder.utils.navigation.NavigationResultListener
import dagger.android.support.DaggerFragment
import kotlin.properties.Delegates

private const val ARGUMENT_NAVIGATION_REQUEST_CODE = "NAVIGATION_REQUEST_CODE"
const val DESTINATION_NOT_SET = -1
const val REQUEST_CODE_NOT_SET = -1
const val NAVIGATION_RESULT_CANCELED = 0
const val NAVIGATION_RESULT_OK = -1

open class BaseFragment : DaggerFragment() {
    val navController by lazy { Navigation.findNavController(view!!) }



    // workaround  to achieve startActivityForResult behavior with navigation component
    private val requestCode: Int
        get() = arguments?.getInt(ARGUMENT_NAVIGATION_REQUEST_CODE, REQUEST_CODE_NOT_SET) ?: REQUEST_CODE_NOT_SET

    private fun navigateBackWithResult(@IdRes destination: Int, result: NavigationResult): Boolean {
        val childFragmentManager = requireActivity().supportFragmentManager.findFragmentById(R.id.navHostFragment)?.childFragmentManager
        var backStackListener: FragmentManager.OnBackStackChangedListener by Delegates.notNull()
        backStackListener = FragmentManager.OnBackStackChangedListener {
            (childFragmentManager?.fragments?.get(0) as? NavigationResultListener)?.onNavigationResult(result)
            childFragmentManager?.removeOnBackStackChangedListener(backStackListener)
        }
        childFragmentManager?.addOnBackStackChangedListener(backStackListener)
        val backStackPopped = if (destination == DESTINATION_NOT_SET) {
            navController.popBackStack()
        } else {
            navController.popBackStack(destination, true)
        }
        if (!backStackPopped) {
            childFragmentManager?.removeOnBackStackChangedListener(backStackListener)
        }
        return backStackPopped
    }

    protected fun navigateBackWithResult(resultCode: Int, data: Bundle? = null): Boolean =
        navigateBackWithResult(DESTINATION_NOT_SET, NavigationResult(requestCode, resultCode, data))

    protected fun navigateBackWithResult(@IdRes destination: Int, resultCode: Int, data: Bundle? = null): Boolean =
        navigateBackWithResult(destination, NavigationResult(requestCode, resultCode, data))


    protected fun navigateForResult(
        @IdRes resId: Int, requestCode: Int, args: Bundle? = null, navOptions: NavOptions? = null,
        navigatorExtras: Navigator.Extras? = null
    ) {
        val argsWithRequestCode = (args ?: Bundle()).apply {
            putInt(ARGUMENT_NAVIGATION_REQUEST_CODE, requestCode)
        }
        navController.navigate(resId, argsWithRequestCode, navOptions, navigatorExtras)
    }

    protected fun navigateForResult(
        requestCode: Int, navDirections: NavDirections, navOptions: NavOptions? = null
    ) {
//        val extras = FragmentNavigatorExtras(appBarLayout to appBarTransition)
        navigateForResult(
            resId = navDirections.actionId,
            requestCode = requestCode,
            args = navDirections.arguments,
            navOptions = navOptions
        )
    }

}