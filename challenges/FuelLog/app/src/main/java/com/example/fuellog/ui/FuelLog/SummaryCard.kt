package com.example.fuellog.ui.FuelLog

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.Locale

@Composable
fun SummaryCard(
    state: FuelLogUiState,
    modifier: Modifier = Modifier
) {
    val formattedMonthlyTotal = state.totalMonthlyCents
        .takeIf { it > 0L }
        ?.let { "$${String.format(Locale.getDefault(), "%.2f", it / 100.0)}" }
        ?: "$0.00"

    val formattedAvg = state.avgCentsPerLitre
        ?.takeIf { it > 0.0 } // Change to 0L if avgCentsPerLitre is a Long
        ?.let { "$${String.format(Locale.getDefault(), "%.2f", it / 100.0)}/L" }
        ?: "—"

    ElevatedCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Metric: Monthly Spend
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Spent This Month",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formattedMonthlyTotal,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Subtle Vertical Divider
            VerticalDivider(
                modifier = Modifier
                    .height(40.dp)
                    .padding(horizontal = 16.dp)
            )

            // Right Metric: Average Price / Litre
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Avg. Price / Litre",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formattedAvg,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}