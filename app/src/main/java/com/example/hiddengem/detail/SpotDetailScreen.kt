package com.example.hiddengem.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.example.hiddengem.data.SpotRepository
import com.example.hiddengem.model.Spot
import com.example.hiddengem.components.AppTopBar
import com.example.hiddengem.ui.theme.Amber
import com.example.hiddengem.ui.theme.Dusk
import com.example.hiddengem.ui.theme.Ink
import com.example.hiddengem.ui.theme.Paper
import com.example.hiddengem.util.LightTimes

@Composable
fun SpotDetailScreen(navController: NavController, spotId: String) {
    val repo = remember { SpotRepository() }
    var spot by remember { mutableStateOf<Spot?>(null) }
    LaunchedEffect(spotId) { repo.getSpot(spotId) { spot = it } }

    Scaffold(
        topBar = { AppTopBar(title = "Spot", showBack = true) { navController.popBackStack() } },
        containerColor = Paper
    ) { padding ->
        val s = spot
        if (s == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(Modifier.padding(padding).verticalScroll(rememberScrollState())) {
                if (s.photoUrl.isNotBlank()) {
                    AsyncImage(
                        model = s.photoUrl, contentDescription = s.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth().height(240.dp)
                    )
                }
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(s.title.ifBlank { "Untitled spot" }, style = MaterialTheme.typography.headlineSmall, color = Dusk)

                    if (s.category.isNotBlank()) {
                        Surface(color = Amber.copy(alpha = 0.15f), shape = RoundedCornerShape(50)) {
                            Text(s.category, color = Amber, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
                        }
                    }

                    // Best-light panel — LightTimes turns the tag into today's clock time.
                    Surface(color = Dusk, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "🌅  " + LightTimes.sentence(s.bestTime, s.latitude, s.longitude),
                            color = Color.White, modifier = Modifier.padding(16.dp)
                        )
                    }

                    if (s.description.isNotBlank()) Text(s.description, color = Ink)
                    Text("Added by ${s.createdByName}", style = MaterialTheme.typography.labelMedium, color = Ink)

                    // Owner-only delete (Jude)
                    if (s.createdBy == FirebaseAuth.getInstance().currentUser?.uid) {
                        OutlinedButton(
                            onClick = { repo.deleteSpot(s.id, onDone = { navController.popBackStack() }, onError = {}) },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Delete this spot") }
                    }
                }
            }
        }
    }
}