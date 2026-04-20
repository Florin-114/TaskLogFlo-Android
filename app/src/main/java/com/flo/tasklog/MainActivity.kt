package com.flo.tasklog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.flo.tasklog.ui.MainScreen
import com.flo.tasklog.ui.theme.BgColor
import com.flo.tasklog.ui.theme.TaskLogFloTheme

class MainActivity : ComponentActivity() {
    private val viewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaskLogFloTheme {
                androidx.compose.material3.Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BgColor),
                    color = BgColor
                ) {
                    MainScreen(viewModel)
                }
            }
        }
    }
}
