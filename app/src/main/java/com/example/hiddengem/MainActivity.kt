package com.example.hiddengem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.hiddengem.navigation.AppNavigation
import com.example.hiddengem.ui.theme.HiddenGemTheme
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {
    //Initial Android Call
    override fun onCreate(savedInstanceState: Bundle?) {

        //Display Splash Screen
        installSplashScreen()

        //parent call to initialize/setup Android first
        super.onCreate(savedInstanceState)

        //draw app underneath system status bar
        enableEdgeToEdge()

        // osmdroid must be configured before any map is shown.
        Configuration.getInstance().load(
            applicationContext,
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )

        // lets the map server know this app is requesting the tiles
        Configuration.getInstance().userAgentValue = packageName  // avoids blank map tiles

        // app handles screen edges
        androidx.core.view.WindowCompat.setDecorFitsSystemWindows(window, false)

        // startup/shows ui
        setContent {
            HiddenGemTheme { AppNavigation() }
        }
    }
}