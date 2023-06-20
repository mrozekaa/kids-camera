package com.example.kidscamera.paint

import android.graphics.Color
import kotlin.random.Random

fun Int.Companion.randomColor(): Int
{
    return Color.argb(255,
        Random.nextInt(256),
        Random.nextInt(256),
        Random.nextInt(256))
}