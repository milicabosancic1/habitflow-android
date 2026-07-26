package com.habitflow.app.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.EmojiObjects
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun OnboardingScreen(viewModel: OnboardingViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (state.step > 0) {
                    IconButton(onClick = viewModel::back) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Nazad")
                    }
                } else {
                    Spacer(Modifier.width(48.dp))
                }
                Spacer(Modifier.weight(1f))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    repeat(ONBOARDING_STEP_COUNT) { i ->
                        Box(
                            Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (i <= state.step) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                        )
                    }
                }
                Spacer(Modifier.weight(1f))
                Spacer(Modifier.width(48.dp))
            }

            AnimatedContent(
                targetState = state.step,
                modifier = Modifier.weight(1f),
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInHorizontally(tween(250)) { it } togetherWith slideOutHorizontally(tween(250)) { -it })
                    } else {
                        (slideInHorizontally(tween(250)) { -it } togetherWith slideOutHorizontally(tween(250)) { it })
                    }
                },
                label = "onboarding-step"
            ) { step ->
                when (step) {
                    0 -> IntroSlide(
                        icon = Icons.Rounded.Favorite,
                        title = "Male navike, velike promene",
                        body = "Atomic Habits uči da mali, dosledni koraci od 1% dnevno " +
                            "vremenom donose ogromne rezultate. HabitFlow ti pomaže da ih " +
                            "pratiš — potpuno offline."
                    )
                    1 -> IntroSlide(
                        icon = Icons.Rounded.EmojiObjects,
                        title = "Postani osoba koja...",
                        body = "Najtrajnije navike se ne grade oko cilja, već oko identiteta. " +
                            "Svaki put kad izvršiš naviku, glasaš za osobu kakva želiš da postaneš."
                    )
                    2 -> IdentityStep(
                        value = state.identityStatement,
                        onChange = viewModel::onIdentityStatementChange
                    )
                    else -> FirstHabitStep(
                        name = state.habitName,
                        category = state.habitCategory,
                        onNameChange = viewModel::onHabitNameChange,
                        onCategoryChange = viewModel::onHabitCategoryChange
                    )
                }
            }

            val isLastStep = state.step == ONBOARDING_STEP_COUNT - 1
            Button(
                onClick = { if (isLastStep) viewModel.finish() else viewModel.next() },
                enabled = !isLastStep || state.habitName.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isLastStep) "Završi" else "Dalje")
            }
        }
    }
}

@Composable
private fun IntroSlide(icon: ImageVector, title: String, body: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
        }
        Spacer(Modifier.height(32.dp))
        Text(
            title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Text(
            body,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IdentityStep(value: String, onChange: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Ko želiš da postaneš?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Ovo će te podsećati zašto gradiš naviku, ne samo šta radiš.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            label = { Text("Želim da postanem osoba koja...") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FirstHabitStep(
    name: String,
    category: String,
    onNameChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Kreiraj svoju prvu naviku",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Počni malo — uvek možeš dodati još navika kasnije.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Naziv navike") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = category,
            onValueChange = onCategoryChange,
            label = { Text("Kategorija (opciono)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
