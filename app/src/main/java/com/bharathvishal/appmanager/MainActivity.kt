package com.bharathvishal.appmanager

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.*
import com.bharathvishal.appmanager.Adapters.ApkInfoExtractor
import com.bharathvishal.appmanager.Adapters.AppsAdapter
import com.bharathvishal.appmanager.Classes.AppInfo
import com.bharathvishal.appmanager.Classes.AppManager
import com.bharathvishal.appmanager.Constants.Constants
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*


class MainActivity : AppCompatActivity() {
    private var adapter: AppsAdapter? = null
    internal lateinit var spinner: Spinner
    internal lateinit var numOfApps: TextView


    internal lateinit var appList: MutableList<AppInfo>
    private lateinit var appListAlternate: MutableList<AppInfo>
    internal lateinit var userAppList: MutableList<AppInfo>
    internal lateinit var systemAppList: MutableList<AppInfo>


    private lateinit var listEmptyApps: TextView
    private lateinit var appManagerRecyclerLayout: LinearLayout

    private var appManOb: AppManager? = null

    private var actvityContext: Context? = null

    private lateinit var apkInfoExtractor: ApkInfoExtractor


    internal lateinit var arrAppType: Array<String>
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewLayoutManager: RecyclerView.LayoutManager
    internal lateinit var numberOfUserApps: String
    internal lateinit var numberOfSystemApps: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appList = ArrayList()
        spinner = findViewById(R.id.spinner_App_Type)
        arrAppType = arrayOf("User Apps", "System Apps")
        numOfApps = findViewById(R.id.app_Counter_App_Manager)
        recyclerView = findViewById(R.id.recycler_view_Apps)

        appManOb = AppManager()

        userAppList = ArrayList()
        systemAppList = ArrayList()
        appListAlternate = ArrayList()

        actvityContext = this

        val spinnerArrayAdapter = ArrayAdapter.createFromResource(actvityContext, R.array.spinner_app_type, R.layout.support_simple_spinner_dropdown_item)
        spinner.adapter = spinnerArrayAdapter

        apkInfoExtractor = ApkInfoExtractor(this)
        recyclerViewLayoutManager = GridLayoutManager(actvityContext, 1)


        getApps(actvityContext as MainActivity)

        listEmptyApps = findViewById(R.id.list_empty_Apps_Appmanager)
        appManagerRecyclerLayout = findViewById(R.id.apps_recycler_layoout_ll)

        spinner.isSelected = false
        spinner.isEnabled = false

    }


    private fun getApps(context: Context) {

        doAsync {
            appManOb = ApkInfoExtractor(context).appManagerInitValues()

            if (appManOb != null) {
                numberOfUserApps = Constants.STRING_EMPTY + appManOb!!.userAppSize
                numberOfSystemApps = Constants.STRING_EMPTY + appManOb!!.systemAppSize

                userAppList.addAll(appManOb!!.userApps)
                systemAppList.addAll(appManOb!!.systemApps)

                appListAlternate.addAll(userAppList)
                appList.addAll(userAppList)

                adapter = AppsAdapter(context, appListAlternate)
            } else {

                numberOfUserApps = Constants.STRING_EMPTY + "0"
                numberOfSystemApps = Constants.STRING_EMPTY + "0"

                userAppList.clear()
                systemAppList.clear()
                appListAlternate.clear()
                appList.clear()

                adapter = AppsAdapter(context, appListAlternate)
            }

            uiThread {
                recyclerView.layoutManager = recyclerViewLayoutManager

                if (adapter!!.itemCount > 0) {
                    recyclerView.adapter = adapter
                    val text = "$numberOfUserApps User apps"
                    numOfApps.text = text

                    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                            val selectedItem = parent.getItemAtPosition(position).toString()

                            if (selectedItem == arrAppType[0]) {
                                //User Apps
                                val textUser = "$numberOfUserApps User apps"
                                numOfApps.text = textUser
                                appList.clear()
                                appList.addAll(userAppList)
                                adapter?.updateList(userAppList)
                            } else if (selectedItem == arrAppType[1]) {
                                //System Apps
                                val textSystem = "$numberOfSystemApps System apps"
                                numOfApps.text = textSystem
                                appList.clear()
                                appList.addAll(systemAppList)
                                adapter?.updateList(systemAppList)
                            }
                        } // to close the onItemSelected

                        override fun onNothingSelected(parent: AdapterView<*>) {

                        }
                    }

                    spinner.isEnabled = true
                    spinner.setSelection(0, true)

                } else {
                    numOfApps.text = getString(R.string.No_Apps)
                    appManagerRecyclerLayout.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    spinner.isEnabled = false
                    listEmptyApps.visibility = View.VISIBLE
                }
            }
        }
    }

}
