package com.flo.tasklog.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.zIndex
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flo.tasklog.TaskViewModel
import com.flo.tasklog.data.Note
import com.flo.tasklog.data.Task
import com.flo.tasklog.ui.theme.*

@Composable
fun TaskCard(
    task: Task,
    viewModel: TaskViewModel,
    onRestored: (() -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }
    var newNote by remember { mutableStateOf("") }
    var editingNoteIndex by remember { mutableStateOf(-1) }
    var editingNoteText by remember { mutableStateOf("") }
    var showDeleteConfirm by remember { mutableStateOf(-1) }
    val focusRequester = remember { FocusRequester() }
    var activeNoteMenu by remember { mutableStateOf(-1) }
    val focusManager = LocalFocusManager.current

    val latestDate = task.notes.lastOrNull { it.dt.isNotEmpty() }?.dt?.take(5) ?: ""

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .zIndex(if (expanded) 1f else 0f)
            .border(1.dp, if (expanded) AccentColor.copy(alpha = 0.4f) else BorderColor, RoundedCornerShape(10.dp)),
        color = SurfaceColor,
        shape = RoundedCornerShape(10.dp),
        shadowElevation = if (expanded) 8.dp else 0.dp
    ) {
        Column {
            // Main row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded; focusManager.clearFocus() }
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Number badge
                Surface(
                    shape = CircleShape,
                    color = if (task.struck) Surface3Color else AccentColor,
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = task.id.toString(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Monospace,
                            color = if (task.struck) Text3Color else Color.White
                        )
                    }
                }

                Spacer(Modifier.width(10.dp))

                // Task name
                Text(
                    text = task.task,
                    modifier = Modifier.weight(1f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (task.struck) Text3Color else TextColor,
                    textDecoration = if (task.struck) TextDecoration.LineThrough else TextDecoration.None
                )

                Spacer(Modifier.width(8.dp))

                // Date tag
                if (latestDate.isNotEmpty()) {
                    Surface(
                        color = Color(0x1FF5A623),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = latestDate,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = if (task.struck) Text3Color else GoldColor
                        )
                    }
                    Spacer(Modifier.width(6.dp))
                }

                // Complete / Restore button always visible in header
                if (!task.struck) {
                    OutlinedButton(
                        onClick = { viewModel.completeTask(task.id) },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                        modifier = Modifier.height(30.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(AccentColor.copy(alpha = 0.5f))
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentColor)
                    ) { Text("Completed", fontSize = 10.sp) }
                } else {
                    OutlinedButton(
                        onClick = { viewModel.restoreTask(task.id); onRestored?.invoke() },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                        modifier = Modifier.height(30.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(GreenColor.copy(alpha = 0.5f))
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = GreenColor)
                    ) { Text("Restore", fontSize = 10.sp) }
                }
            }

            // Expanded content
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 12.dp, bottom = 10.dp)
                ) {
                    HorizontalDivider(color = BorderColor, thickness = 1.dp)
                    Spacer(Modifier.height(8.dp))

                    // Notes list
                    task.notes.forEachIndexed { index, note ->
                        if (editingNoteIndex == index) {
                            // Inline edit
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = editingNoteText,
                                    onValueChange = { editingNoteText = it },
                                    modifier = Modifier
                                        .weight(1f)
                                        .focusRequester(focusRequester),
                                    textStyle = LocalTextStyle.current.copy(fontSize = 12.sp, color = TextColor),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = AccentColor,
                                        unfocusedBorderColor = BorderColor,
                                        cursorColor = AccentColor
                                    ),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                    keyboardActions = KeyboardActions(onDone = {
                                        if (editingNoteText.isNotBlank()) {
                                            viewModel.editNote(task.id, index, editingNoteText)
                                        }
                                        editingNoteIndex = -1
                                    })
                                )
                                Spacer(Modifier.width(4.dp))
                                TextButton(
                                    onClick = {
                                        if (editingNoteText.isNotBlank()) {
                                            viewModel.editNote(task.id, index, editingNoteText)
                                        }
                                        editingNoteIndex = -1
                                    },
                                    colors = ButtonDefaults.textButtonColors(contentColor = AccentColor)
                                ) { Text("Save", fontSize = 11.sp) }
                                TextButton(
                                    onClick = { editingNoteIndex = -1 },
                                    colors = ButtonDefaults.textButtonColors(contentColor = Text3Color)
                                ) { Text("Cancel", fontSize = 11.sp) }
                            }
                            LaunchedEffect(Unit) { focusRequester.requestFocus() }
                        } else {
                            NoteRow(
                                note = note,
                                showActions = activeNoteMenu == index,
                                onToggleShow = { activeNoteMenu = if (activeNoteMenu == index) -1 else index },
                                onToggleStruck = {
                                    viewModel.toggleNoteStruck(task.id, index)
                                    activeNoteMenu = -1
                                },
                                onEdit = {
                                    editingNoteText = note.text
                                    editingNoteIndex = index
                                    activeNoteMenu = -1
                                },
                                onDelete = {
                                    showDeleteConfirm = index
                                    activeNoteMenu = -1
                                }
                            )
                        }
                    }

                    // Add note input
                    if (!task.struck) {
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = newNote,
                                onValueChange = { newNote = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .onFocusChanged { if (it.isFocused) activeNoteMenu = -1 },
                                placeholder = { Text("Add update...", fontSize = 12.sp, color = Text3Color) },
                                textStyle = LocalTextStyle.current.copy(fontSize = 12.sp, color = TextColor),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AccentColor,
                                    unfocusedBorderColor = BorderColor,
                                    cursorColor = AccentColor
                                ),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = {
                                    if (newNote.isNotBlank()) {
                                        viewModel.addNote(task.id, newNote)
                                        newNote = ""
                                        activeNoteMenu = -1
                                    }
                                })
                            )
                            Spacer(Modifier.width(6.dp))
                            Button(
                                onClick = {
                                    if (newNote.isNotBlank()) {
                                        viewModel.addNote(task.id, newNote)
                                        newNote = ""
                                        activeNoteMenu = -1
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentColor)
                            ) { Text("Save", fontSize = 12.sp) }
                        }
                    }

                }
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteConfirm >= 0) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = -1 },
            title = { Text("Delete note?", color = TextColor) },
            text = { Text("This cannot be undone.", color = Text2Color) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteNote(task.id, showDeleteConfirm)
                    showDeleteConfirm = -1
                }) { Text("Delete", color = AccentColor) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = -1 }) {
                    Text("Cancel", color = Text3Color)
                }
            },
            containerColor = Surface2Color,
            tonalElevation = 0.dp
        )
    }
}

