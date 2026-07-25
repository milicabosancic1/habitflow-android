package com.habitflow.app.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.TrendingDown
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.habitflow.app.data.local.RecommendationEntity
import com.habitflow.app.domain.RecommendationType

@Composable
fun RecommendationCard(
    recommendation: RecommendationEntity,
    onDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    iconFor(recommendation.type),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondary
                )
            }
            Spacer(Modifier.width(14.dp))
            Text(
                recommendation.message,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            if (onDismiss != null) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Rounded.Close, contentDescription = "Odbaci preporuku")
                }
            }
        }
    }
}

private fun iconFor(type: RecommendationType): ImageVector = when (type) {
    RecommendationType.OPTIMAL_TIME -> Icons.Rounded.Schedule
    RecommendationType.MAKE_EASIER -> Icons.AutoMirrored.Rounded.TrendingDown
    RecommendationType.STREAK_WARNING -> Icons.Rounded.LocalFireDepartment
    RecommendationType.NEW_HABIT -> Icons.Rounded.AutoAwesome
    RecommendationType.WEEKLY_INSIGHT -> Icons.Rounded.Insights
}
