package com.example.fuellog.data

import com.example.fuellog.model.FillUp
import java.time.LocalDate

class FakeFuelLogRepository(
    fillUps: List<FillUp> = listOf(
        FillUp(
            id = 1,
            truckName = "Ford F-150",
            priceCents = 11540,
            litres = 72.5,
            date = LocalDate.of(2026, 11, 18) // Nov 18, 2026
        ),
        FillUp(
            id = 2,
            truckName = "Freightliner Cascadia",
            priceCents = 48590,
            litres = 310.0,
            date = LocalDate.of(2026, 11, 25) // Nov 25, 2026 (Newest)
        ),
        FillUp(
            id = 3,
            truckName = "Volvo VNL",
            priceCents = 41200,
            litres = 265.4,
            date = LocalDate.of(2026, 10, 5)  // Oct 5, 2026
        )
    )
) : FuelLogRepository {

    private var fillUps = fillUps.toList()

    override fun getFillUps(): List<FillUp> = fillUps
    private var nextId = (fillUps.maxOfOrNull { it.id } ?: 0) + 1

    override fun add(
        truckName: String,
        priceCents: Long,
        litres: Double,
        date: LocalDate
    ) {
        val id = nextId++
        fillUps = fillUps + FillUp(id, truckName, priceCents, litres, date)
    }

    override fun delete(id: Int) {
        fillUps = fillUps.filter { it.id != id }
    }
}