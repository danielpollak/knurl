package com.knurl.app.ui.exercises

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.knurl.app.data.entity.Exercise
import com.knurl.app.data.model.MuscleGroup

@Composable
fun ExercisesScreen(
    modifier: Modifier = Modifier,
    viewModel: ExercisesViewModel = viewModel(factory = ExercisesViewModel.Factory)
) {
    val exercises by viewModel.exercises.collectAsState()

    Scaffold(modifier = modifier) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { AddExerciseCard(onAdd = viewModel::addCustomExercise) }

            items(exercises, key = { it.id }) { exercise ->
                ExerciseRow(exercise)
            }
        }
    }
}

@Composable
private fun ExerciseRow(exercise: Exercise) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = exercise.name, style = MaterialTheme.typography.titleMedium)
            Text(text = exercise.muscleGroup.name, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun AddExerciseCard(onAdd: (String, MuscleGroup) -> Unit) {
    var name by remember { mutableStateOf("") }
    var muscleGroup by remember { mutableStateOf(MuscleGroup.CHEST) }
    var expanded by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = "Add custom exercise", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)) {
                OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                    Text("Muscle group: ${muscleGroup.name}")
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    MuscleGroup.entries.forEach { group ->
                        DropdownMenuItem(
                            text = { Text(group.name) },
                            onClick = {
                                muscleGroup = group
                                expanded = false
                            }
                        )
                    }
                }
            }
            TextButton(
                onClick = {
                    onAdd(name, muscleGroup)
                    name = ""
                },
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text("Add")
            }
        }
    }
}
