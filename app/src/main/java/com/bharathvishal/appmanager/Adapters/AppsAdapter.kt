package com.bharathvishal.appmanager.Adapters

/*
  Created by Vishal on 27-12-2017.
 */


import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bharathvishal.appmanager.Classes.AppInfo
import com.bharathvishal.appmanager.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class AppsAdapter(private val context1: Context, private val appInfoList: MutableList<AppInfo>) :
    androidx.recyclerview.widget.RecyclerView.Adapter<AppsAdapter.ViewHolder>() {


    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        var imageView: ImageView = view.findViewById(R.id.imageviewAppManagerAppLogo)
        var textViewAppName: TextView = view.findViewById(R.id.Apk_Name)
        var textViewAppPackageName: TextView = view.findViewById(R.id.Apk_Package_NameFull)
        var textViewAppVersion: TextView = view.findViewById(R.id.app_VersionNameTV)
        var textViewInstalledOn: TextView = view.findViewById(R.id.installed_On_TV)
        var textViewLastUpdateed: TextView = view.findViewById(R.id.last_Updated_TV)
        var openAppDetails: Button = view.findViewById(R.id.buttontv_OpenAppDetailsBtn)
        var openApps: Button = view.findViewById(R.id.buttontv_OpenAppBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppsAdapter.ViewHolder {
        val view2 = LayoutInflater.from(context1).inflate(R.layout.card_view_layout, parent, false)
        return ViewHolder(view2)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        val applicationPackageName = appInfoList[position].appPackage

        viewHolder.textViewAppName.text = appInfoList[position].appName

        viewHolder.textViewAppPackageName.text = applicationPackageName


        val uri = appInfoList[position].appDrawableURI
        try {
            if (uri != Uri.EMPTY)
                Glide.with(context1).load(uri).apply(RequestOptions().error(R.drawable.ic_android))
                    .into(viewHolder.imageView)
            else {
                val img = ContextCompat.getDrawable(context1, R.drawable.ic_android)
                viewHolder.imageView.setImageDrawable(img)
            }
        } catch (e: Exception) {
            val img = ContextCompat.getDrawable(context1, R.drawable.ic_android)
            viewHolder.imageView.setImageDrawable(img)
        }


        viewHolder.textViewAppVersion.text = appInfoList[position].appVersion

        viewHolder.textViewInstalledOn.text = appInfoList[position].installedOn

        viewHolder.textViewLastUpdateed.text = appInfoList[position].lastUpdated

        viewHolder.openAppDetails.setOnClickListener {
            try {
                //Open the specific App Info page:
                val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:" + applicationPackageName!!)
                context1.startActivity(intent)

            } catch (e: ActivityNotFoundException) {
                //e.printStackTrace();
                val intent = Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
                context1.startActivity(intent)
            }
        }


        viewHolder.openApps.setOnClickListener {
            try {
                val intent =
                    context1.packageManager.getLaunchIntentForPackage(applicationPackageName!!)
                if (intent != null) {
                    context1.startActivity(intent)
                } else {
                    Log.d("Exception", "Exception Handled")
                }
            } catch (e: Exception) {
                Log.d("Exception", "Exception Handled")
            }
        }
    }


    override fun getItemCount(): Int {
        return appInfoList.size
    }


    @SuppressLint("NotifyDataSetChanged")
    fun updateList(items: List<AppInfo>?) {
        if (items != null) {
            if (items.isNotEmpty()) {
                appInfoList.clear()
                appInfoList.addAll(items)
                notifyDataSetChanged()
            }else {
                appInfoList.clear()
                notifyDataSetChanged()
            }
        }
    }
}