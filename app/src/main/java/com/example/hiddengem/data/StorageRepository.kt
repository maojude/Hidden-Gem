package com.example.hiddengem.data

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

class StorageRepository {
    private val storage = FirebaseStorage.getInstance().reference

    // Uploads the picked image, then hands back its public download URL.
    fun uploadSpotPhoto(uri: Uri, onDone: (String) -> Unit, onError: (String) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: "anon"
        val ref = storage.child("spot_photos/$uid/${System.currentTimeMillis()}.jpg")
        ref.putFile(uri)
            .addOnSuccessListener {
                ref.downloadUrl
                    .addOnSuccessListener { url -> onDone(url.toString()) }
                    .addOnFailureListener { e -> onError(e.message ?: "Could not get photo URL") }
            }
            .addOnFailureListener { e -> onError(e.message ?: "Upload failed") }
    }
}