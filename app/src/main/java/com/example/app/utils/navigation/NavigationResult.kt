package com.example.app.utils.navigation

import android.os.Bundle
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NavigationResult(val requestCode:Int, val resultCode:Int, val data:Bundle?=null) : Parcelable