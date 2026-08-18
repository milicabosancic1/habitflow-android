package com.habitflow.app.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitflow.app.R
import com.habitflow.app.ui.theme.PeachAccent
import com.habitflow.app.ui.theme.SageContainer
import com.habitflow.app.ui.theme.SagePrimary
import com.habitflow.app.ui.theme.WarmBackground
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

private const val RING_PROGRESS = 0.65f

/**
 * Brendirani splash koji se prikazuje odmah posle sistemskog (Android SplashScreen API
 * podržava samo ikonicu, ne i tekst). Ikonica se NE animira ovde - ona je već vidljiva
 * sa sistemskog splash-a, pa bi ponovni fade/scale delovao kao "dva različita ekrana".
 * Samo se natpis "HabitFlow" pojavljuje ispod nje.
 *
 * Prsten se crta direktno preko Canvas-a (drawArc), a ne kao VectorDrawable, jer sistemski
 * splash renderer nepouzdano iscrtava stroke-ovane vector putanje - Canvas nema taj problem
 * i ovde daje bogatiju verziju (staza + napredak + "danas" tačka) nego ic_splash_logo.xml.
 */
@Composable
fun BrandedSplashScreen(modifier: Modifier = Modifier) {
    val textAlpha = remember { Animatable(0f) }
    val textOffsetY = remember { Animatable(10f) }

    LaunchedEffect(Unit) {
        launch { textAlpha.animateTo(1f, tween(380, easing = FastOutSlowInEasing)) }
        launch { textOffsetY.animateTo(0f, tween(380, easing = FastOutSlowInEasing)) }
    }

    Box(
        modifier = modifier.fillMaxSize().background(WarmBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(modifier = Modifier.size(132.dp), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = size.minDimension * 0.08f
                    val arcDiameter = size.minDimension - strokeWidth
                    val topLeft = Offset(
                        (size.width - arcDiameter) / 2f,
                        (size.height - arcDiameter) / 2f
                    )
                    val arcSize = androidx.compose.ui.geometry.Size(arcDiameter, arcDiameter)

                    // Staza (puna, svetlija)
                    drawArc(
                        color = SageContainer,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                    // Napredak (65%, tamnija)
                    val sweep = 360f * RING_PROGRESS
                    drawArc(
                        color = SagePrimary,
                        startAngle = -90f,
                        sweepAngle = sweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                    // "Danas" tačka na kraju luka
                    val endAngleRad = Math.toRadians((-90f + sweep).toDouble())
                    val radius = arcDiameter / 2f
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val dotCenter = Offset(
                        x = center.x + radius * cos(endAngleRad).toFloat(),
                        y = center.y + radius * sin(endAngleRad).toFloat()
                    )
                    drawCircle(color = PeachAccent, radius = strokeWidth * 0.6f, center = dotCenter)
                }
                Image(
                    painter = painterResource(R.drawable.ic_sprout),
                    contentDescription = null,
                    modifier = Modifier.size(76.dp)
                )
            }
            Column(
                modifier = Modifier.graphicsLayer {
                    alpha = textAlpha.value
                    translationY = textOffsetY.value.dp.toPx()
                },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row {
                    Text(
                        "Habit",
                        fontSize = 27.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.3).sp,
                        color = SagePrimary
                    )
                    Text(
                        "Flow",
                        fontSize = 27.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.3).sp,
                        color = PeachAccent
                    )
                }
                Text(
                    "Male navike, velike promene.",
                    style = MaterialTheme.typography.labelMedium,
                    color = SagePrimary.copy(alpha = 0.7f)
                )
            }
        }
    }
}
