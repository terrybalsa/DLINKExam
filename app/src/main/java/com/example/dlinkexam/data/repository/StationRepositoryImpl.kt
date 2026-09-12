package com.example.dlinkexam.data.repository

import com.example.dlinkexam.data.remote.YouBikeApi
import com.example.dlinkexam.data.remote.toDomain
import com.example.dlinkexam.domain.model.StationsResult
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class StationRepositoryImpl @Inject constructor(
    private val api: YouBikeApi,
) : StationRepository {

    override fun observeStations(pollIntervalMillis: Long): Flow<StationsResult> = flow {
        while (true) {
            emit(fetchStations())
            delay(pollIntervalMillis)
        }
    }

    private suspend fun fetchStations(): StationsResult = try {
        StationsResult.Success(api.getStations().map { it.toDomain() })
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        StationsResult.Failure(e)
    }
}
