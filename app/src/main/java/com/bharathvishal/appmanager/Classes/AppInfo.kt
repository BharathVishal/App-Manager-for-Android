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

package com.bharathvishal.appmanager.Classes

import android.net.Uri
import com.bharathvishal.appmanager.Constants.Constants


/*
 * Created by Bharath Vishal on 25-06-2018.
 * App info Class
 */

class AppInfo {
    var appName: String? = null
    var appPackage: String? = null
    var installedOn: String? = null
    var lastUpdated: String? = null
    var appVersion: String? = null
    var appDrawableURI: Uri? = null


    constructor() {
        appName = Constants.STRING_EMPTY
        appPackage = Constants.STRING_EMPTY
        installedOn = Constants.STRING_EMPTY
        lastUpdated = Constants.STRING_EMPTY
        appVersion = Constants.STRING_EMPTY
        appDrawableURI = Uri.EMPTY
    }


    constructor(appName: String, appPackage: String, installed_On: String, last_Updated: String, appVersion: String, appDrawableURI: Uri) {
        this.appName = appName
        this.appPackage = appPackage
        this.installedOn = installed_On
        this.lastUpdated = last_Updated
        this.appVersion = appVersion
        this.appDrawableURI = appDrawableURI
    }
}
