package com.habitflow.app.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogin: () -> Unit,
    onRegister: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Profil") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (state.user == null) {
                Card(shape = RoundedCornerShape(20.dp)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Nalog", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Podaci ostaju na uređaju dok se ne prijaviš. Prijavom se postojeće navike " +
                                "pripajaju nalogu i počinje sinhronizacija.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Button(onClick = onLogin, modifier = Modifier.fillMaxWidth()) {
                            Text("Prijavi se")
                        }
                        OutlinedButton(onClick = onRegister, modifier = Modifier.fillMaxWidth()) {
                            Text("Registruj se")
                        }
                    }
                }
            } else {
                Card(shape = RoundedCornerShape(20.dp)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Nalog", style = MaterialTheme.typography.titleMedium)
                        Text(state.user!!.displayName, style = MaterialTheme.typography.bodyLarge)
                        Text(state.user!!.email, style = MaterialTheme.typography.bodyMedium)
                        OutlinedButton(onClick = viewModel::logout, modifier = Modifier.fillMaxWidth()) {
                            Text("Odjavi se")
                        }
                    }
                }

                Card(shape = RoundedCornerShape(20.dp)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Status sinhronizacije", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Poslednja sinhronizacija: ${formatSyncTime(state.lastSyncTime)}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (state.syncError != null) {
                            Text(state.syncError!!, color = MaterialTheme.colorScheme.error)
                        }
                        Button(
                            onClick = viewModel::syncNow,
                            enabled = !state.syncing,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (state.syncing) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp))
                            } else {
                                Text("Sinhronizuj sada")
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatSyncTime(millis: Long): String {
    if (millis == 0L) return "Nikad"
    val fmt = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    return fmt.format(Date(millis))
}
