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
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()               // must be the first line
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // osmdroid must be configured before any map is shown.
        Configuration.getInstance().load(
            applicationContext,
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )
        Configuration.getInstance().userAgentValue = packageName  // avoids blank map tiles

        setContent {
            HiddenGemTheme { AppNavigation() }
        }
    }
}