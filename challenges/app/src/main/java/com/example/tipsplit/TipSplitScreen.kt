package com.example.tipsplit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tipsplit.ui.theme.TipSplitTheme
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipSplitScreen(
    viewModel: TipSplitViewModel = viewModel(),
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val tipOptions = listOf(10, 15, 20)

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Tip Split") })
        }
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            OutlinedTextField(
                value = state.billText,
                onValueChange = {
                    viewModel.onBillChanged(it)
                },
                label = { Text(text = "Add bill amount") },
                isError = state.errorMessage != null,
                supportingText = { state.errorMessage?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                modifier = Modifier.padding(8.dp)

            )

            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.padding(8.dp)
            ) {
                tipOptions.forEachIndexed { index, tip ->
                    SegmentedButton(
                        onClick = {
                            viewModel.onTipSelected(tip)

                        },
                        selected = state.tipPercent == tip,
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = tipOptions.size
                        )
                    ) {
                        Text(text = "$tip%")
                    }
                }
            }
            Row(
                modifier = Modifier.padding(8.dp)
            ) {
                Button(
                    onClick = { viewModel.onRemovePerson() },
                    enabled = state.people > 1
                ) {
                    Text(text = "-")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "${state.people}", modifier = Modifier.padding(8.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { viewModel.onAddPerson() }
                ) {
                    Text(text = "+")
                }
            }

            Column {
                Text(text = "Total bill amount: ${formatAmount(state.total)}")
                Text(text = "Tip: ${formatAmount(state.tip)}")
                Text(text = "Per person total: ${formatAmount(state.perPersonTotal)}")
            }
        }
    }
}

private fun formatAmount(amount: Double?): String {
    if (amount == null) return "—"

    return String.format(Locale.US, "$%.2f", amount)
}

@Preview(showBackground = true)
@Composable
fun TipSplitPreview() {
    TipSplitTheme {
        TipSplitScreen()
    }
}