package com.knurl.app.ui.programs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.knurl.app.data.entity.Exercise

@Composable
fun ProgramEditorScreen(
    onSaved: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProgramEditorViewModel = viewModel(factory = ProgramEditorViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val libraryExercises by viewModel.libraryExercises.collectAsState()

    LaunchedEffect(uiState.saved) {
        if (uiState.saved) onSaved()
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
                OutlinedTextField(
                    value = uiState.programName,
                    onValueChange = viewModel::updateProgramName,
                    label = { Text("Program name") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            itemsIndexed(uiState.days) { dayIndex: Int, day: EditableDayUi ->
                DayEditor(
                    day = day,
                    libraryExercises = libraryExercises,
                    canRemove = uiState.days.size > 1,
                    onNameChange = { viewModel.updateDayName(dayIndex, it) },
                    onRemoveDay = { viewModel.removeDay(dayIndex) },
                    onAddExercise = { exercise, sets, reps, increment ->
                        viewModel.addExercise(dayIndex, exercise, sets, reps, increment)
                    },
                    onRemoveExercise = { exerciseIndex -> viewModel.removeExercise(dayIndex, exerciseIndex) }
                )
            }

            item {
                TextButton(onClick = viewModel::addDay) { Text("Add day") }
            }

            item {
                Button(onClick = viewModel::save, modifier = Modifier.fillMaxWidth()) {
                    Text("Save program")
                }
            }
        }
    }
}

@Composable
private fun DayEditor(
    day: EditableDayUi,
    libraryExercises: List<Exercise>,
    canRemove: Boolean,
    onNameChange: (String) -> Unit,
    onRemoveDay: () -> Unit,
    onAddExercise: (Exercise, Int, Int, Double) -> Unit,
    onRemoveExercise: (Int) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(
                    value = day.name,
                    onValueChange = onNameChange,
                    label = { Text("Day name") },
                    modifier = Modifier.weight(1f)
                )
                if (canRemove) {
                    IconButton(onClick = onRemoveDay) {
                        Icon(Icons.Filled.Delete, contentDescription = "Remove day")
                    }
                }
            }

            day.exercises.forEachIndexed { index, exercise ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${exercise.exerciseName} — ${exercise.sets}x${exercise.targetReps} (+${exercise.increment})",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    IconButton(onClick = { onRemoveExercise(index) }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Remove exercise")
                    }
                }
            }

            AddExerciseForm(libraryExercises = libraryExercises, onAdd = onAddExercise)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddExerciseForm(libraryExercises: List<Exercise>, onAdd: (Exercise, Int, Int, Double) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf<Exercise?>(null) }
    var sets by remember { mutableStateOf("3") }
    var reps by remember { mutableStateOf("10") }
    var increment by remember { mutableStateOf("5") }

    Column(modifier = Modifier.padding(top = 12.dp)) {
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            OutlinedTextField(
                value = selected?.name ?: "Select exercise",
                onValueChange = {},
                readOnly = true,
                label = { Text("Exercise") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                libraryExercises.forEach { exercise ->
                    DropdownMenuItem(
                        text = { Text(exercise.name) },
                        onClick = {
                            selected = exercise
                            expanded = false
                        }
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = sets,
                onValueChange = { sets = it },
                label = { Text("Sets") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.width(90.dp)
            )
            OutlinedTextField(
                value = reps,
                onValueChange = { reps = it },
                label = { Text("Reps") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.width(90.dp)
            )
            OutlinedTextField(
                value = increment,
                onValueChange = { increment = it },
                label = { Text("Increment") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.width(110.dp)
            )
        }

        TextButton(
            onClick = {
                val exercise = selected ?: return@TextButton
                val setsValue = sets.toIntOrNull() ?: return@TextButton
                val repsValue = reps.toIntOrNull() ?: return@TextButton
                val incrementValue = increment.toDoubleOrNull() ?: return@TextButton
                onAdd(exercise, setsValue, repsValue, incrementValue)
                selected = null
            },
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Text("Add exercise")
        }
    }
}
