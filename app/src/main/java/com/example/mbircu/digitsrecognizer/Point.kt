package com.example.mbircu.digitsrecognizer

import android.graphics.PointF

class Point(x: Float = 0f, y: Float = 0f) : PointF(x, y) {
    fun copy() = Point(x, y)
    fun set(point: Point)  {
        x = point.x
        y = point.y
    }
}