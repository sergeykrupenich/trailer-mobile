package com.siarheikrupenich.trailermobile

import android.app.Application
import com.siarheikrupenich.trailermobile.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class TrailerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        timberTreeForDebug()
    }

    private fun timberTreeForDebug() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
