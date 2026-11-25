package com.example.fuellog.data

import com.example.fuellog.model.FillUp
import java.time.LocalDate

class FakeFuelLogRepository: FuelLogRepository {
    override fun getFillUps():List<FillUp> {
        TODO("Not yet implemented")
    }

    override fun add(
        truckName: String,
        priceCents: Long,
        litres: Double,
        date: LocalDate
    ) {
        TODO("Not yet implemented")
    }

    override fun delete(id: Int) {
        TODO("Not yet implemented")
    }
}