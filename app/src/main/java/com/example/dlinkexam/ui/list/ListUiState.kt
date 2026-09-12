package com.example.dlinkexam.ui.list

import com.example.dlinkexam.domain.model.Station

sealed interface ListUiState {
    data object Loading : ListUiState
    data class Success(val groupedStations: Map<String, List<Station>>) : ListUiState
    data class Error(val message: String) : ListUiState
}
