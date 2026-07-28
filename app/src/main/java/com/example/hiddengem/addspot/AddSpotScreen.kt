package com.example.hiddengem.addspot

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import com.example.hiddengem.data.StorageRepository
import com.example.hiddengem.map.PickedLocation
import com.example.hiddengem.util.LightTimes

@Composable
fun AddSpotScreen(navController: NavController, vm: AddSpotViewModel = viewModel()) {
    val repo = remember { SpotRepository() }
    val storageRepo = remember { StorageRepository() }
    var formError by remember { mutableStateOf<String?>(null) }
    var uploading by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var bestTime by remember { mutableStateOf("") }
    var lat by remember { mutableStateOf("10.3157") }   // Cebu default
    var lng by remember { mutableStateOf("123.8854") }
    var photoUrl by remember { mutableStateOf("") }
    var editingId by remember { mutableStateOf<String?>(null) }

    // Gallery picker: on pick, upload the image and store the returned URL.
    val pickImage = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            uploading = true
            formError = null
            storageRepo.uploadSpotPhoto(
                uri,
                onDone = { url -> photoUrl = url; uploading = false },
                onError = { formError = it; uploading = false }
            )
        }
    }

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

        Text("Photo", style = MaterialTheme.typography.labelLarge)
        Button(
            onClick = {
                pickImage.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            enabled = !uploading,
            modifier = Modifier.fillMaxWidth()
        ) { Text(if (uploading) "Uploading…" else if (photoUrl.isBlank()) "Pick a photo" else "Change photo") }

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
                    uploading                            -> "Please wait for the photo to finish uploading"
                    latValue == null || lngValue == null -> "Location looks invalid"
                    else                                 -> null
                }
                if (formError == null) {
                    vm.save(editingId, title, category, description, bestTime, latValue!!, lngValue!!, photoUrl) {
                        navController.popBackStack("map", false)
                    }
                }
            },
            enabled = !uploading,
            modifier = Modifier.fillMaxWidth()
        ) { Text(if (editingId == null) "Save spot" else "Save changes") }
        formError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        vm.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    }
}