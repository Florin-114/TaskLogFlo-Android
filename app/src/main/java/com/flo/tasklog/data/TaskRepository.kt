package com.flo.tasklog.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class TaskRepository(context: Context) {
    private val gson = Gson()
    private val file = File(context.filesDir, "tasks.json")

    fun load(): List<Task> {
        if (!file.exists()) return DefaultTasks.list
        return try {
            val type = object : TypeToken<List<Task>>() {}.type
            gson.fromJson<List<Task>>(file.readText(), type) ?: DefaultTasks.list
        } catch (e: Exception) {
            DefaultTasks.list
        }
    }

    fun save(tasks: List<Task>) {
        file.writeText(gson.toJson(tasks))
    }
}
