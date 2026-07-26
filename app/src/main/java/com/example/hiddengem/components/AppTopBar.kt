package com.example.hiddengem.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.hiddengem.ui.theme.Dusk

@OptIn(ExperimentalMaterial3Api::class)   // TopAppBar needs this — harmless
@Composable
fun AppTopBar(title: String, showBack: Boolean = false, onBack: () -> Unit = {}) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (showBack) IconButton(onClick = onBack) { Text("←", color = Color.White) }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Dusk,
            titleContentColor = Color.White
        )
    )
}