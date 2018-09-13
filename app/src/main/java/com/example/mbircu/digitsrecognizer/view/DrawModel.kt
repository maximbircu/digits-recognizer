package com.example.mbircu.digitsrecognizer.view

import com.example.mbircu.digitsrecognizer.Point
import java.util.ArrayList

class Line(private val points: MutableList<Point> = mutableListOf()) {
    val pointsCount: Int
        get() = points.size
    fun addPoint(point: Point) = points.add(point)
    fun getPoint(index: Int) = points[index]
}

class DrawModel(val width: Int = 28, val height: Int = 28) {
    private var currentLine: Line? = null
    private val lines = ArrayList<Line>()

    val lineSize: Int
        get() = lines.size

    fun startLine(point: Point) {
        currentLine = Line().apply {
            addPoint(point)
            lines.add(this)
        }
    }

    fun addPointToCurrentLine(point: Point) = currentLine?.addPoint(point)

    fun endLine() {
        currentLine = null
    }

    fun getLine(index: Int) = lines[index]

    fun clear() = lines.clear()
}