package com.example.tipsplit

data class TipSplitUiState(
    val billText: String = "",
    val tipPercent: Int = 15,
    val people: Int = 1,
    val tip: Double? = null,
    val total: Double? = null,
    val perPersonTotal: Double? = null,
    val errorMessage: String? = null,
)