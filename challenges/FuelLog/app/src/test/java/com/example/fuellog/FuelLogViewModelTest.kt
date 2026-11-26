package com.example.fuellog

import com.example.fuellog.data.FakeClock
import com.example.fuellog.data.FakeFuelLogRepository
import com.example.fuellog.model.FillUp
import com.example.fuellog.ui.FuelLog.FuelLogViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate


class FuelLogViewModelTest {


    private lateinit var fuelLogRepository: FakeFuelLogRepository
    private lateinit var clock: FakeClock
    private lateinit var viewModel: FuelLogViewModel


    @Before
    fun setUp() {
        val fillUps = listOf(
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

        fuelLogRepository = FakeFuelLogRepository(fillUps)
        clock = FakeClock(LocalDate.of(2026, 11, 26))
        viewModel = FuelLogViewModel(fuelLogRepository = fuelLogRepository, clock = clock)

    }

    @Test
    fun `Adding a valid fill up adds to the fill ups list and return true`(){
        val result = viewModel.onAdd("Truck A", "100.00", "15.00")
        val fillUps = viewModel.state.value.fillUps

        assertTrue(result)
        assertEquals(4, fillUps.size)
        assertTrue(fillUps.any { it.truckName == "Truck A" })
    }

    @Test
    fun `The price and litres are accepted with a comma`(){
        val result = viewModel.onAdd("Truck A", "100,01", "15,01")
        val fillUps = viewModel.state.value.fillUps
        val added = fillUps.find { it.truckName == "Truck A" }!!

        assertTrue(result)
        assertNull(viewModel.state.value.errorMessage)
        assertEquals(10001L, added.priceCents)
        assertEquals(15.01, added.litres, 0.0001 )

    }

    @Test
    fun `Deleting a fill up log remove the exact item from the list`(){
        viewModel.onDelete(3)
        val fillUps = viewModel.state.value.fillUps

        assertEquals(2, fillUps.size)
        assertFalse(fillUps.any { it.truckName == "Volvo VNL" })
        assertFalse(fillUps.any { it.id == 3 })
    }

    @Test
    fun `The monthly total for a given month give back the right amount`(){
        val repository = FakeFuelLogRepository(listOf(
            FillUp(1, "A", 4000, 40.0, LocalDate.of(2026, 11, 3)),
            FillUp(2, "B", 2500, 25.0, LocalDate.of(2026, 10, 30)),
            FillUp(3, "C", 9900, 99.0, LocalDate.of(2025, 11, 10))
        ))
        val viewModel = FuelLogViewModel(fuelLogRepository = repository, clock = clock)

        val monthlyTotalCents = viewModel.state.value.totalMonthlyCents

        assertEquals(4000L,monthlyTotalCents)
    }

    @Test
    fun `The average litre per cent return the exact value`(){
        val repository = FakeFuelLogRepository(listOf(
            FillUp(1, "A", 2000, 10.0, LocalDate.of(2026, 11, 3)),   // counts
            FillUp(2, "B", 10000, 100.0, LocalDate.of(2026, 10, 30)),  // other month // same month, OTHER YEAR
        ))

        val viewModel = FuelLogViewModel(fuelLogRepository = repository, clock = clock)

        val avgCentsPerLitre = viewModel.state.value.avgCentsPerLitre!!

        assertEquals(109.0909, avgCentsPerLitre,0.0001)
    }


}