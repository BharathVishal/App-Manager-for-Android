package com.bharathvishal.appmanager.Activities

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import coil.compose.rememberAsyncImagePainter
import com.bharathvishal.appmanager.Adapters.ApkInformationExtractor
import com.bharathvishal.appmanager.Classes.AppInfo
import com.bharathvishal.appmanager.Classes.AppManager
import com.bharathvishal.appmanager.Constants.Constants
import com.bharathvishal.appmanager.R
import com.bharathvishal.appmanager.theme.Material3AppTheme
import com.google.android.material.color.DynamicColors
import kotlinx.coroutines.*
import java.lang.ref.WeakReference

class MainActivityCompose : AppCompatActivity(), CoroutineScope by MainScope() {
    //private lateinit var appList: MutableList<AppInfo>

    private var appList = SnapshotStateList<AppInfo>()

    private lateinit var appListAlternate: MutableList<AppInfo>
    private lateinit var userAppList: MutableList<AppInfo>
    private lateinit var systemAppList: MutableList<AppInfo>

    private var appManOb: AppManager? = null

    private var appLaunchedNow = true

    private lateinit var activityContext: Context

    private var apkInformationExtractor: ApkInformationExtractor? = null

    private var arrAppType: Array<String>? = null
    private var numberOfUserApps: String? = Constants.STRING_EMPTY
    private var numberOfSystemApps: String? = Constants.STRING_EMPTY

    private val appTypeOptions = listOf("User Apps", "System Apps")
    private var expandedBoolval = mutableStateOf(false)
    private var selectedOptionTextVal = mutableStateOf(appTypeOptions[0])
    private var numberOfAppsVal = mutableStateOf("0 apps")

    private var showSnackBarVal = mutableStateOf(false)
    private var snackBarMessageVal = mutableStateOf("-")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Applies Material dynamic theming
        try {
            DynamicColors.applyToActivityIfAvailable(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        appList = SnapshotStateList()
        arrAppType = arrayOf("User Apps", "System Apps")

        appManOb = AppManager()

        userAppList = ArrayList()
        systemAppList = ArrayList()
        appListAlternate = ArrayList()

        appLaunchedNow = true

        activityContext = this

        setContent {
            Material3AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainViewImplementation()
                }
            }
        }

        apkInformationExtractor = ApkInformationExtractor(this)

