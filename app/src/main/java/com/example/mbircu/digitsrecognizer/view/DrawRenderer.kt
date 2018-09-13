package com.example.mbircu.digitsrecognizer.view

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF

object DrawRenderer {
    fun renderModel(canvas: Canvas, model: DrawModel, paint: Paint, startLineIndex: Int) {
        paint.isAntiAlias = true
        for (i in startLineIndex until model.lineSize) {
            val line = model.getLine(i)
            paint.color = Color.BLACK
            if (line.pointsCount < 1) {
                continue
            }
            var point: PointF = line.getPoint(0)
            var lastX = point.x
            var lastY = point.y

            for (j in 0 until line.pointsCount) {
                point = line.getPoint(j)
                val x = point.x
                val y = point.y
                canvas.drawLine(lastX, lastY, x, y, paint)
                lastX = x
                lastY = y
            }
        }
    }
}

