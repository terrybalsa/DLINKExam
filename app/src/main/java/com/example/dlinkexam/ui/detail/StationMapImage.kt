package com.example.dlinkexam.ui.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.dlinkexam.util.GeoTileMath

private const val ZOOM = 16
private val MARKER_SIZE = 24.dp

/** A single raw OSM tile (no built-in marker support) with a pin overlaid at the exact pixel. */
@Composable
fun StationMapImage(latitude: Double, longitude: Double, modifier: Modifier = Modifier) {
    val location = remember(latitude, longitude) {
        GeoTileMath.locate(latitude, longitude, ZOOM)
    }
    val tileUrl = "https://tile.openstreetmap.org/${location.zoom}/${location.xTile}/${location.yTile}.png"

    Box(modifier = modifier.size(GeoTileMath.TILE_SIZE.dp)) {
        AsyncImage(
            model = tileUrl,
            contentDescription = "站點位置地圖",
            modifier = Modifier.size(GeoTileMath.TILE_SIZE.dp),
        )
        Icon(
            imageVector = Icons.Default.Place,
            contentDescription = null,
            tint = Color.Red,
            modifier = Modifier
                .size(MARKER_SIZE)
                .offset(
                    x = (location.pixelX.dp - MARKER_SIZE / 2),
                    y = (location.pixelY.dp - MARKER_SIZE),
                ),
        )
    }
}
