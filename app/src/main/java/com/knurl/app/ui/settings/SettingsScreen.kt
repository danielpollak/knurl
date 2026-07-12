package com.knurl.app.ui.settings

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.knurl.app.data.model.WeightUnit
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory)
) {
    val settings by viewModel.settings.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var barWeightText by remember { mutableStateOf("") }
    var restTimerText by remember { mutableStateOf("") }
    var plates by remember { mutableStateOf(emptyList<Double>()) }
    var newPlateText by remember { mutableStateOf("") }
    var showRestoreConfirm by remember { mutableStateOf(false) }
    var pendingRestoreContent by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(settings) {
        settings?.let {
            barWeightText = formatPlateNumber(it.barWeight)
            restTimerText = it.restTimerSeconds.toString()
            plates = it.availablePlates
        }
    }

    val exportCsvLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri ->
        if (uri != null) {
            coroutineScope.launch {
                val csv = viewModel.buildCsv()
                context.contentResolver.openOutputStream(uri)?.use { it.write(csv.toByteArray()) }
            }
        }
    }

    val exportBackupLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if (uri != null) {
            coroutineScope.launch {
                val backupJson = viewModel.buildJsonBackup()
                context.contentResolver.openOutputStream(uri)?.use { it.write(backupJson.toByteArray()) }
            }
        }
    }

    val restoreLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            coroutineScope.launch {
                val content = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { reader -> reader.readText() }
                if (content != null) {
                    pendingRestoreContent = content
                    showRestoreConfirm = true
                }
            }
        }
    }

    if (showRestoreConfirm) {
        AlertDialog(
            onDismissRequest = {
                showRestoreConfirm = false
                pendingRestoreContent = null
            },
            title = { Text("Restore backup?") },
            text = { Text("This replaces all current data -- programs, exercises, and history -- with the contents of the backup file. This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    val content = pendingRestoreContent
                    showRestoreConfirm = false
                    pendingRestoreContent = null
                    if (content != null) {
                        coroutineScope.launch { viewModel.restoreFromJson(content) }
                    }
                }) { Text("Restore") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showRestoreConfirm = false
                    pendingRestoreContent = null
                }) { Text("Cancel") }
            }
        )
    }

    Scaffold(modifier = modifier) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column {
                    Text("Unit", style = MaterialTheme.typography.titleMedium)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        WeightUnit.entries.forEach { unit ->
                            val selected = settings?.unit == unit
                            if (selected) {
                                Button(onClick = {}) { Text(unit.name) }
                            } else {
                                OutlinedButton(onClick = { viewModel.switchUnit(unit) }) { Text(unit.name) }
                            }
                        }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Bar & plates", style = MaterialTheme.typography.titleMedium)
                        OutlinedTextField(
                            value = barWeightText,
                            onValueChange = { barWeightText = it },
                            label = { Text("Bar weight") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            plates.forEachIndexed { index, plate ->
                                PlateChip(plate = plate, onRemove = { plates = plates.filterIndexed { i, _ -> i != index } })
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = newPlateText,
                                onValueChange = { newPlateText = it },
                                label = { Text("Add plate") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(onClick = {
                                val value = newPlateText.toDoubleOrNull()
                                if (value != null && value > 0) {
                                    plates = (plates + value).sortedDescending()
                                    newPlateText = ""
                                }
                            }) { Text("Add") }
                        }

                        OutlinedTextField(
                            value = restTimerText,
                            onValueChange = { restTimerText = it },
                            label = { Text("Default rest timer (seconds)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        )

                        Button(
                            onClick = {
                                val barWeight = barWeightText.toDoubleOrNull()
                                val restSeconds = restTimerText.toIntOrNull()
                                if (barWeight != null && restSeconds != null) {
                                    viewModel.updateBarAndPlates(barWeight, plates)
                                    viewModel.updateRestTimer(restSeconds)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                        ) {
                            Text("Save settings")
                        }
                    }
                }
            }

            item { HorizontalDivider() }

            item {
                Column {
                    Text("Export", style = MaterialTheme.typography.titleMedium)
                    Button(
                        onClick = { exportCsvLauncher.launch(csvFileName()) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) { Text("Export CSV") }
                    OutlinedButton(
                        onClick = {
                            coroutineScope.launch {
                                val csv = viewModel.buildCsv()
                                shareCsv(context, csv)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) { Text("Share CSV") }
                }
            }

            item {
                Column {
                    Text("Backup", style = MaterialTheme.typography.titleMedium)
                    Button(
                        onClick = { exportBackupLauncher.launch("knurl_backup.json") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) { Text("Backup to JSON") }
                    OutlinedButton(
                        onClick = { restoreLauncher.launch(arrayOf("application/json")) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) { Text("Restore from JSON") }
                }
            }
        }
    }
}

@Composable
private fun PlateChip(plate: Double, onRemove: () -> Unit) {
    Card {
        Row(
            modifier = Modifier.padding(start = 12.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = formatPlateNumber(plate))
            IconButton(onClick = onRemove) {
                Icon(Icons.Filled.Close, contentDescription = "Remove plate")
            }
        }
    }
}

private fun formatPlateNumber(value: Double): String =
    if (value == value.toLong().toDouble()) value.toLong().toString() else value.toString()

private fun csvFileName(): String =
    "workouts_${LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))}.csv"

private fun shareCsv(context: Context, csv: String) {
    val file = File(context.cacheDir, csvFileName())
    file.writeText(csv)
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/csv"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Share workout CSV"))
}
