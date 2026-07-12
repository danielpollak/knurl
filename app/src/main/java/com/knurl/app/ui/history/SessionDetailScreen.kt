package com.knurl.app.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.knurl.app.data.entity.SetLog

@Composable
fun SessionDetailScreen(
    sessionId: Long,
    modifier: Modifier = Modifier,
    viewModel: SessionDetailViewModel = viewModel(factory = SessionDetailViewModel.factory(sessionId))
) {
    val setLogs by viewModel.setLogs.collectAsState()
    val unitLabel by viewModel.unitLabel.collectAsState()

    Scaffold(modifier = modifier) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(setLogs, key = { it.id }) { setLog ->
                SetLogRow(setLog, unitLabel)
            }
        }
    }
}

@Composable
private fun SetLogRow(setLog: SetLog, unitLabel: String) {
    val successColor = MaterialTheme.colorScheme.primary
    val failureColor = MaterialTheme.colorScheme.error
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "${setLog.exerciseName} — set ${setLog.setNumber}")
            Text(
                text = "${setLog.repsDone}/${setLog.targetReps} @ ${setLog.weight.toInt()} $unitLabel",
                color = if (setLog.success) successColor else failureColor
            )
        }
    }
}
