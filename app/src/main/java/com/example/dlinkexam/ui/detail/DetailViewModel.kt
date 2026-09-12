package com.example.dlinkexam.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dlinkexam.data.repository.StationRepository
import com.example.dlinkexam.domain.model.StationsResult
import com.example.dlinkexam.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class DetailViewModel @Inject constructor(
    repository: StationRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val sno: String = checkNotNull(savedStateHandle[Routes.STATION_SNO_ARG])

    val uiState: StateFlow<DetailUiState> = repository.observeStations(POLL_INTERVAL_MILLIS)
        .map { result ->
            when (result) {
                is StationsResult.Success -> {
                    val station = result.stations.find { it.sno == sno }
                    if (station != null) {
                        DetailUiState.Success(station)
                    } else {
                        DetailUiState.Error("找不到這個站點")
                    }
                }
                is StationsResult.Failure -> DetailUiState.Error(result.throwable.message ?: "載入失敗")
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = DetailUiState.Loading,
        )

    private companion object {
        const val POLL_INTERVAL_MILLIS = 60 * 1000L
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
