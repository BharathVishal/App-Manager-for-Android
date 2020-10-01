package com.bharathvishal.appmanager

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.*
import com.bharathvishal.appmanager.Adapters.ApkInfoExtractor
import com.bharathvishal.appmanager.Adapters.AppsAdapter
import com.bharathvishal.appmanager.Classes.AppInfo
import com.bharathvishal.appmanager.Classes.AppManager
import com.bharathvishal.appmanager.Constants.Constants
import com.bharathvishal.appmanager.R.array.spinner_app_type
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private var adapter: AppsAdapter? = null

    internal lateinit var appList: MutableList<AppInfo>
    private lateinit var appListAlternate: MutableList<AppInfo>
    internal lateinit var userAppList: MutableList<AppInfo>
    internal lateinit var systemAppList: MutableList<AppInfo>
    internal lateinit var displayList: MutableList<AppInfo>


    private var appManOb: AppManager? = null

    private lateinit var actvityContext: Context

    private var apkInfoExtractor: ApkInfoExtractor? = null

    internal var arrAppType: Array<String>? = null
    private var recyclerViewLayoutManager: RecyclerView.LayoutManager? = null
    internal var numberOfUserApps: String? = Constants.STRING_EMPTY
    internal var numberOfSystemApps: String? = Constants.STRING_EMPTY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appList = ArrayList()
        arrAppType = arrayOf("User Apps", "System Apps")

        appManOb = AppManager()

        userAppList = ArrayList()
        systemAppList = ArrayList()
        appListAlternate = ArrayList()
        displayList = ArrayList()

        actvityContext = this

        val spinnerArrayAdapter = ArrayAdapter.createFromResource(actvityContext, spinner_app_type, R.layout.support_simple_spinner_dropdown_item)
        spinner_App_Type.adapter = spinnerArrayAdapter

        apkInfoExtractor = ApkInfoExtractor(this)
        recyclerViewLayoutManager = GridLayoutManager(actvityContext, 1)


        getApps(actvityContext)

        spinner_App_Type.isSelected = false
        spinner_App_Type.isEnabled = false

    }


    fun getApps(context: Context) {
        val contextRef: WeakReference<Context> = WeakReference(context)

        //Coroutine
        launch(Dispatchers.Default) {
            try {
                val context1 = contextRef.get()

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

                    adapter = AppsAdapter(context, displayList)
                }

                //UI Thread
                withContext(Dispatchers.Main) {

                    recycler_view_Apps.layoutManager = recyclerViewLayoutManager

                    if (adapter!!.itemCount > 0) {
                        recycler_view_Apps.adapter = adapter
                        val text = "$numberOfUserApps User apps"
                        app_Counter_App_Manager.text = text

                        spinner_App_Type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                                val selectedItem = parent.getItemAtPosition(position).toString()

                                if (selectedItem == arrAppType!![0]) {
                                    //User Apps
                                    val textUser = "$numberOfUserApps User apps"
                                    app_Counter_App_Manager.text = textUser
                                    appList.clear()
                                    displayList.clear()
                                    appList.addAll(userAppList)
                                    displayList.addAll(appList)
                                    adapter?.updateList(displayList)
                                } else if (selectedItem == arrAppType!![1]) {
                                    //System Apps
                                    val textSystem = "$numberOfSystemApps System apps"
                                    app_Counter_App_Manager.text = textSystem
                                    appList.clear()
                                    displayList.clear()
                                    appList.addAll(systemAppList)
                                    displayList.addAll(appList)
                                    adapter?.updateList(displayList)
                                }
                            } // to close the onItemSelected

                            override fun onNothingSelected(parent: AdapterView<*>) {

                            }
                        }

                        spinner_App_Type.isEnabled = true
                        spinner_App_Type.setSelection(0, true)

                    } else {
                        app_Counter_App_Manager.text = getString(R.string.No_Apps)
                        apps_recycler_layoout_ll.visibility = View.GONE
                        recycler_view_Apps.visibility = View.GONE
                        spinner_App_Type.isEnabled = false
                        list_empty_Apps_Appmanager.visibility = View.VISIBLE
                    }

                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.search_app, menu)

        val menuItem = menu?.findItem(R.id.search)

        if (menuItem != null) {
            val searchView = menuItem.actionView as androidx.appcompat.widget.SearchView

            searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(p0: String?): Boolean {

                    if (!p0!!.isEmpty()) {
                        displayList.clear()
                        val search = p0.toLowerCase(Locale.ROOT)
                        appList.forEach {
                            if (it.appName?.toLowerCase(Locale.ROOT)?.contains(search)!!) {
                                displayList.add(it)
                            }

                        }
                        adapter?.updateList(displayList)
                    } else {
                        displayList.clear()
                        displayList.addAll(appList)
                        adapter?.updateList(displayList)
                    }
                    return true
                }


            })
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return super.onOptionsItemSelected(item)
    }
}
