package com.example.dlinkexam.navigation

object Routes {
    const val STATION_SNO_ARG = "sno"

    const val LIST = "list"
    const val DETAIL = "detail/{$STATION_SNO_ARG}"

    fun detail(sno: String) = "detail/$sno"
}
