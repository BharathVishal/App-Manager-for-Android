package com.bharathvishal.appmanager.Classes

import java.util.ArrayList

/**
 * Created by Bharath Vishal on 25-06-2018.
 * Holder for apps
 */

class AppManager {
    var userApps: MutableList<AppInfo> = ArrayList()
    var systemApps: MutableList<AppInfo> = ArrayList()
    var userAppSize: Int = 0
    var systemAppSize: Int = 0
}
