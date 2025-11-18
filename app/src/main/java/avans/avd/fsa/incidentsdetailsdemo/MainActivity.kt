package avans.avd.fsa.incidentsdetailsdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import avans.avd.fsa.incidentsdetailsdemo.ui.theme.IncidentsDetailsDemoTheme
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.days
// the following import can be removed when the experimental time API is stable (In Kotlin 2.3.0)
import kotlin.time.ExperimentalTime

enum class Priority(val label: String, val deadlineDays: Long) {
    LOW("Low", 7),
    MEDIUM("Medium", 3),
    HIGH("High", 1)
}




class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IncidentsDetailsDemoTheme {
                var note by remember { mutableStateOf("") }
                var priority by remember { mutableStateOf(Priority.MEDIUM) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    IncidentScreen(
                        category = "Vandalisme",
                        description = "Zijruit van bushalte ingegooid",
                        status = "Reported",
                        priority = priority,
                        onPriorityChange = { priority = it },
                        note = note,
                        onNoteChange = { note = it },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
// This OptIn can be removed when the experimental time API is stable (In Kotlin 2.3.0)
@OptIn(ExperimentalTime::class)
// State (note, priority) is passed down as parameters
// Events (onNoteChange, onPriorityChange) are passed up as callbacks
@Composable
fun IncidentScreen(
    category: String,
    description: String,
    status: String,
    priority: Priority,
    onPriorityChange: (Priority) -> Unit,
    note: String,
    onNoteChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // We're "subscribing" to priority, changing deadline each time priority updates.
    val deadline = remember(priority) {
        val now = Clock.System.now()
        val shifted = now + priority.deadlineDays.days
        val ldt = shifted.toLocalDateTime(TimeZone.currentSystemDefault())
        String.format("%02d-%02d-%04d", ldt.day, ldt.month.number, ldt.year)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Incident Details",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    thickness = 2.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                )

                IncidentInfoRow(label = "Category", value = category)
                Spacer(modifier = Modifier.height(16.dp))

                IncidentInfoRow(label = "Description", value = description)
                Spacer(modifier = Modifier.height(16.dp))

                IncidentInfoRow(label = "Status", value = status)
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Priority",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Priority.entries.forEachIndexed { index, p ->
                        SegmentedButton(
                            selected = p == priority,
                            onClick = { onPriorityChange(p) },
                            shape = SegmentedButtonDefaults.itemShape(index, Priority.entries.size)
                        ) {
                            Text(p.label)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                ) {
                    IncidentInfoRow(
                        label = "Deadline",
                        value = deadline,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Notes",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = note,
                    onValueChange = onNoteChange,
                    placeholder = { Text("Add notes...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}

@Composable
fun IncidentInfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview
@Composable
fun IncidentScreenPreview() {
    IncidentsDetailsDemoTheme {
        var note by remember { mutableStateOf("") }
        var priority by remember { mutableStateOf(Priority.MEDIUM) }

        IncidentScreen(
            category = "Vandalisme",
            description = "Zijruit van bushalte ingegooid",
            status = "Reported",
            priority = priority,
            onPriorityChange = { priority = it },
            note = note,
            onNoteChange = { note = it }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun IncidentScreenFullPreview() {
    IncidentsDetailsDemoTheme {
        var note by remember { mutableStateOf("Vuilniswagen rijdt er morgen langs") }
        var priority by remember { mutableStateOf(Priority.LOW) }

        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            IncidentScreen(
                category = "Afval",
                description = "Vuilniszakken achter de jumbo, stinkt als een malle",
                status = "Reported",
                priority = priority,
                onPriorityChange = { priority = it },
                note = note,
                onNoteChange = { note = it },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}