package com.example.mbircu.digitsrecognizer.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.example.mbircu.digitsrecognizer.Point

class DrawView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paint = Paint()
    var drawModel: DrawModel? = null

    private var offScreenBitmap: Bitmap? = null // 28x28 pixel Bitmap
    private var offScreenCanvas: Canvas? = null

    private val myMatrix = Matrix()
    private val invMatrix = Matrix()
    private var linesCount = 0
    private var wasSetup = false

    private val tempPoints = FloatArray(2)

    /**
     * Get 28x28 pixel data for tensorflow input.
     */
    // Get 28x28 pixel data from bitmap
    // Set 0 for white and 255 for black pixel
    fun getImageData() : DoubleArray? {
        offScreenBitmap?.let {
            return getPixelDataFromBitmpa(it)
        }
        return null
    }



    fun getPixelDataFromBitmpa(bitmap: Bitmap): DoubleArray {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        val retPixels = DoubleArray(pixels.size)
        for (i in pixels.indices) {
            val pix = pixels[i]
            val b = pix and 0xff
            retPixels[i] = (0xff - b).toDouble() / 256.toDouble()
        }
        return retPixels
    }

    fun reset() {
        linesCount = 0
        if (offScreenBitmap != null) {
            paint.color = Color.WHITE
            val width = offScreenBitmap!!.width
            val height = offScreenBitmap!!.height
            offScreenCanvas!!.drawRect(Rect(0, 0, width, height), paint)
        }
    }

    private fun setup() {
        wasSetup = true

        // View size
        val width = width.toFloat()
        val height = height.toFloat()

        // Model (bitmap) size
        val modelWidth = drawModel!!.width.toFloat()
        val modelHeight = drawModel!!.height.toFloat()

        val scaleW = width / modelWidth
        val scaleH = height / modelHeight

        var scale = scaleW
        if (scale > scaleH) {
            scale = scaleH
        }

        val newCx = modelWidth * scale / 2
        val newCy = modelHeight * scale / 2
        val dx = width / 2 - newCx
        val dy = height / 2 - newCy

        myMatrix.setScale(scale, scale)
        myMatrix.postTranslate(dx, dy)
        myMatrix.invert(invMatrix)
        wasSetup = true
    }

    public override fun onDraw(canvas: Canvas) {
        if (drawModel == null) {
            return
        }
        if (!wasSetup) {
            setup()
        }
        if (offScreenBitmap == null) {
            return
        }

        var startIndex = linesCount - 1
        if (startIndex < 0) {
            startIndex = 0
        }

        DrawRenderer.renderModel(offScreenCanvas!!, drawModel!!, paint, startIndex)
        canvas.drawBitmap(offScreenBitmap!!, myMatrix, paint)

        linesCount = drawModel!!.lineSize
    }

    /**
     * Convert screen position to local pos (pos in bitmap)
     */
    fun getLocalBitmapPosition(screenPosition: Point): Point {
        tempPoints[0] = screenPosition.x
        tempPoints[1] = screenPosition.y
        invMatrix.mapPoints(tempPoints)
        return Point(tempPoints[0], tempPoints[1])
    }

    fun onResume() {
        createBitmap()
    }

    fun onPause() {
        releaseBitmap()
    }

    private fun createBitmap() {
        if (offScreenBitmap != null) {
            offScreenBitmap!!.recycle()
        }
        offScreenBitmap =
                Bitmap.createBitmap(drawModel!!.width, drawModel!!.height, Bitmap.Config.ARGB_8888)
        offScreenCanvas = Canvas(offScreenBitmap!!)
        reset()
    }

    private fun releaseBitmap() {
        if (offScreenBitmap != null) {
            offScreenBitmap!!.recycle()
            offScreenBitmap = null
            offScreenCanvas = null
        }
        reset()
    }
}
