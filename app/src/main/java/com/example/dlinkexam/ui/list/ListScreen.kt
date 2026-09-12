package com.example.dlinkexam.ui.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.stickyHeader
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dlinkexam.domain.model.Station

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ListScreen(
    onStationClick: (String) -> Unit,
    viewModel: ListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val filterQuery by viewModel.filterQuery.collectAsStateWithLifecycle()
    var showSearch by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (showSearch) {
                        TextField(
                            value = filterQuery,
                            onValueChange = viewModel::onFilterChanged,
                            placeholder = { Text("搜尋區域") },
                            singleLine = true,
                        )
                    } else {
                        Text("YouBike2.0臺北市公共自行車即時資訊")
                    }
                },
                actions = {
                    IconButton(onClick = { showSearch = !showSearch }) {
                        Icon(Icons.Default.Search, contentDescription = "過濾")
                    }
                },
            )
        },
    ) { padding ->
        when (val state = uiState) {
            is ListUiState.Loading -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }

            is ListUiState.Error -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text("無網路，請檢查網路連線")
            }

            is ListUiState.Success -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
            ) {
                state.groupedStations.forEach { (area, stations) ->
                    stickyHeader { AreaHeader(area) }
                    items(stations, key = { it.sno }) { station ->
                        StationRow(station = station, onClick = { onStationClick(station.sno) })
                    }
                }
            }
        }
    }
}

@Composable
private fun AreaHeader(area: String) {
    Surface(color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.fillMaxWidth()) {
        Text(
            text = area,
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
private fun StationRow(station: Station, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
    ) {
        Text(
            text = station.name,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
        )
        Text(text = station.address, style = MaterialTheme.typography.bodySmall)
        Text(
            text = "可租借數量全部剩餘：${station.availableBikes} / ${station.totalSpaces}",
            style = MaterialTheme.typography.bodySmall,
        )
        Text(text = station.updatedAt, style = MaterialTheme.typography.labelSmall)
    }
}
