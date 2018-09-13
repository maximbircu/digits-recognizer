package neuralnetwork

import neuralnetwork.activation.Activation
import neuralnetwork.activation.Sigmoid

class NeuralNetworkBuilder {
    private var layers: IntArray? = null
    private var enableBias: Boolean = false
    private var activation: Activation = Sigmoid()

    private var rangeMin = -1.0
    private var rangeMax = 1.0

    fun withLayers(vararg layers: Int): NeuralNetworkBuilder {
        this.layers = layers
        rangeMin = -getOptimalRange()
        rangeMax = getOptimalRange()
        return this
    }

    private fun getOptimalRange(): Double {
        if (layers == null && layers?.size in Int.MIN_VALUE..3) {
            throw Exception("You should have at least 3 layers")
        }

        if (layers?.size == 3) {
            return Math.sqrt(6.0) / Math.sqrt(layers?.get(1)?.toDouble()!!)
        }
        return Math.sqrt(6.0) / Math.sqrt(layers?.get(layers?.size!! - 1)?.toDouble()!! + layers?.get(layers?.size!! - 2)?.toDouble()!!)
    }

    fun withBias(): NeuralNetworkBuilder {
        enableBias = true
        return this
    }

    fun withActivation(activation: Activation): NeuralNetworkBuilder {
        this.activation = activation
        return this
    }

    fun withInitialWeightsRange(rangeMin: Double, rangeMax: Double): NeuralNetworkBuilder {
        this.rangeMin = rangeMin
        this.rangeMax = rangeMax
        return this
    }

    fun build(): NeuralNetwork {
        if (layers == null && layers?.size in Int.MIN_VALUE..3) {
            throw Exception("You should have at least 3 layers")
        }

        val weights: Array<Tensor> = Array(layers?.size!! - 1, { Tensor.nextTensor(rangeMin, rangeMax, layers!![it], layers!![it + 1]) })
        val activations: Array<Tensor> = Array(layers?.size!! - 1, { Tensor() })
        val preActivations: Array<Tensor> = Array(layers?.size!! - 1, { Tensor() })
        return NeuralNetwork(Sigmoid(), weights, activations, preActivations, enableBias)
    }
}
