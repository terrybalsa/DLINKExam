package com.example.dlinkexam.domain.model

data class Station(
    val sno: String,
    val name: String,
    val area: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val totalSpaces: Int,
    val availableBikes: Int,
    val availableSpaces: Int,
    val updatedAt: String,
    val sourceUpdatedAt: String,
)
