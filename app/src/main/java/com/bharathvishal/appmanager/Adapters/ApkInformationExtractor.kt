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

package com.bharathvishal.appmanager.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.util.Log
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
        var resolveInfoList: List<ResolveInfo>? = null

        try {
            resolveInfoList = context1!!.packageManager.queryIntentActivities(intent, 0)
        } catch (e: Exception) {
            Log.d("Exception handled", "Exception Handled")
            resolveInfoList = null
        }

        if (resolveInfoList != null) {
            for (resolveInfo in resolveInfoList) {

                if (resolveInfo != null) {
                    val activityInfo = resolveInfo.activityInfo
                    if (!isSystemPackage(resolveInfo)) {
                        val appPackageNameCur = activityInfo.applicationInfo.packageName.toString()

                        try {
                            //add to the object only if there is no other object with the same package name
                            var isCurAppAlreadyPresent = false
                            try {
                                for (i in 0 until ob.userAppSize) {
                                    if (ob.userApps[i].appPackage == appPackageNameCur) {
                                        isCurAppAlreadyPresent = true
                                        break
                                    }
                                }
                            } catch (e123: Exception) {
                                Log.d("Exception handled", "Exception Handled")
                            }

                            if (!isCurAppAlreadyPresent) {
                                ob.userApps.add(
                                    AppInfo(
                                        getAppName(appPackageNameCur),
                                        appPackageNameCur,
                                        getFirstInstalledDate(appPackageNameCur),
                                        getLastUpdatedDate(appPackageNameCur),
                                        getAppVersion(appPackageNameCur),
                                        getAppIconURIByPackageName(appPackageNameCur)
                                    )
                                )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        ob.userApps.sortWith(Comparator { o1, o2 ->
                            o1.appName!!.compareTo(
                                o2.appName!!,
                                ignoreCase = true
                            )
                        })
                        ob.userAppSize = ob.userApps.size
                    } else if (isSystemPackage(resolveInfo)) {
                        val appPackageNameTemp1 =
                            activityInfo.applicationInfo.packageName.toString()

                        try {
                            //add to the object only if there is no other object with the same package name
                            var isCurAppAlreadyPresent1 = false
                            try {
                                for (i in 0 until ob.systemAppSize) {
                                    if (ob.systemApps[i].appPackage == appPackageNameTemp1) {
                                        isCurAppAlreadyPresent1 = true
                                        break
                                    }
                                }
                            } catch (e123: Exception) {
                                Log.d("Exception handled", "Exception Handled")
                            }

                            if (!isCurAppAlreadyPresent1) {
                                ob.systemApps.add(
                                    AppInfo(
                                        getAppName(appPackageNameTemp1),
                                        appPackageNameTemp1,
                                        getFirstInstalledDate(appPackageNameTemp1),
                                        getLastUpdatedDate(appPackageNameTemp1),
                                        getAppVersion(appPackageNameTemp1),
                                        getAppIconURIByPackageName(appPackageNameTemp1)
                                    )
                                )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        ob.systemApps.sortWith(Comparator { o1, o2 ->
                            o1.appName!!.compareTo(
                                o2.appName!!,
                                ignoreCase = true
                            )
                        })

                        ob.systemAppSize = ob.systemApps.size
                    }
                }//end of if resolve info
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

        val resolveInfoList = if (Build.VERSION.SDK_INT >= 33) {
            context1.packageManager.queryIntentActivities(
                intent,
                PackageManager.ResolveInfoFlags.of(0)
            )
        } else {
            context1.packageManager.queryIntentActivities(intent, 0)
        }

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

        val resolveInfoList = if (Build.VERSION.SDK_INT >= 33) {
            context1.packageManager.queryIntentActivities(
                intent,
                PackageManager.ResolveInfoFlags.of(0)
            )
        } else {
            context1.packageManager.queryIntentActivities(intent, 0)
        }

        for (resolveInfo in resolveInfoList) {

            val activityInfo = resolveInfo.activityInfo

            if (isSystemPackage(resolveInfo))
                apkPackageName.add(activityInfo.applicationInfo.packageName)

        }
        return apkPackageName
    }


    //Returns App Name
    @Suppress("unused")
    fun getAllInstalledApkInfoAppName(): List<String> {
        val apkName = ArrayList<String>()

        val intent = Intent(Intent.ACTION_MAIN, null)

        intent.addCategory(Intent.CATEGORY_LAUNCHER)

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED

        val resolveInfoList = if (Build.VERSION.SDK_INT >= 33) {
            context1.packageManager.queryIntentActivities(
                intent,
                PackageManager.ResolveInfoFlags.of(0)
            )
        } else {
            context1.packageManager.queryIntentActivities(intent, 0)
        }

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


    @Suppress("unused")
    fun getAppIconByPackageName(ApkTempPackageName: String): Drawable? {
        var drawable: Drawable?
        drawable = try {
            context1.packageManager.getApplicationIcon(ApkTempPackageName)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            ContextCompat.getDrawable(context1, R.drawable.ic_android)
        }
        return drawable
    }


    private fun getAppIconURIByPackageName(ApkTempPackageName: String): Uri {
        var resUri = Uri.EMPTY
        try {
            val pm = context1.packageManager
            val appInfo = if (Build.VERSION.SDK_INT >= 33) {
                pm.getApplicationInfo(ApkTempPackageName, PackageManager.ApplicationInfoFlags.of(0))
            } else {
                pm.getApplicationInfo(ApkTempPackageName, 0)
            }

            if (appInfo != null) {
                if (appInfo.icon != 0) {
                    resUri =
                        Uri.parse("android.resource://" + ApkTempPackageName + "/" + appInfo.icon)
                }
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
            if (Build.VERSION.SDK_INT >= 33) {
                applicationInfo = packageManager.getApplicationInfo(
                    ApkPackageName,
                    PackageManager.ApplicationInfoFlags.of(0)
                )
            } else {
                applicationInfo = packageManager.getApplicationInfo(ApkPackageName, 0)
            }

            if (applicationInfo != null) {
                name = packageManager.getApplicationLabel(applicationInfo) as String
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return name
    }


    private fun getAppVersion(ApkPackageName: String): String {
        var versionName = ""

        @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
        val applicationInfo: ApplicationInfo?
        val packageManager = context1.packageManager
        try {
            if (Build.VERSION.SDK_INT >= 33) {
                @Suppress("UNUSED_VALUE")
                applicationInfo = packageManager.getApplicationInfo(
                    ApkPackageName,
                    PackageManager.ApplicationInfoFlags.of(0)
                )
            } else {
                @Suppress("UNUSED_VALUE")
                applicationInfo = packageManager.getApplicationInfo(ApkPackageName, 0)
            }

            if (applicationInfo != null) {
                versionName = if (Build.VERSION.SDK_INT >= 33) {
                    packageManager.getPackageInfo(
                        ApkPackageName,
                        PackageManager.PackageInfoFlags.of(0)
                    ).versionName
                } else {
                    packageManager.getPackageInfo(ApkPackageName, 0).versionName
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return "v$versionName"
    }

    private fun getFirstInstalledDate(ApkPackageName: String): String {
        var firstInstalled = Constants.SYMBOL_HYPHEN
        val manager = context1.packageManager

        try {
            val info = if (Build.VERSION.SDK_INT >= 33) {
                manager.getPackageInfo(ApkPackageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                manager.getPackageInfo(ApkPackageName, 0)
            }

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
            val info = if (Build.VERSION.SDK_INT >= 33) {
                manager.getPackageInfo(ApkPackageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                manager.getPackageInfo(ApkPackageName, 0)
            }
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