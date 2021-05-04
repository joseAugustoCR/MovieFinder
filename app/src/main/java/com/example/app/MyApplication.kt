package com.example.app

import android.content.Context
import android.content.pm.PackageManager
import android.util.Base64
import androidx.multidex.MultiDex
import com.example.app.utils.ONESIGNAL_ID
import com.example.app.utils.SharedPreferencesManager
import com.example.daggersample.di.DaggerAppComponent
import com.github.ajalt.timberkt.d
import com.onesignal.OSSubscriptionObserver
import com.onesignal.OSSubscriptionStateChanges
import com.onesignal.OneSignal
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import timber.log.Timber
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import javax.inject.Inject

class MyApplication : DaggerApplication(), OSSubscriptionObserver {
    @Inject
    lateinit var sharedPreferences: SharedPreferencesManager

    override fun applicationInjector(): AndroidInjector<MyApplication> {
        return DaggerAppComponent.builder().application(this).build()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        if(BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }
        printHashKey()
        initCrashlytics()
        initOneSignal()
    }

    fun initCrashlytics(){
    }

    fun initOneSignal(){


    }



    fun printHashKey() {
        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
                d{"hash" + "printHashKey() Hash Key: $hashKey"}
            }
        } catch (e: NoSuchAlgorithmException) {

        } catch (e: Exception) {

            e.printStackTrace()
        }

    }

    override fun onOSSubscriptionChanged(stateChanges: OSSubscriptionStateChanges?) {
        val id = stateChanges?.to?.userId
        val savedOnesignalID = sharedPreferences.getObject(ONESIGNAL_ID, String::class.java)
        d{"onesignal id=" + id.toString()+""}
        if(savedOnesignalID.toString().equals(id.toString()) == false){
            sharedPreferences.putObject(ONESIGNAL_ID, id)
        }
    }
}