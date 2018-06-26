package com.bharathvishal.appmanager.Classes

import android.net.Uri
import com.bharathvishal.appmanager.Constants.Constants


/**
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
