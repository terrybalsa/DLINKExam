package com.example.dlinkexam.data.remote

import com.example.dlinkexam.domain.model.Station
import com.example.dlinkexam.util.TaipeiTimeFormatter

fun StationDto.toDomain(): Station = Station(
    sno = sno,
    name = sna,
    area = sarea,
    address = ar,
    latitude = latitude,
    longitude = longitude,
    totalSpaces = quantity,
    availableBikes = availableRentBikes,
    availableSpaces = availableReturnBikes,
    updatedAt = TaipeiTimeFormatter.format(mday),
    sourceUpdatedAt = TaipeiTimeFormatter.format(srcUpdateTime),
)
