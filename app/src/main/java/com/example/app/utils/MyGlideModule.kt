package com.example.app.utils

import android.content.Context
import android.util.Log
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPoolAdapter
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.annotation.GlideModule;

@GlideModule
class MyGlideModule(): AppGlideModule(){


    override fun isManifestParsingEnabled(): Boolean {
        return super.isManifestParsingEnabled()
    }

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setBitmapPool(BitmapPoolAdapter())
        builder.setDiskCache(
                InternalCacheDiskCacheFactory(context, 104857600))

        Log.e("GlideConfig", "DiskCache set!")


    }

}