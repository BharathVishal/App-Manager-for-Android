package com.bharathvishal.appmanager.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.annotation.Keep
import androidx.core.content.ContextCompat
import com.bharathvishal.appmanager.Classes.AppManager
import com.bharathvishal.appmanager.Constants.Constants
import com.bharathvishal.appmanager.R
import com.bharathvishal.appmanager.Classes.AppInfo
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Bharath Vishal on 26-06-2018.
 * Apk Info Extractor
 */

@Keep
class ApkInformationExtractor(private val context1: Context) {


    fun appManagerInitValues(): AppManager {
        val ob = AppManager()

        val intent = Intent(Intent.ACTION_MAIN, null)

        intent.addCategory(Intent.CATEGORY_LAUNCHER)

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED

        val resolveInfoList = context1.packageManager.queryIntentActivities(intent, 0)

        for (resolveInfo in resolveInfoList) {

            val activityInfo = resolveInfo.activityInfo

            if (!isSystemPackage(resolveInfo)) {

                ob.userAppSize = ob.userAppSize + 1
                val appPackageName = activityInfo.applicationInfo.packageName.toString()
                ob.userApps.add(
                    AppInfo(
                        getAppName(appPackageName),
                        appPackageName,
                        getFirstInstalledDate(appPackageName),
                        getLastUpdatedDate(appPackageName),
                        getAppVersion(appPackageName),
                        getAppIconURIByPackageName(appPackageName)
                    )
                )

                ob.userApps.sortWith { o1, o2 ->
                    o1.appName!!.compareTo(
                        o2.appName!!,
                        ignoreCase = true
                    )
                }

            } else if (isSystemPackage(resolveInfo)) {
                ob.systemAppSize = ob.systemAppSize + 1
                val appPackageName = activityInfo.applicationInfo.packageName.toString()

                val add = ob.systemApps.add(
                    AppInfo(
                        getAppName(appPackageName),
                        appPackageName,
                        getFirstInstalledDate(appPackageName),
                        getLastUpdatedDate(appPackageName),
                        getAppVersion(appPackageName),
                        getAppIconURIByPackageName(appPackageName)
                    )
                )

                ob.systemApps.sortWith { o1, o2 ->
                    o1.appName!!.compareTo(
                        o2.appName!!,
                        ignoreCase = true
                    )
                }
            }
        }
        return ob
    }


    //Returns Package Name
    fun getAllInstalledUserApkInfo(): List<String> {
        val apkPackageName = ArrayList<String>()

        val intent = Intent(Intent.ACTION_MAIN, null)

        intent.addCategory(Intent.CATEGORY_LAUNCHER)

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED

        val resolveInfoList = context1.packageManager.queryIntentActivities(intent, 0)

        for (resolveInfo in resolveInfoList) {

            val activityInfo = resolveInfo.activityInfo

            if (!isSystemPackage(resolveInfo)) apkPackageName.add(activityInfo.applicationInfo.packageName)
        }
        return apkPackageName
    }


    fun getAllInstalledSystemApkInfo(): List<String> {
        val apkPackageName = ArrayList<String>()

        val intent = Intent(Intent.ACTION_MAIN, null)

        intent.addCategory(Intent.CATEGORY_LAUNCHER)

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED

        val resolveInfoList = context1.packageManager.queryIntentActivities(intent, 0)

        for (resolveInfo in resolveInfoList) {

            val activityInfo = resolveInfo.activityInfo

            if (isSystemPackage(resolveInfo))
                apkPackageName.add(activityInfo.applicationInfo.packageName)

        }
        return apkPackageName
    }


    //Returns App Name
    fun getAllInstalledApkInfoAppName(): List<String> {
        val apkName = ArrayList<String>()

        val intent = Intent(Intent.ACTION_MAIN, null)

        intent.addCategory(Intent.CATEGORY_LAUNCHER)

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED

        val resolveInfoList = context1.packageManager.queryIntentActivities(intent, 0)

        for (resolveInfo in resolveInfoList) {

            val activityInfo = resolveInfo.activityInfo

            if (!isSystemPackage(resolveInfo)) {
                apkName.add(getAppName(activityInfo.applicationInfo.packageName))
            }
        }
        return apkName
    }


    private fun isSystemPackage(resolveInfo: ResolveInfo): Boolean {
        return resolveInfo.activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
    }


    fun getAppIconByPackageName(ApkTempPackageName: String): Drawable? {
        val drawable: Drawable? = try {
            context1.packageManager.getApplicationIcon(ApkTempPackageName)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            ContextCompat.getDrawable(context1, R.mipmap.ic_launcher)
        }
        return drawable
    }


    private fun getAppIconURIByPackageName(ApkTempPackageName: String): Uri {
        var resUri = Uri.EMPTY
        try {
            val pm = context1.packageManager
            val appInfo = pm.getApplicationInfo(ApkTempPackageName, 0)

            if (appInfo.icon != 0) {
                resUri = Uri.parse("android.resource://" + ApkTempPackageName + "/" + appInfo.icon)
            }

        } catch (e: PackageManager.NameNotFoundException) {
            resUri = Uri.EMPTY
            e.printStackTrace()
        }

        return resUri
    }


    private fun getAppName(ApkPackageName: String): String {
        var name = Constants.STRING_EMPTY

        val applicationInfo: ApplicationInfo?

        val packageManager = context1.packageManager

        try {
            applicationInfo = packageManager.getApplicationInfo(ApkPackageName, 0)
            name = packageManager.getApplicationLabel(applicationInfo) as String
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return name
    }


    private fun getAppVersion(ApkPackageName: String): String {
        var versionName = ""
        val applicationInfo: ApplicationInfo?
        val packageManager = context1.packageManager
        try {
            applicationInfo = packageManager.getApplicationInfo(ApkPackageName, 0)
            versionName = packageManager.getPackageInfo(ApkPackageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return "v$versionName"
    }

    private fun getFirstInstalledDate(ApkPackageName: String): String {
        var firstInstalled = Constants.SYMBOL_HYPHEN
        val manager = context1.packageManager

        try {
            val info = manager.getPackageInfo(ApkPackageName, 0)
            if (info != null) {

                val temp = info.firstInstallTime
                firstInstalled = "Installed " + getDate(temp)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return firstInstalled
    }


    private fun getLastUpdatedDate(ApkPackageName: String): String {
        var lastUpdated = Constants.SYMBOL_HYPHEN
        val manager = context1.packageManager

        try {
            val info = manager.getPackageInfo(ApkPackageName, 0)
            if (info != null) {
                val temp = info.lastUpdateTime
                lastUpdated = "Updated " + getDate(temp)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return lastUpdated
    }


    @SuppressLint("SimpleDateFormat")
    private fun getDate(milliSeconds: Long): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

}