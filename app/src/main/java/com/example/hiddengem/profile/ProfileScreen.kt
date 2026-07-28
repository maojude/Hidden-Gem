package com.example.hiddengem.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.example.hiddengem.data.SpotRepository
import com.example.hiddengem.model.Spot
import com.example.hiddengem.components.AppTopBar
import com.example.hiddengem.components.SpotCard
import com.example.hiddengem.ui.theme.Dusk
import com.example.hiddengem.ui.theme.Ink
import com.example.hiddengem.ui.theme.Paper

@Composable
fun ProfileScreen(navController: NavController) {
    val repo = remember { SpotRepository() }
    val user = FirebaseAuth.getInstance().currentUser
    var mySpots by remember { mutableStateOf<List<Spot>>(emptyList()) }
    LaunchedEffect(Unit) {
        repo.getSpots { all -> mySpots = all.filter { it.createdBy == (user?.uid ?: "") } }
    }

    Scaffold(
        topBar = { AppTopBar(title = "My Spots", showBack = true) { navController.popBackStack() } },
        containerColor = Paper
    ) { padding ->
        Column(
            Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(user?.email ?: "You", style = MaterialTheme.typography.titleMedium, color = Dusk)
            if (mySpots.isEmpty()) Text("No spots yet — tap + on the map to add your first!", color = Ink)
            else Text("${mySpots.size} spots added", color = Ink)

            mySpots.forEach { spot ->
                SpotCard(spot, onClick = { navController.navigate("spotDetail/${spot.id}") })
            }

            Spacer(Modifier.height(16.dp))
            OutlinedButton(
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("login") { popUpTo(0) }   // clear the back stack
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Log out") }
        }
    }
}