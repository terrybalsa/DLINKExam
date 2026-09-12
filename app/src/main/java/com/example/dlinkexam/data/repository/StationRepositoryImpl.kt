package com.example.dlinkexam.data.repository

import com.example.dlinkexam.data.remote.YouBikeApi
import com.example.dlinkexam.data.remote.toDomain
import com.example.dlinkexam.domain.model.Station
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class StationRepositoryImpl @Inject constructor(
    private val api: YouBikeApi,
) : StationRepository {

    override fun observeStations(pollIntervalMillis: Long): Flow<List<Station>> = flow {
        while (true) {
            emit(api.getStations().map { it.toDomain() })
            delay(pollIntervalMillis)
        }
    }
}
