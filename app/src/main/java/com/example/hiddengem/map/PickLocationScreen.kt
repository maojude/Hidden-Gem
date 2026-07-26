package com.example.hiddengem.map

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.hiddengem.components.AppTopBar
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

@Composable
fun PickLocationScreen(navController: NavController) {
    val mapRef = remember { mutableStateOf<MapView?>(null) }

    Scaffold(
        topBar = { AppTopBar(title = "Pick location", showBack = true) { navController.popBackStack() } }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    MapView(context).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        controller.setZoom(15.0)
                        controller.setCenter(GeoPoint(10.3157, 123.8854)) // Cebu City
                        mapRef.value = this
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