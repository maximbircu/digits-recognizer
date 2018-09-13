package neuralnetwork

import org.apache.commons.math3.exception.DimensionMismatchException
import org.apache.commons.math3.exception.NotStrictlyPositiveException
import org.apache.commons.math3.linear.MatrixUtils
import org.apache.commons.math3.linear.RealMatrix
import java.io.Serializable
import java.util.*

class Tensor : Serializable {

    private var matrix: RealMatrix? = null

    private constructor(matrix: RealMatrix) {
        this.matrix = matrix
    }

    constructor() {
        matrix = MatrixUtils.createRealMatrix(1, 1)
    }


    @Throws(NotStrictlyPositiveException::class)
    constructor(rows: Int, columns: Int) {
        matrix = MatrixUtils.createRealMatrix(rows, columns)
    }

    @Throws(DimensionMismatchException::class, NotStrictlyPositiveException::class)
    constructor(rawData: Array<DoubleArray>) {
        matrix = MatrixUtils.createRealMatrix(rawData)
    }

    private fun randInit(rangeMin: Double, rangeMax: Double) {
        for (i in 0 until matrix!!.rowDimension) {
            for (j in 0 until matrix!!.columnDimension) {
                matrix!!.setEntry(i, j, getRand(rangeMin, rangeMax))
            }
        }
    }

    private fun getRand(rangeMin: Double, rangeMax: Double): Double {
        val r = Random()
        return rangeMin + (rangeMax - rangeMin) * r.nextDouble()
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        for (rows in matrix!!.data) {
            for (valueInRow in rows) {
                stringBuilder.append(String.format(if (valueInRow > 0) " %f " else "%f ", valueInRow))
            }
            stringBuilder.append("\n")
        }
        return stringBuilder.toString()
    }

    // Operations.

    fun add(tensor: Tensor): Tensor {
        return Tensor(this.matrix!!.add(tensor.matrix))
    }

    fun subtract(tensor: Tensor): Tensor {
        return Tensor(this.matrix!!.subtract(tensor.matrix))
    }

    fun multiply(tensor: Tensor): Tensor {
        return Tensor(this.matrix!!.multiply(tensor.matrix))
    }

    fun multiply(scalar: Double): Tensor {
        return Tensor(this.matrix!!.scalarMultiply(scalar))
    }

    fun preMultiply(tensor: Tensor): Tensor {
        return Tensor(this.matrix!!.preMultiply(tensor.matrix))
    }

    fun transpose(): Tensor {
        return Tensor(this.matrix!!.transpose())
    }

    fun copy(): Tensor {
        return Tensor(matrix!!.copy())
    }

    val rowsCount: Int
        get() = matrix!!.rowDimension

    val columnsCount: Int
        get() = matrix!!.columnDimension

    fun sum(): Double {
        var totalSum = 0.0
        for (rows in matrix!!.data) {
            for (valueInRow in rows) {
                totalSum += valueInRow
            }
        }
        return totalSum
    }

    fun square(): Tensor {
        val resultMatrix = matrix!!.copy()
        for (i in 0 until resultMatrix.rowDimension) {
            for (j in 0 until resultMatrix.columnDimension) {
                resultMatrix.setEntry(i, j, Math.pow(resultMatrix.getEntry(i, j), 2.0))
            }
        }
        return Tensor(resultMatrix)
    }

    fun applyToEachElement(functionToApply: (x: Double) -> Double): Tensor {
        val resultMatrix = matrix!!.copy()
        for (i in 0 until matrix!!.rowDimension) {
            for (j in 0 until resultMatrix.columnDimension) {
                resultMatrix.setEntry(i, j, functionToApply(resultMatrix.getEntry(i, j)))
            }
        }
        return Tensor(resultMatrix)
    }

    fun multiplyElementWise(tensor: Tensor): Tensor {
        val resultMatrix = tensor.matrix!!.copy()
        for (i in 0 until resultMatrix.rowDimension) {
            for (j in 0 until resultMatrix.columnDimension) {
                resultMatrix.setEntry(i, j, this.matrix!!.getEntry(i, j) * resultMatrix.getEntry(i, j))
            }
        }
        return Tensor(resultMatrix)
    }

    val data: Array<DoubleArray>
        get() = matrix!!.data

    companion object {

        fun nextTensor(rows: Int, columns: Int): Tensor {
            return nextTensor(-1.0, 1.0, rows, columns)
        }

        fun nextTensor(rangeMin: Double, rangeMax: Double, rows: Int, columns: Int): Tensor {
            val tensorTest = Tensor(rows, columns)
            tensorTest.randInit(rangeMin, rangeMax)
            return tensorTest
        }
    }
}
