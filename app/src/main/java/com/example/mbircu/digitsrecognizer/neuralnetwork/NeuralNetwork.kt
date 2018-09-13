package neuralnetwork

import neuralnetwork.activation.Activation
import java.io.*
import java.util.*

class NeuralNetwork constructor(
    private val activation: Activation,
    val weights: Array<Tensor>,
    val activations: Array<Tensor>,
    val preActivations: Array<Tensor>,
    val isBiasEnabled: Boolean = false) : Serializable {

    lateinit var output: Tensor

    var bestResult: Double? = null

    val random = Random()

    fun feedForward(x: Tensor) {
        preActivations[0] = x.multiply(weights[0])
        if (isBiasEnabled) {
            preActivations[0].applyToEachElement { it + 1 }
        }
        activations[0] = preActivations[0].applyToEachElement(activation)
        for (i in 1 until weights.size) {
            preActivations[i] = activations[i - 1].multiply(weights[i])
            if (isBiasEnabled) {
                preActivations[1].applyToEachElement { it + 1 }
            }
            activations[i] = preActivations[i].applyToEachElement(activation)
        }
        output = activations.last().copy()
    }

    fun persist(filePath: String) {
        val file = File(filePath)
        val outputStream = ObjectOutputStream(FileOutputStream(file))
        outputStream.writeObject(this)
        outputStream.close()
        outputStream.flush()
    }

    companion object {

        fun builder(): NeuralNetworkBuilder = NeuralNetworkBuilder()

        fun load(filePath: String): NeuralNetwork {
            val file = File(filePath)
            val inputStream = ObjectInputStream(FileInputStream(file))
            val neuralNetwork = inputStream.readObject() as NeuralNetwork
            inputStream.close()
            return neuralNetwork
        }
    }
}