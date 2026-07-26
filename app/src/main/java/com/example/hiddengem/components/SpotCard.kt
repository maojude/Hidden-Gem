package com.example.hiddengem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.hiddengem.model.Spot
import com.example.hiddengem.ui.theme.Amber
import com.example.hiddengem.ui.theme.Dusk

@Composable
fun SpotCard(spot: Spot, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Card(
        modifier = modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            if (spot.photoUrl.isNotBlank()) {
                AsyncImage(
                    model = spot.photoUrl,
                    contentDescription = spot.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(56.dp).clip(RoundedCornerShape(10.dp))
                )
            } else {
                Box(
                    modifier = Modifier.size(56.dp).clip(RoundedCornerShape(10.dp)).background(Dusk),
                    contentAlignment = Alignment.Center
                ) { Text("📍", color = Color.White) }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    spot.title.ifBlank { "Untitled spot" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Dusk
                )
                if (spot.category.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Surface(color = Amber.copy(alpha = 0.15f), shape = RoundedCornerShape(50)) {
                        Text(
                            spot.category,
                            color = Amber,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}
