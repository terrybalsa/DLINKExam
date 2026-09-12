package com.example.dlinkexam.data.remote

import retrofit2.http.GET

interface YouBikeApi {
    @GET("dotapp/youbike/v2/youbike_immediate.json")
    suspend fun getStations(): List<StationDto>
}
