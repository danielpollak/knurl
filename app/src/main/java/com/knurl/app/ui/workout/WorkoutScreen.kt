package com.knurl.app.ui.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun WorkoutScreen(modifier: Modifier = Modifier, viewModel: WorkoutViewModel = viewModel(factory = WorkoutViewModel.Factory)) {
    val uiState by viewModel.uiState.collectAsState()
    WorkoutContent(
        uiState = uiState,
        onTapSet = viewModel::tapSet,
        onAdjustWeight = viewModel::adjustWeight,
        onSelectDay = viewModel::selectDay,
        onSkipRest = viewModel::skipRest,
        onFinishWorkout = viewModel::finishWorkout,
        modifier = modifier
    )
}

@Composable
private fun WorkoutContent(
    uiState: WorkoutUiState,
    onTapSet: (exerciseId: Long, setIndex: Int) -> Unit,
    onAdjustWeight: (exerciseId: Long, direction: Int) -> Unit,
    onSelectDay: (dayId: Long) -> Unit,
    onSkipRest: () -> Unit,
    onFinishWorkout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDayPicker by remember { mutableStateOf(false) }
    if (showDayPicker) {
        AlertDialog(
            onDismissRequest = { showDayPicker = false },
            title = { Text("Choose workout") },
            text = {
                Column {
                    uiState.availableDays.forEach { day ->
                        TextButton(
                            onClick = {
                                showDayPicker = false
                                if (day.name != uiState.dayName) onSelectDay(day.id)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(day.name, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showDayPicker = false }) { Text("Cancel") } }
        )
    }

    var weightDialogExerciseId by remember { mutableStateOf<Long?>(null) }
    uiState.exercises.firstOrNull { it.exerciseId == weightDialogExerciseId }?.let { exercise ->
        WeightDialog(
            exercise = exercise,
            barWeight = uiState.barWeight,
            unitLabel = uiState.unitLabel,
            onAdjust = { direction -> onAdjustWeight(exercise.exerciseId, direction) },
            onDismiss = { weightDialogExerciseId = null }
        )
    }

    Column(modifier = modifier.fillMaxSize()) {
        if (uiState.restSecondsRemaining != null) {
            RestTimerBanner(secondsRemaining = uiState.restSecondsRemaining, onSkip = onSkipRest)
        }

        when {
            uiState.isLoading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Loading workout...")
            }

            !uiState.hasActiveProgram -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No active program. Pick one from Programs.")
            }

            else -> Column(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Text(
                        text = uiState.dayName + if (uiState.availableDays.size > 1) " \u25BE" else "",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable(enabled = uiState.availableDays.size > 1) { showDayPicker = true }
                    )
                    Text(text = uiState.programName, style = MaterialTheme.typography.bodyMedium)
                }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.exercises, key = { it.exerciseId }) { exercise ->
                        ExerciseCard(
                            exercise = exercise,
                            unitLabel = uiState.unitLabel,
                            onTapSet = { setIndex -> onTapSet(exercise.exerciseId, setIndex) },
                            onWeightClick = { weightDialogExerciseId = exercise.exerciseId }
                        )
                    }
                }

                Button(
                    onClick = onFinishWorkout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Finish workout")
                }
            }
        }
    }
}

@Composable
private fun RestTimerBanner(secondsRemaining: Int, onSkip: () -> Unit) {
    Surface(color = MaterialTheme.colorScheme.secondaryContainer, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Resting: ${secondsRemaining}s", style = MaterialTheme.typography.titleMedium)
            TextButton(onClick = onSkip) { Text("Skip") }
        }
    }
}

@Composable
private fun ExerciseCard(
    exercise: ExerciseUiState,
    unitLabel: String,
    onTapSet: (setIndex: Int) -> Unit,
    onWeightClick: () -> Unit
) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = exercise.name, style = MaterialTheme.typography.titleLarge)
                Surface(
                    onClick = onWeightClick,
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Text(
                        text = "${exercise.weight.toInt()} $unitLabel",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }
            Text(
                text = plateBreakdownLabel(exercise.plateBreakdown),
                style = MaterialTheme.typography.bodySmall
            )
            Row(
                modifier = Modifier.padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                exercise.repsPerSet.forEachIndexed { index, reps ->
                    SetChip(
                        reps = reps,
                        targetReps = exercise.targetReps,
                        onClick = { onTapSet(index) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SetChip(reps: Int?, targetReps: Int, onClick: () -> Unit) {
    val backgroundColor = if (reps == null) {
        MaterialTheme.colorScheme.surface
    } else {
        MaterialTheme.colorScheme.error
    }
    val contentColor = if (reps == null) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.onError
    }
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = (reps ?: targetReps).toString(), color = contentColor, textAlign = TextAlign.Center)
    }
}

@Composable
private fun WeightDialog(
    exercise: ExerciseUiState,
    barWeight: Double,
    unitLabel: String,
    onAdjust: (direction: Int) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(exercise.name) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = { onAdjust(-1) }) { Text("-") }
                    Text(
                        text = "${formatWeight(exercise.weight)} $unitLabel",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Button(onClick = { onAdjust(1) }) { Text("+") }
                }
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Text("Each side:", style = MaterialTheme.typography.titleSmall)
                    Text("Bar: ${formatWeight(barWeight)} $unitLabel")
                    exercise.plateBreakdown.groupingBy { it }.eachCount().forEach { (plate, count) ->
                        Text("$count x ${formatWeight(plate)} $unitLabel")
                    }
                    if (exercise.plateBreakdown.isEmpty()) Text("No plates")
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Done") } }
    )
}

private fun formatWeight(value: Double): String =
    if (value == value.toLong().toDouble()) value.toLong().toString() else value.toString()

private fun plateBreakdownLabel(plates: List<Double>): String {
    if (plates.isEmpty()) return "Bar only"
    val formatted = plates.joinToString(", ") { plate ->
        if (plate == plate.toLong().toDouble()) plate.toLong().toString() else plate.toString()
    }
    return "Plates per side: $formatted"
}
