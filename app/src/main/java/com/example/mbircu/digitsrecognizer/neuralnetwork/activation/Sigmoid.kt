package neuralnetwork.activation

class Sigmoid : Activation() {
    override fun invoke(x: Double) = 1 / (1 + Math.exp(-x))
}
