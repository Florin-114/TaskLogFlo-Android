package com.flo.tasklog.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flo.tasklog.ui.theme.*

@Composable
fun StatsBar(total: Int, open: Int, done: Int, updatedToday: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard("Total", total.toString(), TextColor, Modifier.weight(1f))
        StatCard("Open", open.toString(), AccentColor, Modifier.weight(1f))
        StatCard("Done", done.toString(), GreenColor, Modifier.weight(1f))
        StatCard("Today", updatedToday.toString(), TextColor, Modifier.weight(1f))
    }
}

@Composable
private fun StatCard(label: String, value: String, valueColor: androidx.compose.ui.graphics.Color, modifier: Modifier) {
    Surface(
        modifier = modifier.border(1.dp, BorderColor, RoundedCornerShape(8.dp)),
        color = SurfaceColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace,
                color = valueColor
            )
            Text(
                text = label.uppercase(),
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.08.sp,
                color = Text3Color
            )
        }
    }
}
