package com.example.dlinkexam.util

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.tan

/** Slippy-map (OSM) tile math: https://wiki.openstreetmap.org/wiki/Slippy_map_tilenames */
object GeoTileMath {

    data class TileLocation(
        val zoom: Int,
        val xTile: Int,
        val yTile: Int,
        val pixelX: Int,
        val pixelY: Int,
    )

    fun locate(lat: Double, lon: Double, zoom: Int): TileLocation {
        val n = 2.0.pow(zoom)
        val xTileF = (lon + 180.0) / 360.0 * n
        val latRad = Math.toRadians(lat)
        val yTileF = (1.0 - ln(tan(latRad) + 1.0 / cos(latRad)) / PI) / 2.0 * n

        val xTile = floor(xTileF).toInt()
        val yTile = floor(yTileF).toInt()
        val pixelX = ((xTileF - xTile) * TILE_SIZE).toInt()
        val pixelY = ((yTileF - yTile) * TILE_SIZE).toInt()

        return TileLocation(zoom, xTile, yTile, pixelX, pixelY)
    }

    const val TILE_SIZE = 256
}
