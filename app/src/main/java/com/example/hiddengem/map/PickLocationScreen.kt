package com.example.hiddengem.map

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.hiddengem.location.LocationHelper
import com.example.hiddengem.components.AppTopBar
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

@Composable
fun PickLocationScreen(navController: NavController) {
    val context = LocalContext.current
    val mapRef = remember { mutableStateOf<MapView?>(null) }
    val locationHelper = remember { LocationHelper(context) }
    var target by remember { mutableStateOf<GeoPoint?>(null) }   // remember where to center

    fun goToMyLocation() {
        locationHelper.currentLocation(
            onResult = { lat, lng ->
                val p = GeoPoint(lat, lng)
                target = p
                mapRef.value?.controller?.setCenter(p)
            },
            onFail = { /* keep the Cebu fallback */ }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> if (granted) goToMyLocation() }

    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (granted) goToMyLocation()
        else permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    Scaffold(
        topBar = { AppTopBar(title = "Pick location", showBack = true) { navController.popBackStack() } }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    MapView(ctx).apply {
                        setTileSource(EsriStreets)                     // shared from MapScreen.kt
                        controller.setZoom(16.0)
                        controller.setCenter(GeoPoint(10.3157, 123.8854)) // Cebu fallback
                        mapRef.value = this
                        target?.let { controller.setCenter(it) }
                        setMultiTouchControls(true)                     // enables pinch-to-zoom (if not already there)
                        zoomController.setVisibility(
                            org.osmdroid.views.CustomZoomButtonsController.Visibility.NEVER
                        )
                    }
                }
            )
            Text("✛", style = MaterialTheme.typography.displaySmall, modifier = Modifier.align(Alignment.Center))
            Button(
                onClick = {
                    val center = mapRef.value?.mapCenter
                    if (center != null) {
                        PickedLocation.lat = center.latitude
                        PickedLocation.lng = center.longitude
                    }
                    navController.navigate("addSpot")
                },
                modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp).fillMaxWidth()
            ) { Text("Use this location") }
        }
    }
}