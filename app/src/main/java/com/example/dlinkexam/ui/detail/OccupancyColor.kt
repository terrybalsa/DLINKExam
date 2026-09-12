package com.example.dlinkexam.ui.detail

import androidx.compose.ui.graphics.Color

/** Per spec.md 4.2: <20% red, 20%~50% (inclusive) yellow, >50% green. */
fun occupancyColor(available: Int, total: Int): Color {
    if (total <= 0) return Color.Gray
    val ratio = available.toDouble() / total
    return when {
        ratio < 0.2 -> Color(0xFFD32F2F)
        ratio <= 0.5 -> Color(0xFFF9A825)
        else -> Color(0xFF2E7D32)
    }
}
