package com.example.hiddengem.addspot

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.example.hiddengem.data.SpotRepository
import com.example.hiddengem.model.Spot

class AddSpotViewModel : ViewModel() {
    private val repo = SpotRepository()
    var error by mutableStateOf<String?>(null)

    fun save(
        editingId: String?,
        title: String, category: String, description: String, bestTime: String,
        lat: Double, lng: Double, photoUrl: String, onDone: () -> Unit
    ) {
        val user = FirebaseAuth.getInstance().currentUser
        val spot = Spot(
            id = editingId ?: "",
            title = title, category = category, description = description, bestTime = bestTime,
            latitude = lat, longitude = lng, photoUrl = photoUrl,
            createdBy = user?.uid ?: "", createdByName = user?.email ?: "someone"
        )
        if (editingId != null) repo.updateSpot(spot, onDone) { error = it }
        else repo.addSpot(spot, onDone) { error = it }
    }
}
