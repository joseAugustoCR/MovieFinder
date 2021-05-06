package com.example.daggersample.di


import com.example.app.di.fragments.*
import com.example.daggersample.di.main.PerFragment
import com.example.app.ui.auth.AuthFragment
import com.example.app.ui.auth.login.LoginFragment
import com.example.app.ui.auth.register.RegisterFragment
import com.example.app.ui.main.MainFragment
import com.example.app.ui.main.community.CommunityFragment
import com.example.app.ui.main.home.HomeFragment
import com.example.app.ui.splash.SplashFragment
import com.example.app.ui.main.timeline.TimelineFragment
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
        modules = arrayOf()
    )
    abstract fun contributeAuthFragment(): AuthFragment

    @PerFragment
    @ContributesAndroidInjector(
        modules = arrayOf(LoginModule::class)
    )
    abstract fun contributeLoginFragment(): LoginFragment

    @PerFragment
    @ContributesAndroidInjector(
        modules = arrayOf(RegisterModule::class)
    )
    abstract fun contributeRegisterFragment(): RegisterFragment

    @PerFragment
    @ContributesAndroidInjector(
        modules = arrayOf(HomeModule::class)
    )
    abstract fun homeFragment(): HomeFragment

    @PerFragment
    @ContributesAndroidInjector(
        modules = arrayOf(CommunityModule::class)
    )
    abstract fun communityFragment(): CommunityFragment


}