@Composable
private fun NoteRow(
    note: Note,
    showActions: Boolean,
    onToggleShow: () -> Unit,
    onToggleStruck: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable { onToggleShow(); focusManager.clearFocus() }
        .padding(vertical = 3.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Text(
                text = buildAnnotatedString {
                    if (note.struck) {
                        withStyle(SpanStyle(textDecoration = TextDecoration.LineThrough, color = Text3Color)) {
                            append(note.text)
                        }
                    } else {
                        append(note.text)
                    }
                    if (note.dt.isNotEmpty()) {
                        withStyle(SpanStyle(
                            color = if (note.struck) Text3Color else GoldColor,
                            fontSize = 9.5.sp,
                            fontFamily = FontFamily.Monospace,
                            textDecoration = if (note.struck) TextDecoration.LineThrough else TextDecoration.None
                        )) {
                            append("  · ${note.dt}")
                        }
                    }
                },
                fontSize = 11.5.sp,
                color = if (note.struck) Text3Color else Text2Color,
                modifier = Modifier.weight(1f)
            )
        }

        AnimatedVisibility(visible = showActions) {
            Row(
                modifier = Modifier.padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                NoteActionButton(
                    label = if (note.struck) "undo" else "done",
                    color = if (note.struck) GreenColor else Text3Color,
                    onClick = onToggleStruck
                )
                NoteActionButton("edit", GoldColor, onEdit)
                NoteActionButton("del", AccentColor, onDelete)
            }
        }
    }
}

@Composable
private fun NoteActionButton(label: String, color: Color, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
        modifier = Modifier.height(26.dp),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = androidx.compose.ui.graphics.SolidColor(color.copy(alpha = 0.5f))
        ),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = color)
    ) {
        Text(label, fontSize = 10.sp)
    }
}
