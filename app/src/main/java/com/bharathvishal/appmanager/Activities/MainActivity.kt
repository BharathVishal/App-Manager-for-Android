package com.bharathvishal.appmanager.Activities

import android.content.Context
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bharathvishal.appmanager.Adapters.ApkInformationExtractor
import com.bharathvishal.appmanager.Adapters.AppsAdapter
import com.bharathvishal.appmanager.Classes.AppInfo
import com.bharathvishal.appmanager.Classes.AppManager
import com.bharathvishal.appmanager.Constants.Constants
import com.bharathvishal.appmanager.R
import com.bharathvishal.appmanager.R.array.spinner_app_type
import com.bharathvishal.appmanager.databinding.ActivityMainBinding
import com.google.android.material.color.DynamicColors
import kotlinx.coroutines.*
import java.lang.ref.WeakReference

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

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private var adapter: AppsAdapter? = null

    private lateinit var appList: MutableList<AppInfo>
    private lateinit var appListAlternate: MutableList<AppInfo>
    private lateinit var userAppList: MutableList<AppInfo>
    private lateinit var systemAppList: MutableList<AppInfo>

    private var appManOb: AppManager? = null

    private lateinit var actvityContext: Context

    private var apkInformationExtractor: ApkInformationExtractor? = null

    private var arrAppType: Array<String>? = null
    private var recyclerViewLayoutManager: RecyclerView.LayoutManager? = null
    private var numberOfUserApps: String? = Constants.STRING_EMPTY
    private var numberOfSystemApps: String? = Constants.STRING_EMPTY

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            DynamicColors.applyToActivityIfAvailable(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        appList = ArrayList()
        arrAppType = arrayOf("User Apps", "System Apps")

        appManOb = AppManager()

        userAppList = ArrayList()
        systemAppList = ArrayList()
        appListAlternate = ArrayList()

        actvityContext = this

        val spinnerArrayAdapter = ArrayAdapter.createFromResource(
            actvityContext,
            spinner_app_type,
            R.layout.support_simple_spinner_dropdown_item
        )
        binding.spinnerAppType.adapter = spinnerArrayAdapter

        apkInformationExtractor = ApkInformationExtractor(this)
        recyclerViewLayoutManager = GridLayoutManager(actvityContext, 1)


        getApps(actvityContext)

        binding.spinnerAppType.isSelected = false
        binding.spinnerAppType.isEnabled = false

        binding.aboutLink.movementMethod = LinkMovementMethod.getInstance()
    }


    private fun getApps(context: Context) {
        val contextRef: WeakReference<Context> = WeakReference(context)

        //Coroutine
        launch(Dispatchers.Default) {
            try {
                val context1 = contextRef.get()
                appManOb = ApkInformationExtractor(context1!!).appManagerInitValues()

                if (appManOb != null) {
                    numberOfUserApps = Constants.STRING_EMPTY + appManOb!!.userAppSize
                    numberOfSystemApps = Constants.STRING_EMPTY + appManOb!!.systemAppSize

                    userAppList.addAll(appManOb!!.userApps)
                    systemAppList.addAll(appManOb!!.systemApps)

                    appListAlternate.addAll(userAppList)
                    appList.addAll(userAppList)

                    adapter = AppsAdapter(context1, appListAlternate)
                } else {

                    numberOfUserApps = Constants.STRING_EMPTY + "0"
                    numberOfSystemApps = Constants.STRING_EMPTY + "0"

                    userAppList.clear()
                    systemAppList.clear()
                    appListAlternate.clear()
                    appList.clear()

                    adapter = AppsAdapter(context1, appListAlternate)
                }

                //UI Thread
                withContext(Dispatchers.Main) {

                    binding.recyclerViewApps.layoutManager = recyclerViewLayoutManager

                    if (adapter!!.itemCount > 0) {
                        binding.recyclerViewApps.adapter = adapter
                        val text = "$numberOfUserApps User apps"
                        binding.appCounterAppManager.text = text

                        binding.spinnerAppType.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    parent: AdapterView<*>,
                                    view: View,
                                    position: Int,
                                    id: Long
                                ) {

                                    val selectedItem = parent.getItemAtPosition(position).toString()

                                    if (selectedItem == arrAppType!![0]) {
                                        //User Apps
                                        val textUser = "$numberOfUserApps User apps"
                                        binding.appCounterAppManager.text = textUser
                                        appList.clear()
                                        appList.addAll(userAppList)
                                        adapter?.updateList(userAppList)
                                    } else if (selectedItem == arrAppType!![1]) {
                                        //System Apps
                                        val textSystem = "$numberOfSystemApps System apps"
                                        binding.appCounterAppManager.text = textSystem
                                        appList.clear()
                                        appList.addAll(systemAppList)
                                        adapter?.updateList(systemAppList)
                                    }
                                } // to close the onItemSelected

                                override fun onNothingSelected(parent: AdapterView<*>) {

                                }
                            }

                        binding.spinnerAppType.isEnabled = true
                        binding.spinnerAppType.setSelection(0, true)

                    } else {
                        binding.appCounterAppManager.text = getString(R.string.No_Apps)
                        binding.appsRecyclerLayooutLl.visibility = View.GONE
                        binding.recyclerViewApps.visibility = View.GONE
                        binding.spinnerAppType.isEnabled = false
                        binding.listEmptyAppsAppmanager.visibility = View.VISIBLE
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }
}
