package com.example.hiddengem.addspot

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun AddSpotScreen(navController: NavController, vm: AddSpotViewModel = viewModel()) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var lat by remember { mutableStateOf("10.3157") }   // Cebu default
    var lng by remember { mutableStateOf("123.8854") }

    Column(
        Modifier.fillMaxSize().statusBarsPadding().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Add a spot", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(title, { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(category, { category = it }, label = { Text("Category (nature / city / water)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(lat, { lat = it }, label = { Text("Latitude") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(lng, { lng = it }, label = { Text("Longitude") }, modifier = Modifier.fillMaxWidth())
        Button(
            onClick = { vm.save(title, category, lat.toDouble(), lng.toDouble()) { navController.popBackStack() } },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Save spot") }
        vm.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    }
}
