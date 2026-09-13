package com.example.dlinkexam.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dlinkexam.data.repository.StationRepository
import com.example.dlinkexam.domain.model.StationsResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class ListViewModel @Inject constructor(
    repository: StationRepository,
) : ViewModel() {

    private val _filterQuery = MutableStateFlow("")
    val filterQuery: StateFlow<String> = _filterQuery.asStateFlow()

    val uiState: StateFlow<ListUiState> = combine(
        repository.observeStations(POLL_INTERVAL_MILLIS),
        _filterQuery.debounce(FILTER_DEBOUNCE_MILLIS),
    ) { result, query ->
        when (result) {
            is StationsResult.Success -> {
                val filtered = if (query.isBlank()) {
                    result.stations
                } else {
                    result.stations.filter { it.area.contains(query, ignoreCase = true) }
                }
                ListUiState.Success(filtered.groupBy { it.area })
            }
            is StationsResult.Failure -> ListUiState.Error(result.throwable.message ?: "載入失敗")
        }
    }.flowOn(Dispatchers.Default).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = ListUiState.Loading,
    )

    fun onFilterChanged(query: String) {
        _filterQuery.value = query
    }

    private companion object {
        const val POLL_INTERVAL_MILLIS = 3 * 60 * 1000L
        const val STOP_TIMEOUT_MILLIS = 5_000L
        const val FILTER_DEBOUNCE_MILLIS = 150L
    }
}
