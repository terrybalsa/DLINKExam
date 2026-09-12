package com.example.dlinkexam

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient

@HiltAndroidApp
class DLinkExamApplication : Application(), SingletonImageLoader.Factory {

    /**
     * OSM's tile usage policy requires a descriptive User-Agent identifying the app
     * (https://operations.osmfoundation.org/policies/tiles/) — the default OkHttp UA is not enough.
     */
    override fun newImageLoader(context: PlatformContext): ImageLoader {
        val tileHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("User-Agent", "DLINKExam/1.0 (+https://github.com/terrybalsa/DLINKExam)")
                    .build()
                chain.proceed(request)
            }
            .build()

        return ImageLoader.Builder(context)
            .components {
                add(OkHttpNetworkFetcherFactory(callFactory = { tileHttpClient }))
            }
            .build()
    }
}
