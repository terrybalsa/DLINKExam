package com.example.dlinkexam.domain.model

sealed interface StationsResult {
    data class Success(val stations: List<Station>) : StationsResult
    data class Failure(val throwable: Throwable) : StationsResult
}
