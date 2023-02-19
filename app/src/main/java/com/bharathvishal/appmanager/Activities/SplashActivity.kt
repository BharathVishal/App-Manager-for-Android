/**
 *
 * Copyright 2018-2023 Bharath Vishal G.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **/

package com.bharathvishal.appmanager.Activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class SplashActivity : AppCompatActivity() {
    private var actvityContext: Context? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isOSSandAbove) {
            val splashScreen = installSplashScreen()
        }

        actvityContext = this@SplashActivity


        //For testing purpose
        //1 - Default Main Activity with xml
        //2 - Main activity with Jetpack Compose
        val activityTypeToLaunch = 2

        if (activityTypeToLaunch == 1) {
            val intent = Intent(actvityContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(actvityContext, MainActivityCompose::class.java)
            startActivity(intent)
            finish()
        }
    }


    private val isOSSandAbove: Boolean
        get() {
            val sdkInt = Build.VERSION.SDK_INT
            return sdkInt >= 31
        }
}