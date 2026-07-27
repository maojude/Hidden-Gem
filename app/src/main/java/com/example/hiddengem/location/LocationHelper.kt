package com.example.hiddengem.location

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

class LocationHelper(context: Context) {
    private val client = LocationServices.getFusedLocationProviderClient(context)

    // Caller must check/request permission BEFORE calling this.
    @SuppressLint("MissingPermission")
    fun currentLocation(onResult: (Double, Double) -> Unit, onFail: () -> Unit) {
        client.lastLocation
            .addOnSuccessListener { last ->
                if (last != null) {
                    onResult(last.latitude, last.longitude)
                } else {
                    client.getCurrentLocation(
                        Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                        CancellationTokenSource().token
                    ).addOnSuccessListener { loc ->
                        if (loc != null) onResult(loc.latitude, loc.longitude) else onFail()
                    }.addOnFailureListener { onFail() }
                }
            }
            .addOnFailureListener { onFail() }
    }
}