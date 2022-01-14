package com.bharathvishal.appmanager.ApplicationClass

import android.app.Application
import com.google.android.material.color.DynamicColors

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        //For dynamic theming on Android 12 and above
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}