package com.darkraha.gldemos.gl

import org.joml.Matrix4f

class Matrices {

    val projection = Matrix4f().perspective(45 * TO_RAD, 1f, 1f, 100f)
    val view = Matrix4f().lookAlong(0f, 0f, -1f, 0f, 1f, 0f)
    lateinit var model: Matrix4f
    val viewModel = Matrix4f()
    val normals = Matrix4f()
    val matrix = Matrix4f()

    fun applyModel(modelMatrix: Matrix4f) {
        model = modelMatrix
        view.mul(modelMatrix, viewModel)
        matrix.set(projection).mul(viewModel)
        viewModel.invert(normals)
        normals.transpose()
    }

    companion object {
        const val PI_F = Math.PI.toFloat()
        val TO_RAD: Float = PI_F / 180f
    }
}