package com.habitflow.app.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material.icons.rounded.Whatshot
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.habitflow.app.domain.AchievementType
import java.time.Instant
import java.time.ZoneId

@Composable
fun AchievementBadge(
    type: AchievementType,
    unlocked: Boolean,
    unlockedAt: Long?,
    modifier: Modifier = Modifier
) {
    val (name, description) = copyFor(type)

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (unlocked) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
        border = if (unlocked) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (unlocked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outlineVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    iconFor(type),
                    contentDescription = null,
                    tint = if (unlocked) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (!unlocked) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Rounded.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(name, style = MaterialTheme.typography.titleSmall, textAlign = TextAlign.Center)
            Spacer(Modifier.height(4.dp))
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            if (unlocked && unlockedAt != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    "Otključano ${formatUnlockedAt(unlockedAt)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private val MONTH_NAMES = listOf(
    "januar", "februar", "mart", "april", "maj", "jun",
    "jul", "avgust", "septembar", "oktobar", "novembar", "decembar"
)

private fun formatUnlockedAt(millis: Long): String {
    val date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
    return "${date.dayOfMonth}. ${MONTH_NAMES[date.monthValue - 1]} ${date.year}."
}

private fun copyFor(type: AchievementType): Pair<String, String> = when (type) {
    AchievementType.FIRST_HABIT -> "Prvi korak" to "Kreirala si svoju prvu naviku."
    AchievementType.STREAK_7 -> "Nedelja upornosti" to "7 dana zaredom bez preskakanja."
    AchievementType.STREAK_30 -> "Mesec discipline" to "30 dana zaredom — navika je postala deo tebe."
    AchievementType.PERFECT_WEEK -> "Savršena nedelja" to "Sve navike završene svaki dan ove nedelje."
    AchievementType.COMEBACK -> "Povratak u formu" to "Vratila si se nakon pauze — nastavak je najvažniji."
}

private fun iconFor(type: AchievementType): ImageVector = when (type) {
    AchievementType.FIRST_HABIT -> Icons.Rounded.Flag
    AchievementType.STREAK_7 -> Icons.Rounded.LocalFireDepartment
    AchievementType.STREAK_30 -> Icons.Rounded.Whatshot
    AchievementType.PERFECT_WEEK -> Icons.Rounded.EmojiEvents
    AchievementType.COMEBACK -> Icons.Rounded.Replay
}
