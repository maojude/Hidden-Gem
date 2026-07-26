package com.example.hiddengem.model

data class Spot(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val photoUrl: String = "",
    val bestTime: String = "",
    val createdBy: String = "",
    val createdByName: String = ""
)
