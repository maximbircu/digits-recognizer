package com.example.mbircu.digitsrecognizer

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_MOVE
import android.view.MotionEvent.ACTION_UP
import android.view.View
import com.example.mbircu.digitsrecognizer.view.DrawModel
import kotlinx.android.synthetic.main.activity_main.*
import neuralnetwork.NeuralNetwork
import neuralnetwork.Tensor
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.ObjectInputStream


private const val NEURAL_NET_FILE_NAME = "neural_net_old"

@SuppressLint("ClickableViewAccessibility")
class MainActivity : AppCompatActivity(), View.OnTouchListener {
    private var drawModel: DrawModel = DrawModel()
    private val lastPoint = Point()

    private lateinit var neuralNetwork: NeuralNetwork

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawingView.drawModel = drawModel
        drawingView.setOnTouchListener(this)
        clearButton.setOnClickListener(::onClearClicked)
        neuralNetwork = load(assets.open(NEURAL_NET_FILE_NAME))
    }

    private fun load(inputStream: InputStream): NeuralNetwork {
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()

        val byteArrayInputStream = ByteArrayInputStream(buffer)
        val neuralNetwork = ObjectInputStream(byteArrayInputStream).readObject() as NeuralNetwork
        inputStream.close()
        return neuralNetwork
    }

    override fun onResume() {
        drawingView.onResume()
        super.onResume()
    }

    override fun onPause() {
        drawingView.onPause()
        super.onPause()
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        return when (event.action and MotionEvent.ACTION_MASK) {
            ACTION_DOWN -> processTouchDown(event)
            ACTION_MOVE -> processTouchMove(event)
            ACTION_UP -> processTouchUp()
            else -> false
        }
    }

    private fun processTouchDown(event: MotionEvent): Boolean {
        lastPoint.set(event.toPoint())
        drawModel.startLine(drawingView.getLocalBitmapPosition(lastPoint.copy()))
        return true
    }

    private fun processTouchMove(event: MotionEvent): Boolean {
        drawModel.addPointToCurrentLine(drawingView.getLocalBitmapPosition(event.toPoint()))
        lastPoint.set(event.toPoint())
        drawingView.invalidate()
        return true
    }

    private fun processTouchUp(): Boolean {
        drawModel.endLine()
        drawingView.getImageData()?.let {
            neuralNetwork.feedForward(Tensor(arrayOf(it)))
            result.text = getDigit().toString()
        }

        return true
    }

    private fun getDigit(): Int {
        var maxValue = Double.MIN_VALUE
        var digit = 0
        neuralNetwork.output.data[0].forEachIndexed { index, value ->
            if (maxValue < value) {
                maxValue = value
                digit = index
            }
        }
        return digit
    }

    private fun onClearClicked() {
        drawModel.clear()
        drawingView.reset()
        drawingView.invalidate()
        result.text = ""
    }
}



private fun MotionEvent.toPoint() = Point(x, y)
