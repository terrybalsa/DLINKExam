package com.example.dlinkexam.data.repository

import com.example.dlinkexam.domain.model.Station
import kotlinx.coroutines.flow.Flow

interface StationRepository {
    /** Emits the full station list immediately, then again every [pollIntervalMillis]. */
    fun observeStations(pollIntervalMillis: Long): Flow<List<Station>>
}
