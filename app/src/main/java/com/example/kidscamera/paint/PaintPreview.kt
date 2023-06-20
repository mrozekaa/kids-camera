package com.example.kidscamera.paint

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PaintPreview(color:Color) {
    var points by remember { mutableStateOf<List<Offset>>(emptyList()) }
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(0.dp, 0.dp, 0.dp, 150.dp)
            .pointerInput(key1 = Unit) {
                detectDragGestures(
                    onDragStart = { touch ->
                        points = listOf(touch)
                    },
                    onDrag = { change, _ ->
                        val pointsFromHistory = change.historical
                            .map { it.position }
                            .toTypedArray()
                        val newPoints = listOf(*pointsFromHistory, change.position)
                        points = points + newPoints
                    }
                )
            }

    ) {
        if (points.size > 1) {
            val path = Path().apply {
                val firstPoint = points.first()
                val rest = points.subList(1, points.size - 1)

                moveTo(firstPoint.x, firstPoint.y)
                rest.forEach {
                    lineTo(it.x, it.y)
                }
            }

            drawPath(path, color, style = Stroke(width = 5.dp.toPx()))
        }
    }
}