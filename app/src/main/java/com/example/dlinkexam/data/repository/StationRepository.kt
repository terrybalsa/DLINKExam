package com.example.dlinkexam.data.repository

import com.example.dlinkexam.domain.model.StationsResult
import kotlinx.coroutines.flow.Flow

interface StationRepository {
    /**
     * Emits a [StationsResult] immediately, then again every [pollIntervalMillis].
     * A failed cycle emits [StationsResult.Failure] but polling continues.
     */
    fun observeStations(pollIntervalMillis: Long): Flow<StationsResult>
}
