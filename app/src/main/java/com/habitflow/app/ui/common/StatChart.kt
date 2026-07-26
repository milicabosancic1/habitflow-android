package com.habitflow.app.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.habitflow.app.domain.CategoryStat
import com.habitflow.app.domain.ChartPoint
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.line.lineSpec
import com.patrykandpatrick.vico.compose.component.lineComponent
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf

/** Linijski grafikon (nedeljni/mesečni trend uspešnosti). */
@Composable
fun StatChart(points: List<ChartPoint>, modifier: Modifier = Modifier) {
    val modelProducer = remember { ChartEntryModelProducer() }
    val labels = points.map { it.label }

    LaunchedEffect(points) {
        modelProducer.setEntries(points.mapIndexed { i, p -> entryOf(i, p.successPct) })
    }

    val lineColor = MaterialTheme.colorScheme.primary
    val bottomFormatter = remember(labels) {
        AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ -> labels.getOrElse(value.toInt()) { "" } }
    }

    Chart(
        chart = lineChart(lines = listOf(lineSpec(lineColor = lineColor))),
        chartModelProducer = modelProducer,
        startAxis = rememberStartAxis(),
        bottomAxis = rememberBottomAxis(valueFormatter = bottomFormatter),
        modifier = modifier.fillMaxWidth().height(200.dp)
    )
}

/** Kolonski grafikon (uspešnost po kategorijama). */
@Composable
fun CategoryStatChart(stats: List<CategoryStat>, modifier: Modifier = Modifier) {
    val modelProducer = remember { ChartEntryModelProducer() }
    val labels = stats.map { it.category }

    LaunchedEffect(stats) {
        modelProducer.setEntries(stats.mapIndexed { i, s -> entryOf(i, s.successPct) })
    }

    val bottomFormatter = remember(labels) {
        AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ -> labels.getOrElse(value.toInt()) { "" } }
    }
    val barColor = MaterialTheme.colorScheme.primary

    Chart(
        chart = columnChart(columns = listOf(lineComponent(color = barColor, thickness = 16.dp))),
        chartModelProducer = modelProducer,
        startAxis = rememberStartAxis(),
        bottomAxis = rememberBottomAxis(valueFormatter = bottomFormatter),
        modifier = modifier.fillMaxWidth().height(200.dp)
    )
}
