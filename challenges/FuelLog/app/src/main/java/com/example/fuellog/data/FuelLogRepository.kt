package com.example.fuellog.data

import com.example.fuellog.model.FillUp
import java.time.LocalDate

interface FuelLogRepository {
    fun getFillUps(): List<FillUp>
    fun add(truckName: String, priceCents: Long, litres: Double, date: LocalDate)
    fun delete(id: Int)
}