        getApps(activityContext, false, selectedOptionTextVal.value)
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun MainViewImplementation() {
        Column {
            TopAppBarMain()
            Box(
                modifier = Modifier
                    .padding(1.dp)
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                CardViewMain()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .align(Alignment.BottomStart),
                ) {
                    SnackBarViewComposable(showSnackBarVal.value, snackBarMessageVal.value)
                }
            }
        }
    }

    //Top App bar composable function
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TopAppBarMain() {
        TopAppBar(
            title = { Text("App Manager  - Compose Activity") },
            colors = TopAppBarDefaults.topAppBarColors(
                titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                containerColor = MaterialTheme.colorScheme.surface
            )
        )
    }

    //CardView composable function
    @Suppress("UNNECESSARY_SAFE_CALL")
    @Composable
    fun CardViewMain() {
        Column {
            Spacer(modifier = Modifier.padding(top = 6.dp))
            Card(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(),
                colors = CardDefaults.cardColors(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                )
                {
                    TextLicenseHeader()

                    DropDownListAppsType()
                    CountOfAppsComposeView(numberOfAppsVal.value)
                    Spacer(modifier = Modifier.padding(top = 4.dp))
                    LazyColumnView()

                }//end of column
            }//end of card
        }//end of outer column
    }//end of card view main


    @Composable
    fun TextLicenseHeader() {
        val text1 = "© 2023. "
        val text2 = "Open Source Software"
        val text3 = " licensed with Apache-2.0 license."
        val annotatedString = buildAnnotatedString {
            append(text1)
            append(text2)
            addStyle(
                style = SpanStyle(
                    color = Color(0xff63a4ff),
                    textDecoration = TextDecoration.Underline
                ), start = 8, end = 28
            )
            addStringAnnotation(
                tag = "URL",
                annotation = "https://github.com/BharathVishal/App-Manager-Android",
                start = 0,
                end = text2.length
            )
            append(text3)
        }

        val uriHandlerTemp = LocalUriHandler.current

        ClickableText(
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth(),
            text = annotatedString,
            style = MaterialTheme.typography.labelSmall +
                    TextStyle(
                        textAlign = TextAlign.Center
                    ),
            onClick = {
                annotatedString
                    .getStringAnnotations("URL", it, it)
                    .firstOrNull()?.let { stringAnnotation ->
                        uriHandlerTemp.openUri(stringAnnotation.item)
                    }
            },
        )
    }

    @Composable
    fun DropDownListAppsType() {
        var textfieldSize by remember { mutableStateOf(Size.Zero) }

        val icon = if (expandedBoolval.value)
            Icons.Filled.ArrowDropUp //it requires androidx.compose.material:material-icons-extended
        else
            Icons.Filled.ArrowDropDown

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            OutlinedButton(
                onClick = {
                    expandedBoolval.value = !expandedBoolval.value
                },
                shape = RoundedCornerShape(10), // = 50% percent
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .onGloballyPositioned { coordinates ->
                        //This value is used to assign to the DropDown the same width
                        textfieldSize = coordinates.size.toSize()
                    }
            ) {
                Text(
                    selectedOptionTextVal.value,
                    textAlign = TextAlign.Start
                )
                Icon(imageVector = icon, contentDescription = "icon")
            }

            DropdownMenu(
                expanded = expandedBoolval.value,
                onDismissRequest = { expandedBoolval.value = false },
                modifier = Modifier.width(with(LocalDensity.current) { textfieldSize.width.toDp() })
            ) {
                appTypeOptions.forEach { label ->
                    DropdownMenuItem(onClick = {
                        selectedOptionTextVal.value = label
                        expandedBoolval.value = false

                        if (label == "User Apps") {
                            appList.clear()
                            appList.addAll(userAppList)
                            numberOfAppsVal.value = "$numberOfUserApps User apps"
                        } else {
                            appList.clear()
                            appList.addAll(systemAppList)
                            numberOfAppsVal.value = "$numberOfSystemApps System apps"
                        }
                    },
                        text = { Text(text = label) }
                    )
                }
            }//end of drop down menu
        }//end of column
    }

    @Composable
    fun CountOfAppsComposeView(str: String) {
        OutlinedButton(
            onClick = {},
            shape = RoundedCornerShape(10),
            border = BorderStroke(0.dp, Color.Transparent),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Icon(painter = painterResource(R.drawable.ic_apps), contentDescription = "icon")
            Text(
                str,
                textAlign = TextAlign.Center
            )
        }
    }

    @Suppress("SENSELESS_COMPARISON")
    @Composable
    fun LazyColumnView() {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            contentPadding = PaddingValues(1.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            if (appList != null) {
                items(items = appList) {
                     item -> if (item != null)
                        ComposableCardViewApp(
                            item.appName!!,
                            item.appPackage!!,
                            item.installedOn!!,
                            item.lastUpdated!!,
                            item.appVersion!!,
                            item.appDrawableURI!!
                        )
                }
            }
        }
    }


    @Composable
    fun ComposableCardViewApp(
        appName: String,
        appPackageName1: String,
        installed: String,
        updateddate: String,
        versionStr: String,
        uriOfImage: Uri
    ) {
        Card(
            modifier = Modifier
                .padding(1.dp, 1.dp, 1.dp, 8.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 1.dp
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(9.dp, 1.dp, 1.dp, 1.dp)
            ) {
                //Coil library async image loading with coroutines
                val painterTemp = rememberAsyncImagePainter(uriOfImage)

                Image(
                    painter = painterTemp,
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .padding(1.dp),
                    contentScale = ContentScale.Fit
                )
                Column(Modifier.padding(4.dp)) {
                    Text(
                        text = appName,
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2
                    )
                    Text(
                        text = appPackageName1,
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 2
                    )
                    Text(
                        text = installed,
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.labelSmall,
                    )
                    Text(
                        text = updateddate,
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.labelSmall,
                    )
                    Text(
                        text = versionStr,
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.labelMedium,
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(1.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = {
                                try {
                                    val intent =
                                        activityContext.packageManager.getLaunchIntentForPackage(
                                            appPackageName1
                                        )
                                    if (intent != null) {
                                        activityContext.startActivity(intent)
                                    } else {
                                        Log.d("Exception", "Exception Handled")
                                    }
                                } catch (e: Exception) {
                                    Log.d("Exception", "Exception Handled")
                                }
                            },
                            modifier = Modifier
                                .padding(1.dp)
                                .wrapContentHeight(),
                            enabled = true
                        ) {
                            Text(text = "Launch")
                        }

                        OutlinedButton(
                            onClick = {
                                try {
                                    //Open the specific App Info page:
                                    val intent =
                                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    intent.data = Uri.parse("package:$appPackageName1")
                                    activityContext.startActivity(intent)

                                } catch (e: ActivityNotFoundException) {
                                    //e.printStackTrace();
                                    val intent =
                                        Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
                                    activityContext.startActivity(intent)
                                }
                            },
                            modifier = Modifier
                                .wrapContentWidth()
                                .padding(1.dp)
                                .wrapContentHeight(),
                            enabled = true
                        ) {
                            Text(text = "Details")
                        }
                    }
                }
            }
        }
    }


    @Composable
    fun SnackBarViewComposable(visibilityState: Boolean, message: String) {
        AnimatedVisibility(visible = visibilityState) {
            Snackbar(action = {}) {
                Text(text = message)
            }
        }
    }

    private fun getApps(context: Context, isCalledOnResume: Boolean, typeOfAppsToShow: String) {
        val contextRef: WeakReference<Context> = WeakReference(context)

        //Coroutine
        launch(Dispatchers.Default) {
            try {
                val context1 = contextRef.get()
                appManOb = ApkInformationExtractor(context1!!).appManagerInitValues()

                if (appManOb != null) {
                    numberOfUserApps = Constants.STRING_EMPTY + appManOb!!.userAppSize
                    numberOfSystemApps = Constants.STRING_EMPTY + appManOb!!.systemAppSize

                    appListAlternate.clear()
                    userAppList.clear()
                    systemAppList.clear()

                    userAppList.addAll(appManOb!!.userApps)
                    systemAppList.addAll(appManOb!!.systemApps)
                    appListAlternate.addAll(userAppList)
                } else {

                    numberOfUserApps = Constants.STRING_EMPTY + "0"
                    numberOfSystemApps = Constants.STRING_EMPTY + "0"

                    userAppList.clear()
                    systemAppList.clear()
                    appListAlternate.clear()
                }

                //UI Thread
                withContext(Dispatchers.Main) {
                    //Runs on OnCreate
                    if (!isCalledOnResume) {
                        numberOfAppsVal.value = "$numberOfUserApps User apps"

                        //Always show user apps list ny default oncreate
                        appList.clear()
                        appList.addAll(appListAlternate)
                    }


                    //Runs for onResume
                    if (isCalledOnResume) {
                        if (typeOfAppsToShow == "User Apps") {
                            numberOfAppsVal.value = "$numberOfUserApps User apps"
                            selectedOptionTextVal.value = "User Apps"
                            appList.clear()
                            appList.addAll(appListAlternate)
                        } else {
                            numberOfAppsVal.value = "$numberOfSystemApps System apps"
                            selectedOptionTextVal.value = "System Apps"
                            appList.clear()
                            appList = SnapshotStateList<AppInfo>()
                            appList.addAll(systemAppList)
                        }
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }


    override fun onResume() {
        super.onResume()

        if (appLaunchedNow) {
            appLaunchedNow = false
            Log.d("onres1", "Executed first time")
        } else {
            //Refresh and reload the apps
            if (activityContext != null) {
                getApps(activityContext, true, selectedOptionTextVal.value)

                Log.d("onres1", "Executed on Resume")
                Log.d("onres1", "selected option val$selectedOptionTextVal")
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()

        //Cancels this coroutine score
        cancel()
    }

    //Preview for jetpack composable view
    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        Material3AppTheme {
            MainViewImplementation()
            //ComposableCardViewApp("App new", "com.com.com", "Installed on 12/10/2014", "Updated on 24/12/2022", "v1.1", Uri.EMPTY)
        }
    }
}