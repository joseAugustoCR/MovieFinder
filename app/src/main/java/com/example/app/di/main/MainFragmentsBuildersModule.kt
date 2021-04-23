package com.example.daggersample.di


import com.example.app.di.fragments.SaleModule
import com.example.daggersample.di.main.PerFragment
import com.example.app.di.fragments.SplashModule
import com.example.app.di.fragments.TimelineModule
import com.example.app.di.fragments.WebviewModule
import com.example.app.ui.main.MainFragment
import com.example.app.ui.main.sale.SaleFragment
import com.example.app.ui.main.splash.SplashFragment
import com.example.app.ui.main.timeline.TimelineFragment
import com.example.app.ui.main.webview.WebviewFragment
import com.example.app.ui.welcome.WelcomeFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
public abstract class MainFragmentsBuildersModule {

    @PerFragment
    @ContributesAndroidInjector(
        modules = arrayOf(SplashModule::class)
    )
    abstract fun contributeSplashFragment(): SplashFragment

    @PerFragment
    @ContributesAndroidInjector(
    )
    abstract fun contributeMainFragment(): MainFragment


    @PerFragment
    @ContributesAndroidInjector(
       modules = arrayOf(TimelineModule::class)
    )
    abstract fun contributeTimelineFragment(): TimelineFragment


    @PerFragment
    @ContributesAndroidInjector(
        modules = arrayOf(WebviewModule::class)
    )
    abstract fun contributeWebviewFragment(): WebviewFragment

    @PerFragment
    @ContributesAndroidInjector(
    )
    abstract fun contributeWelcomeFragment(): WelcomeFragment

    @PerFragment
    @ContributesAndroidInjector(
        modules = arrayOf(SaleModule::class)
    )
    abstract fun contributeSaleFragment(): SaleFragment


}