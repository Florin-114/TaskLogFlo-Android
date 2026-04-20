package com.flo.tasklog.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flo.tasklog.TaskViewModel
import com.flo.tasklog.data.todayStr
import com.flo.tasklog.ui.components.StatsBar
import com.flo.tasklog.ui.components.TaskCard
import com.flo.tasklog.ui.theme.*
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(viewModel: TaskViewModel) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    var currentTab by remember { mutableStateOf(0) }
    var newTaskText by remember { mutableStateOf("") }

    val activeTasks = tasks.filter { !it.struck }
    val completedTasks = tasks.filter { it.struck }
    val today = todayStr()
    val updatedToday = tasks.count { t -> t.notes.any { it.dt.startsWith(today) } }
    val dateLabel = SimpleDateFormat("EEE d MMM yyyy", Locale.getDefault()).format(Date())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
            .padding(top = 16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = RoundedCornerShape(50), color = AccentColor, modifier = Modifier.size(8.dp)) {}
                    Spacer(Modifier.width(8.dp))
                    Text("TO DO LIST", fontSize = 22.sp, fontWeight = FontWeight.SemiBold, color = TextColor)
                }
                Text(
                    "FLO SOFTWARE HOUSE",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.08.sp,
                    color = Text3Color,
                    modifier = Modifier.padding(start = 16.dp, top = 2.dp)
                )
            }
            Surface(
                color = Surface2Color,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.border(1.dp, BorderColor, RoundedCornerShape(8.dp))
            ) {
                Text(
                    dateLabel,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontSize = 11.sp,
                    color = Text2Color
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        // Stats
        StatsBar(total = tasks.size, open = activeTasks.size, done = completedTasks.size, updatedToday = updatedToday)

        Spacer(Modifier.height(12.dp))

        // Tabs
        TabRow(
            selectedTabIndex = currentTab,
            containerColor = Surface2Color,
            contentColor = AccentColor,
        ) {
            Tab(selected = currentTab == 0, onClick = { currentTab = 0 }) {
                Text(
                    "Active (${activeTasks.size})",
                    modifier = Modifier.padding(vertical = 10.dp),
                    fontSize = 12.sp,
                    color = if (currentTab == 0) AccentColor else Text3Color
                )
            }
            Tab(selected = currentTab == 1, onClick = { currentTab = 1 }) {
                Text(
                    "Completed (${completedTasks.size})",
                    modifier = Modifier.padding(vertical = 10.dp),
                    fontSize = 12.sp,
                    color = if (currentTab == 1) GreenColor else Text3Color
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        // Remember outside if-block (Compose rule)
        val lazyListState = rememberLazyListState()
        val reorderState = rememberReorderableLazyListState(lazyListState) { from, to ->
            viewModel.reorderActiveTasks(from.index, to.index)
        }

        if (currentTab == 0) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(activeTasks, key = { it.id }) { task ->
                    ReorderableItem(reorderState, key = task.id) { _ ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DragHandle,
                                contentDescription = "Drag",
                                modifier = Modifier
                                    .draggableHandle()
                                    .size(24.dp),
                                tint = Text3Color
                            )
                            Spacer(Modifier.width(6.dp))
                            TaskCard(task = task, viewModel = viewModel)
                        }
                    }
                }
                item { Spacer(Modifier.height(4.dp)) }
            }

            Spacer(Modifier.height(10.dp))

            // Add task bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newTaskText,
                    onValueChange = { newTaskText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Add new task...", fontSize = 13.sp, color = Text3Color) },
                    textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, color = TextColor),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentColor,
                        unfocusedBorderColor = BorderColor,
                        cursorColor = AccentColor
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        if (newTaskText.isNotBlank()) {
                            viewModel.addTask(newTaskText)
                            newTaskText = ""
                        }
                    })
                )
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (newTaskText.isNotBlank()) {
                            viewModel.addTask(newTaskText)
                            newTaskText = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentColor)
                ) {
                    Text("+ Add", fontSize = 13.sp)
                }
            }
        } else {
            // Completed tasks
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(completedTasks, key = { it.id }) { task ->
                    TaskCard(task = task, viewModel = viewModel, onRestored = { currentTab = 0 })

                }
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}
