package neuralnetwork.activation

class SigmoidPrime : ActivationPrime() {
    override fun invoke(x: Double) = Math.exp(-x) / Math.pow(1 + Math.exp(-x), 2.0)
}
