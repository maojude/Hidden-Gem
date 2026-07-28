package com.example.hiddengem.addspot

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.hiddengem.data.SpotRepository
import com.example.hiddengem.map.PickedLocation
import com.example.hiddengem.util.LightTimes

//test justine commit
@Composable
fun AddSpotScreen(navController: NavController, vm: AddSpotViewModel = viewModel()) {
    val repo = remember { SpotRepository() }
    var formError by remember { mutableStateOf<String?>(null) }
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var bestTime by remember { mutableStateOf("") }
    var lat by remember { mutableStateOf("10.3157") }   // Cebu default
    var lng by remember { mutableStateOf("123.8854") }
    var photoUrl by remember { mutableStateOf("") }
    var editingId by remember { mutableStateOf<String?>(null) }

    // On open: EDIT an existing spot, or take the map-picked location for a NEW spot.
    LaunchedEffect(Unit) {
        val id = EditingSpot.id
        EditingSpot.id = null
        if (id != null) {
            editingId = id
            repo.getSpot(id) { s ->
                if (s != null) {
                    title = s.title; category = s.category; description = s.description
                    bestTime = s.bestTime; photoUrl = s.photoUrl
                    lat = s.latitude.toString(); lng = s.longitude.toString()
                }
            }
        } else {
            PickedLocation.lat?.let { lat = it.toString() }
            PickedLocation.lng?.let { lng = it.toString() }
            PickedLocation.lat = null; PickedLocation.lng = null
        }
    }

    Column(
        Modifier.fillMaxSize().statusBarsPadding().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(if (editingId == null) "Add a spot" else "Edit spot", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(title, { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())

        Text("Category", style = MaterialTheme.typography.labelLarge)
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Nature", "City", "Water").forEach { option ->
                FilterChip(selected = category == option, onClick = { category = option }, label = { Text(option) })
            }
        }

        OutlinedTextField(
            description, { description = it },
            label = { Text("Description (what makes it special?)") },
            modifier = Modifier.fillMaxWidth(), minLines = 2
        )

        Text("Best light", style = MaterialTheme.typography.labelLarge)
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            LightTimes.options.forEach { option ->
                FilterChip(selected = bestTime == option, onClick = { bestTime = option }, label = { Text(option) })
            }
        }

        OutlinedTextField(photoUrl, { photoUrl = it }, label = { Text("Photo URL (paste an image link)") }, modifier = Modifier.fillMaxWidth())
        if (photoUrl.isNotBlank()) {
            AsyncImage(
                model = photoUrl, contentDescription = null, contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(12.dp))
            )
        }

        OutlinedTextField(lat, { lat = it }, label = { Text("Latitude") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(lng, { lng = it }, label = { Text("Longitude") }, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                val latValue = lat.toDoubleOrNull()
                val lngValue = lng.toDoubleOrNull()
                formError = when {
                    title.isBlank()                      -> "Please add a title"
                    category.isBlank()                   -> "Please pick a category"
                    latValue == null || lngValue == null -> "Location looks invalid"
                    else                                 -> null
                }
                if (formError == null) {
                    vm.save(editingId, title, category, description, bestTime, latValue!!, lngValue!!, photoUrl) {
                        navController.popBackStack("map", false)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text(if (editingId == null) "Save spot" else "Save changes") }
        formError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        vm.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    }
}
