package com.flo.tasklog.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Note(
    val text: String,
    val dt: String = "",
    val struck: Boolean = false
)

data class Task(
    val id: Int,
    val task: String,
    val struck: Boolean = false,
    val notes: List<Note> = emptyList()
)

fun todayStr(): String = SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date())
fun nowStamp(): String = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(Date())
