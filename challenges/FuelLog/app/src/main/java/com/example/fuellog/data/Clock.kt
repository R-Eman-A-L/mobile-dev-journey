package com.example.fuellog.data

import java.time.LocalDate
import javax.inject.Inject

interface Clock {
    fun today(): LocalDate
}

class SystemClock @Inject constructor(): Clock {
    override fun today(): LocalDate = LocalDate.now()
}

class FakeClock(private val date: LocalDate) : Clock {
    override fun today(): LocalDate = date
}