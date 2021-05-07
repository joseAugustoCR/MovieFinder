package com.example.daggersample.di


import com.example.app.di.fragments.*
import com.example.app.di.fragments.main.CastTVModule
import com.example.app.di.fragments.TermsModule
import com.example.app.di.fragments.auth.LoginModule
import com.example.app.di.fragments.auth.RegisterModule
import com.example.app.di.fragments.main.HomeModule
import com.example.app.di.fragments.main.community.CommunityModule
import com.example.app.di.fragments.main.community.CreatePostModule
import com.example.app.di.fragments.main.NotificationsModule
import com.example.app.di.fragments.main.ProductDetailModule
import com.example.app.di.fragments.main.community.PostDetailModule
import com.example.daggersample.di.main.PerFragment
import com.example.app.ui.auth.AuthFragment
import com.example.app.ui.auth.login.LoginFragment
import com.example.app.ui.auth.register.RegisterFragment
import com.example.app.ui.main.MainFragment
import com.example.app.ui.main.cast_tv.CastTVFragment
import com.example.app.ui.main.community.CommunityFragment
import com.example.app.ui.main.community.create_post.CreatePostFragment
import com.example.app.ui.main.community.post_detail.PostDetailFragment
import com.example.app.ui.main.home.HomeFragment
import com.example.app.ui.main.notifications.NotificationsFragment
import com.example.app.ui.main.product_detail.ProductDetailFragment
import com.example.app.ui.terms.TermsFragment
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


    @PerFragment
    @ContributesAndroidInjector(
        modules = arrayOf(PostDetailModule::class)
    )
    abstract fun postDetailFragment(): PostDetailFragment

    @PerFragment
    @ContributesAndroidInjector(
        modules = arrayOf(CreatePostModule::class)
    )
    abstract fun createPostFragment(): CreatePostFragment

    @PerFragment
    @ContributesAndroidInjector(
        modules = arrayOf(NotificationsModule::class)
    )
    abstract fun notificationsFragment(): NotificationsFragment

    @PerFragment
    @ContributesAndroidInjector(
        modules = arrayOf(TermsModule::class)
    )
    abstract fun termsFragment(): TermsFragment


    @PerFragment
    @ContributesAndroidInjector(
        modules = arrayOf(CastTVModule::class)
    )
    abstract fun castTVFragment(): CastTVFragment

    @PerFragment
    @ContributesAndroidInjector(
        modules = arrayOf(ProductDetailModule::class)
    )
    abstract fun productDetailsFragment(): ProductDetailFragment
}