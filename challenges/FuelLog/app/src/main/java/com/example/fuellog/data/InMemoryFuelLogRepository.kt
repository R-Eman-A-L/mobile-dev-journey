package com.example.fuellog.data

import com.example.fuellog.model.FillUp
import jakarta.inject.Inject
import java.time.LocalDate

class InMemoryFuelLogRepository @Inject constructor() : FuelLogRepository {
    private var fillUps: List<FillUp> = listOf(
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
        ),
        FillUp(
            id = 4,
            truckName = "Ford F-150",
            priceCents = 9875,
            litres = 61.2,
            date = LocalDate.of(2025, 12, 30) // Dec 30, 2025 (Oldest - tests year boundary)
        ),
        FillUp(
            id = 5,
            truckName = "Kenworth T680",
            priceCents = 51025,
            litres = 325.8,
            date = LocalDate.of(2026, 11, 25) // Nov 25, 2026 (Same day as ID 2)
        ),
        FillUp(
            id = 6,
            truckName = "RAM 2500",
            priceCents = 14210,
            litres = 89.0,
            date = LocalDate.of(2026, 11, 2)  // Nov 2, 2026
        )
    )
    private var nextId = (fillUps.maxOfOrNull { it.id } ?: 0) + 1

    override fun getFillUps() = fillUps

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
        fillUps = fillUps.filter{it.id != id}
    }
}