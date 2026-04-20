package com.flo.tasklog

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.flo.tasklog.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TaskViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = TaskRepository(app)
    private val _tasks = MutableStateFlow(repo.load())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private fun update(newList: List<Task>) {
        _tasks.value = newList
        repo.save(newList)
    }

    fun addTask(name: String) {
        val maxId = _tasks.value.maxOfOrNull { it.id } ?: 0
        update(_tasks.value + Task(maxId + 1, name, notes = listOf(Note("Task added", nowStamp()))))
    }

    fun completeTask(id: Int) {
        update(_tasks.value.map { t ->
            if (t.id == id) t.copy(struck = true)
            else t
        })
    }

    fun restoreTask(id: Int) {
        update(_tasks.value.map { t ->
            if (t.id == id) t.copy(struck = false)
            else t
        })
    }

    fun addNote(taskId: Int, text: String) {
        update(_tasks.value.map { t ->
            if (t.id == taskId) t.copy(notes = t.notes + Note(text, nowStamp()))
            else t
        })
    }

    fun editNote(taskId: Int, noteIndex: Int, text: String) {
        update(_tasks.value.map { t ->
            if (t.id == taskId) {
                val notes = t.notes.toMutableList()
                notes[noteIndex] = notes[noteIndex].copy(text = text, dt = nowStamp())
                t.copy(notes = notes)
            } else t
        })
    }

    fun deleteNote(taskId: Int, noteIndex: Int) {
        update(_tasks.value.map { t ->
            if (t.id == taskId) {
                val notes = t.notes.toMutableList()
                notes.removeAt(noteIndex)
                t.copy(notes = notes)
            } else t
        })
    }

    fun toggleNoteStruck(taskId: Int, noteIndex: Int) {
        update(_tasks.value.map { t ->
            if (t.id == taskId) {
                val notes = t.notes.toMutableList()
                notes[noteIndex] = notes[noteIndex].copy(struck = !notes[noteIndex].struck)
                t.copy(notes = notes)
            } else t
        })
    }

    fun reorderActiveTasks(fromIndex: Int, toIndex: Int) {
        val active = _tasks.value.filter { !it.struck }.toMutableList()
        val completed = _tasks.value.filter { it.struck }
        val item = active.removeAt(fromIndex)
        active.add(toIndex, item)
        update(active + completed)
    }
}
