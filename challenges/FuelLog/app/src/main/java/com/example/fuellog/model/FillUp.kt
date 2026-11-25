package com.example.fuellog.model

import java.time.LocalDate

data class FillUp(
    val id: Int,
    val truckName: String,
    val priceCents: Long,
    val litres: Double,
    val date: LocalDate
)