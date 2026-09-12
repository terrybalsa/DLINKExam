package com.example.dlinkexam.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Raw API response shape for a single YouBike2.0 station.
 * Field names/casing must match the source API exactly (see spec.md 2.1) —
 * notably "Quantity" is capitalized, unlike every other field.
 */
@Serializable
data class StationDto(
    val sno: String,
    val sna: String,
    val sarea: String,
    val ar: String,
    val latitude: Double,
    val longitude: Double,
    @SerialName("Quantity") val quantity: Int,
    @SerialName("available_rent_bikes") val availableRentBikes: Int,
    @SerialName("available_return_bikes") val availableReturnBikes: Int,
    val mday: String,
    val srcUpdateTime: String,
)
