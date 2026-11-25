package com.example.fuellog.ui.FuelLog

import androidx.lifecycle.ViewModel
import com.example.fuellog.data.Clock
import com.example.fuellog.data.FuelLogRepository
import com.example.fuellog.model.FillUp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import javax.inject.Inject
import kotlin.math.roundToLong

@HiltViewModel
class FuelLogViewModel @Inject constructor(
    private val fuelLogRepository: FuelLogRepository,
    private val clock: Clock
) : ViewModel() {
    private val _state = MutableStateFlow(FuelLogUiState())
    val state: StateFlow<FuelLogUiState> = _state.asStateFlow()


    init {
        refresh()
    }


    fun onAdd(truckNameText: String, priceText: String, litresText: String): Boolean {
        val truckNameTextTrim = truckNameText.trim()
        val priceText = priceText.replace(',', '.')
        val litresText = litresText.replace(',', '.')
        val price = priceText.toDoubleOrNull()
        val litres = litresText.toDoubleOrNull()



        if (truckNameTextTrim.isEmpty()) {
            refresh("Enter a valid truck name")
            return false
        }

        if (price == null || price <= 0) {
            refresh("Enter a valid price")
            return false
        }

        if (litres == null || litres <= 0) {
            refresh("Enter a valid litre amount")
            return false
        }

        val priceInCents = (price * 100).roundToLong()
        fuelLogRepository.add(truckNameTextTrim, priceInCents, litres, clock.today())
        refresh()
        return true
    }

    fun onDelete(id: Int) {
        fuelLogRepository.delete(id)
        refresh()
    }

    fun onTextChanged() {
        _state.update { it.copy(errorMessage = null) }
    }

    private fun refresh(error: String? = null) {
        val fillUps = fuelLogRepository.getFillUps().sortedWith(
            compareByDescending<FillUp> { it.date }.thenByDescending { it.id }
        )
        val today = clock.today()
        val totalMonthlyCents = totalMonthlyCents(fillUps, today)
        val avgCentsPerLitre = avgCentsPerLitre(fillUps)
        _state.update {
            it.copy(
                fillUps = fillUps,
                totalMonthlyCents = totalMonthlyCents,
                avgCentsPerLitre = avgCentsPerLitre,
                errorMessage = error
            )
        }
    }

    private fun totalMonthlyCents(fillUps: List<FillUp>, today: LocalDate): Long =
        fillUps.filter { it.date.year == today.year && it.date.month == today.month }
            .sumOf { it.priceCents }

    private fun avgCentsPerLitre(fillUps: List<FillUp>): Double? {
        val totalLitres = fillUps.sumOf { it.litres }

        if (totalLitres == 0.0) return null

        val totalPriceCents = fillUps.sumOf { it.priceCents }

        return totalPriceCents / totalLitres
    }

}

