package com.example.dlinkexam.ui.detail

import com.example.dlinkexam.domain.model.Station

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Success(val station: Station) : DetailUiState
    data class Error(val message: String) : DetailUiState
}
