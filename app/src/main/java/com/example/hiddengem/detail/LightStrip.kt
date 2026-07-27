package com.example.hiddengem.detail

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.hiddengem.ui.theme.Amber
import com.example.hiddengem.ui.theme.Dusk
import com.example.hiddengem.util.LightTimes

@Composable
fun LightStrip(lat: Double, lng: Double) {
    val moments = LightTimes.dayMoments(lat, lng)
    if (moments.isEmpty()) return
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Today's light", style = MaterialTheme.typography.labelLarge, color = Dusk)
        Row(
            Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            moments.forEach { m ->
                Surface(color = Dusk, shape = RoundedCornerShape(12.dp)) {
                    Column(
                        Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(m.label, color = Amber, style = MaterialTheme.typography.labelSmall)
                        Text(
                            m.time,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}