package com.example.dlinkexam.domain.model

/** One polling cycle's outcome — a failed cycle must not stop the next one from firing. */
sealed interface StationsResult {
    data class Success(val stations: List<Station>) : StationsResult
    data class Failure(val throwable: Throwable) : StationsResult
}
