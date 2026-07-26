package com.example.hiddengem.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.hiddengem.data.SpotRepository
import com.example.hiddengem.model.Spot
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun MapScreen(navController: NavController) {
    val repo = remember { SpotRepository() }
    var spots by remember { mutableStateOf<List<Spot>>(emptyList()) }
    LaunchedEffect(Unit) { repo.getSpots { spots = it } }   // live updates

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("addSpot") }) { Text("+") }
        }
    ) { padding ->
        AndroidView(
            modifier = Modifier.fillMaxSize().padding(padding),
            factory = { context ->
                MapView(context).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    controller.setZoom(13.0)
                    controller.setCenter(GeoPoint(10.3157, 123.8854)) // Cebu City
                }
            },
            update = { map ->
                map.overlays.clear()
                spots.forEach { spot ->
                    val marker = Marker(map)
                    marker.position = GeoPoint(spot.latitude, spot.longitude)
                    marker.title = spot.title
                    map.overlays.add(marker)
                }
                map.invalidate()
            }
        )
    }
}