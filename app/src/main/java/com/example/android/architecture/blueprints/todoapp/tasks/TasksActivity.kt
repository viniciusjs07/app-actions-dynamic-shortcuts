/*
 * Copyright (C) 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.architecture.blueprints.todoapp.tasks

import android.annotation.TargetApi
import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.android.architecture.blueprints.todoapp.R
import com.google.android.material.navigation.NavigationView

/**
 * Main activity for the todoapp. Holds the Navigation Host Fragment and the Drawer, Toolbar, etc.
 */
class TasksActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val TAG = "TasksActivity"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tasks_act)
        setupNavigationDrawer()
        setSupportActionBar(findViewById(R.id.toolbar))
        createShortcut()

        // Logging for troubleshooting purposes
        logIntent(intent)

        val navController: NavController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration =
            AppBarConfiguration.Builder(R.id.tasks_fragment_dest, R.id.statistics_fragment_dest)
                .setOpenableLayout(drawerLayout)
                .build()
        setupActionBarWithNavController(navController, appBarConfiguration)
        findViewById<NavigationView>(R.id.nav_view)
            .setupWithNavController(navController)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createShortcut() {
        val shortcutManager = getSystemService(ShortcutManager::class.java)

        if (shortcutManager!!.isRequestPinShortcutSupported) {
            // Assumes there's already a shortcut with the ID "my-shortcut".
            // The shortcut must be enabled.
            val pinShortcutInfo = ShortcutInfo.Builder(applicationContext, "my-shortcut")
                .setShortLabel(applicationContext.getString(R.string.open_destination_page_static)) //set short label
                .setLongLabel(applicationContext.getString(R.string.open_destination_page_static)) //set long label
                .setDisabledMessage(applicationContext.getString(R.string.access_disabled)) //Shortcut disabled message.
                .setIcon(Icon.createWithResource(applicationContext,android.R.drawable.star_big_on)) //set icon
                .setIntent(intent)
                .build()

            // Create the PendingIntent object only if your app needs to be notified
            // that the user allowed the shortcut to be pinned. Note that, if the
            // pinning operation fails, your app isn't notified. We assume here that the
            // app has implemented a method called createShortcutResultIntent() that
            // returns a broadcast intent.
            val pinnedShortcutCallbackIntent = shortcutManager.createShortcutResultIntent(pinShortcutInfo)

            // Configure the intent so that your app's broadcast receiver gets
            // the callback successfully.For details, see PendingIntent.getBroadcast().
            val successCallback = PendingIntent.getBroadcast(applicationContext, /* request code */ 0,
                pinnedShortcutCallbackIntent, /* flags */ 0)

            shortcutManager.requestPinShortcut(pinShortcutInfo,
                successCallback.intentSender)
        }
    }

    @TargetApi(Build.VERSION_CODES.N_MR1)
    @RequiresApi(api = Build.VERSION_CODES.M)
    fun onClickPinnedShortcutBtn(view: View?) {
        val mShortcutManager: ShortcutManager = applicationContext.getSystemService(ShortcutManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //check if device supports Pin Shortcut or not
            if (mShortcutManager != null && mShortcutManager.isRequestPinShortcutSupported) {
                // I am using the dynamic shortcut ID "open_destinationPage" to create pinned shortcut.  The added shortcut ID must be added.
                val pinShortcutInfo =
                    ShortcutInfo.Builder(applicationContext, "my-shortcut").build()

                /* ***  Let's create the PendingIntent object for the app to be notified that the user allowed the shortcut to be pinned.
                   The app will not notified if the pinning operation fails, here suppose if app has implemented a method called createShortcutResultIntent() that  will return a broadcast intent.
  ****/
                val pinnedShortcutCallbackIntent =
                    mShortcutManager.createShortcutResultIntent(pinShortcutInfo)

                // Configure the intent so that your app's broadcast receiver gets
                // the callback successfully.
                val successCallback = PendingIntent.getBroadcast(
                    applicationContext, 101,
                    pinnedShortcutCallbackIntent, 0)

                //Ask the user to add the shortcut to home screen
                mShortcutManager.requestPinShortcut(
                    pinShortcutInfo,
                    successCallback.intentSender
                )
            }
        }
    }

    fun logIntent(intent: Intent) {
        val bundle: Bundle = intent.extras ?: return

        Log.d(TAG, "======= logIntent ========= %s")
        Log.d(TAG, "Logging intent data start")

        bundle.keySet().forEach { key ->
            Log.d(TAG, "[$key=${bundle.get(key)}]");
        }

        Log.d(TAG, "Logging intent data complete")
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp(appBarConfiguration) ||
            super.onSupportNavigateUp()
    }

    private fun setupNavigationDrawer() {
        drawerLayout = (findViewById<DrawerLayout>(R.id.drawer_layout))
            .apply {
                setStatusBarBackground(R.color.colorPrimaryDark)
            }
    }
}

// Keys for navigation
const val ADD_EDIT_RESULT_OK = Activity.RESULT_FIRST_USER + 1
const val DELETE_RESULT_OK = Activity.RESULT_FIRST_USER + 2
const val EDIT_RESULT_OK = Activity.RESULT_FIRST_USER + 3
