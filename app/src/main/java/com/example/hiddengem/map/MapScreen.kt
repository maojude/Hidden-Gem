package com.example.hiddengem.map

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.hiddengem.data.SpotRepository
import com.example.hiddengem.location.LocationHelper
import com.example.hiddengem.model.Spot
import com.example.hiddengem.ui.theme.Dusk
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

// Esri ArcGIS street tiles — free, no API key, and not rate-blocked like OSM's
// volunteer servers. Note: Esri's URL order is z/y/x, not the usual z/x/y.
// Declared as a plain `val` (not private) so PickLocationScreen can reuse it.
val EsriStreets = object : OnlineTileSourceBase(
    "EsriStreets", 0, 19, 256, "",
    arrayOf("https://server.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer/tile/")
) {
    override fun getTileURLString(pMapTileIndex: Long): String =
        baseUrl +
                MapTileIndex.getZoom(pMapTileIndex) + "/" +
                MapTileIndex.getY(pMapTileIndex) + "/" +
                MapTileIndex.getX(pMapTileIndex)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navController: NavController) {
    val context = LocalContext.current
    val repo = remember { SpotRepository() }
    val locationHelper = remember { LocationHelper(context) }
    val mapRef = remember { mutableStateOf<MapView?>(null) }

    var spots by remember { mutableStateOf<List<Spot>>(emptyList()) }
    var filter by remember { mutableStateOf("All") }
    LaunchedEffect(Unit) { repo.getSpots { spots = it } }

    val categories = listOf("All", "Nature", "City", "Water")
    val shown = if (filter == "All") spots
    else spots.filter { it.category.equals(filter, ignoreCase = true) }

    fun goToMyLocation() {
        locationHelper.currentLocation(
            onResult = { lat, lng -> mapRef.value?.controller?.animateTo(GeoPoint(lat, lng)) },
            onFail = {}
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> if (granted) goToMyLocation() }
    fun onLocateClick() {
        val granted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (granted) goToMyLocation()
        else permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hidden Gem") },
                actions = {
                    TextButton(onClick = { navController.navigate("profile") }) {
                        Text("Profile", color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Dusk, titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SmallFloatingActionButton(onClick = { onLocateClick() }) { Text("◎") }   // locate me
                FloatingActionButton(onClick = { navController.navigate("pickLocation") }) { Text("+") }
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            // Map is the bottom layer
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    MapView(ctx).apply {
                        setTileSource(EsriStreets)
                        setMultiTouchControls(true)
                        controller.setZoom(13.0)
                        controller.setCenter(GeoPoint(10.3157, 123.8854))
                        mapRef.value = this
                    }
                },
                update = { map ->
                    map.overlays.clear()
                    shown.forEach { spot ->
                        val marker = Marker(map)
                        marker.position = GeoPoint(spot.latitude, spot.longitude)
                        marker.title = spot.title
                        marker.setOnMarkerClickListener { _, _ ->
                            navController.navigate("spotDetail/${spot.id}"); true
                        }
                        map.overlays.add(marker)
                    }
                    map.invalidate()
                }
            )
            // Chips float on top, pinned to the top of the screen
            Row(
                Modifier
                    .align(Alignment.TopStart)
                    .horizontalScroll(rememberScrollState())
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { c ->
                    FilterChip(selected = filter == c, onClick = { filter = c }, label = { Text(c) })
                }
            }
        }
    }
}