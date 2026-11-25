package com.example.tipsplit

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


/*
// ===== 1. THE TICKET IN MY OWN WORDS =====
// The user types a bill, picks a tip and the number of people, and instantly sees
// the tip, the total and what each person pays.

// ===== 2. UNKNOWNS =====
//   - how to turn text into a number safely, without crashing on "abc"
//   - how to show a number with 2 decimals
//   - how to make a button look selected
//   - how to show the number keyboard (KeyboardType.Decimal)
//   Decisions:
//   - empty bill → results "—", NO error (ticket owner, Oct 23)
//   - comma → accept it: "84,50" is treated as "84.50" (many Québec keyboards type commas)

// ===== 3. DO I NEED AN INTERFACE? =====
// No. Nothing is saved or comes from outside (no reach out), there's no second way
// to do the job (no swap out), and the math can be tested directly (no fake out).

// ===== 4. UISTATE =====
// Inputs (what the user chose):
//   - billText        | text            | ""    | changes on every keystroke
//   - tipPercent      | whole number    | 15    | changes when a tip button is tapped
//   - people          | whole number    | 1     | changes with − and +
// Results (what the brain calculates):
//   - tip             | number or null  | null  | recalculated after every change
//   - total           | number or null  | null  | recalculated after every change
//   - perPerson       | number or null  | null  | recalculated after every change
//   - error           | text or null    | null  | appears for letters, 0 or negative
// (null = the screen shows "—")

// ===== 5. SCREEN-ONLY STATE =====
//   - none: the brain needs every input to calculate, and all of it must survive rotation

// ===== 6. EVENTS =====
//   - type in the bill field → onBillChanged(text)
//   - tap a tip button       → onTipSelected(percent)
//   - tap +                  → onAddPerson()
//   - tap −                  → onRemovePerson()

// ===== 7. STEPS =====
// refresh()  (helper)
// Goal: the box has the current inputs and results (or an error) that match them.
// Have: the current box
//   1. read billText, tipPercent and people from the current box
//   2. replace a comma with a dot, and trim
//   3. if the text is empty: results = null, error = null, put the box, STOP
//   4. turn the text into a number safely
//   5. if it isn't a number, or it's 0 or less: results = null,
//      error = "Enter an amount above 0", put the box, STOP
//   6. tip = bill × tipPercent ÷ 100
//   7. total = bill + tip
//   8. perPerson = total ÷ people
//   9. build a NEW box with the inputs, the three results and error = null, and put it in the state
//
// onBillChanged(text)
// Goal: the box holds exactly what was typed, and the results (or error) match it.
// Have: the text
//   1. copy the box with billText = text
//   2. call refresh
//
// onTipSelected(percent)
// Goal: the chosen tip is selected and the results use it.
// Have: the percent
//   1. copy the box with tipPercent = percent
//   2. call refresh
//
// onAddPerson()
// Goal: one more person, and the results use it.
// Have: the current box
//   1. copy the box with people + 1
//   2. call refresh
//
// onRemovePerson()
// Goal: one person fewer, but never below 1.
// Have: the current box
//   1. if people is already 1, STOP (nothing to do)
//   2. copy the box with people − 1
//   3. call refresh

// ===== 8. THE SCREEN =====
// Reads:
//   - state.billText → the field's text (number keyboard)
//   - state.error    → under the field
//   - state.tipPercent → which of the 3 buttons looks selected
//   - state.people   → the number between − and +
//   - state.tip, state.total, state.perPerson → shown as "$12.68", or "—" when null
// Calls:
//   - typing → onBillChanged(text)
//   - 10% / 15% / 20% → onTipSelected(10 / 15 / 20)
//   - + → onAddPerson(), − → onRemovePerson()
// (Formatting "$12.68" / "—" is the screen's job: it's about showing, not deciding.)

// ===== 9. EDGE CASES =====
//   - empty or only spaces → "—", no error
//   - letters (pasted)     → "Enter an amount above 0", "—"
//   - "0"                  → "Enter an amount above 0", "—"
//   - negative (pasted)    → "Enter an amount above 0", "—"
//   - "1.2.3" or "."       → not a number → error, "—"
//   - "84,50"              → treated as 84.50
//   - − at 1 person        → stays at 1, no error
//   - rotate               → everything stays (the box lives in the ViewModel)

// ===== 10. TEST PLAN (one line per criterion) =====
//   1. type 84.50              → the field shows 84.50
//   2. app starts              → 15% selected; tap 20% → only 20% looks selected
//   3. tap − at 1              → stays 1; tap + twice → 3
//   4. 100, 15%, 2 people      → tip $15.00, total $115.00, per person $57.50;
//                                 tap 20% → $20.00, $120.00, $60.00 right away
//   5. empty → "—", no error;  "abc" (pasted) → error + "—";  "0" → error + "—"
//   6. 50, 15%, 3 people       → tip $7.50, total $57.50, per person $19.17
//   7. 100, 20%, 2 people, rotate → still $20.00, $120.00, $60.00, and 20% still selected
*/
class TipSplitViewModel : ViewModel() {

    private val _state = MutableStateFlow(TipSplitUiState())
    val state: StateFlow<TipSplitUiState> = _state.asStateFlow()

    private fun refresh() {
        val billText = state.value.billText
        val tipPercent = state.value.tipPercent
        val people = state.value.people

        val cleanBillText = billText.replace(",", ".").trim()

        if (cleanBillText.isEmpty()) {
            showNoResults()
            return
        }
        val billAmount = cleanBillText.toDoubleOrNull()

        if (billAmount == null) {
            showNoResults("Enter a valid bill amount")
            return
        }

        if (billAmount <= 0) {
            showNoResults("Enter an amount above 0")
            return
        }

        val tip = billAmount * tipPercent / 100
        val total = billAmount + tip
        val perPersonTotal = total / people

        _state.value = _state.value.copy(
            tip = tip,
            total = total,
            perPersonTotal = perPersonTotal,
            errorMessage = null
        )
    }

    private fun showNoResults(errorMessage: String? = null) {
        _state.value = _state.value.copy(
            tip = null,
            total = null,
            perPersonTotal = null,
            errorMessage = errorMessage
        )
    }


    fun onBillChanged(bill: String) {
        _state.value = _state.value.copy(billText = bill)
        refresh()
    }

    fun onTipSelected(tip: Int) {
        _state.value = _state.value.copy(tipPercent = tip)
        refresh()
    }

    fun onAddPerson() {
        val people = state.value.people
        _state.value = _state.value.copy(people = people + 1)
        refresh()
    }

    fun onRemovePerson() {
        val people = state.value.people

        if (people == 1) return

        _state.value = _state.value.copy(people = people - 1)

        refresh()
    }

}