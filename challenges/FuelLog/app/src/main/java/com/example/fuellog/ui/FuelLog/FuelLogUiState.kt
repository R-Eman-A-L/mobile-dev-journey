package com.example.fuellog.ui.FuelLog

import com.example.fuellog.model.FillUp

data class FuelLogUiState(
    val fillUps: List<FillUp> = emptyList(),
    val totalMonthlyCents: Long = 0,
    val avgCentsPerLitre: Double? = null,
    val errorMessage: String? = null,
)