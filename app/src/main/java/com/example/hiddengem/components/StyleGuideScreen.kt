package com.example.hiddengem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hiddengem.model.Spot
import com.example.hiddengem.ui.theme.*

@Composable
fun StyleGuideScreen() {
    var sampleText by remember { mutableStateOf("") }
    Scaffold(
        topBar = { AppTopBar(title = "Hidden Gem — Style Guide") },
        containerColor = Paper
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(20.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            SectionTitle("Colours")
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ColorSwatch("Dusk", Dusk); ColorSwatch("Amber", Amber)
                ColorSwatch("Blue", Blue); ColorSwatch("Ink", Ink)
            }
            SectionTitle("Button")
            PrimaryButton(text = "Save spot", onClick = { })
            SectionTitle("Text field")
            AppTextField(value = sampleText, onValueChange = { sampleText = it }, label = "Spot title")
            SectionTitle("Spot card")
            SpotCard(Spot(title = "Molo Church view", category = "City"))
            SpotCard(Spot(title = "Esplanade sunset", category = "Water"))
        }
    }
}

@Composable private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Dusk)
}
@Composable private fun ColorSwatch(name: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(56.dp).clip(RoundedCornerShape(12.dp)).background(color))
        Spacer(Modifier.height(6.dp))
        Text(name, style = MaterialTheme.typography.labelSmall, color = Ink)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable private fun StyleGuidePreview() {
    HiddenGemTheme { StyleGuideScreen() }
}
