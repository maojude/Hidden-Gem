package com.example.hiddengem.data

import com.google.firebase.firestore.FirebaseFirestore
import com.example.hiddengem.model.Spot

class SpotRepository {
    private val spots = FirebaseFirestore.getInstance().collection("spots")

    fun addSpot(spot: Spot, onDone: () -> Unit, onError: (String) -> Unit) {
        spots.add(spot)
            .addOnSuccessListener { onDone() }
            .addOnFailureListener { onError(it.message ?: "Save failed") }
    }

    // Live updates: fires again whenever anyone adds/edits/deletes a spot.
    fun getSpots(onResult: (List<Spot>) -> Unit) {
        spots.addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                onResult(snapshot.documents.mapNotNull {
                    it.toObject(Spot::class.java)?.copy(id = it.id)
                })
            }
        }
    }

    fun getSpot(id: String, onResult: (Spot?) -> Unit) {
        spots.document(id).get()
            .addOnSuccessListener { doc -> onResult(doc.toObject(Spot::class.java)?.copy(id = doc.id)) }
            .addOnFailureListener { onResult(null) }
    }

    fun deleteSpot(id: String, onDone: () -> Unit, onError: (String) -> Unit) {
        spots.document(id).delete()
            .addOnSuccessListener { onDone() }
            .addOnFailureListener { onError(it.message ?: "Delete failed") }
    }

    fun updateSpot(spot: Spot, onDone: () -> Unit, onError: (String) -> Unit) {
        spots.document(spot.id).set(spot)
            .addOnSuccessListener { onDone() }
            .addOnFailureListener { onError(it.message ?: "Update failed") }
    }
}