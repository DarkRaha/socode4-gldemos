package com.darkraha.gldemos.gl

import android.opengl.GLES30.*
import org.joml.Matrix4f
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

object GlUtils {

    private val MATRIX_BUFFER = ByteBuffer.allocateDirect(16 * 4)
        .order(ByteOrder.nativeOrder()).asFloatBuffer()
    private var FLOAT_BUFFER: FloatBuffer = ByteBuffer.allocateDirect(100 * 4)
        .order(ByteOrder.nativeOrder()).asFloatBuffer()
    private var BYTE_BUFFER: ByteBuffer = ByteBuffer.allocateDirect(100)
    private val ID_ARRAY = intArrayOf(0)

    const val A_LOCATION_COORDS = 0
    const val A_LOCATION_COLORS = 1
    const val A_LOCATION_NORMALS = 2
    const val A_LOCATION_TEXCOORDS = 3
    const val U_MATRIX = "matrix"
    const val U_PROJ_MATRIX = "projMatrix"
    const val U_VIEW_MATRIX = "viewMatrix"
    const val U_MODEL_MATRIX = "modelMatrix"

    fun createVAO(): Int {
        glGenVertexArrays(1, ID_ARRAY,0)
        glBindVertexArray(ID_ARRAY[0])
        return ID_ARRAY[0]
    }


    fun createVBO(data: FloatArray): Int {
        glGenBuffers(1, ID_ARRAY, 0)
        val idVbo = ID_ARRAY[0]
        glBindBuffer(GL_ARRAY_BUFFER, idVbo)
        FLOAT_BUFFER.put(data).position(0)
        glBufferData(GL_ARRAY_BUFFER, data.size * 4, FLOAT_BUFFER, GL_STATIC_DRAW)
        return idVbo
    }

    fun createIBO(data: ByteArray): Int {
        BYTE_BUFFER.put(data).position(0)
        glGenBuffers(1, ID_ARRAY, 0)
        val idIbo = ID_ARRAY[0]
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idIbo)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, data.size, BYTE_BUFFER, GL_STATIC_DRAW)
        return idIbo
    }


    fun bindAttributes(idVBO: Int, numCoords: Int, numColorComponents: Int, texCoord: Boolean) {
        glBindBuffer(GL_ARRAY_BUFFER, idVBO)

        // size of float is 4
        val stride = 4 * (numCoords + numColorComponents + if (texCoord) 2 else 0)
        if (numCoords > 0) {
            glEnableVertexAttribArray(A_LOCATION_COORDS)
            glVertexAttribPointer(
                A_LOCATION_COORDS,
                numCoords, GL_FLOAT, false,
                stride, 0
            )
        }
        if (numColorComponents > 0) {
            glEnableVertexAttribArray(A_LOCATION_COLORS)
            glVertexAttribPointer(
                A_LOCATION_COLORS,
                numColorComponents, GL_FLOAT, true,
                stride, 4 * numCoords
            )
        }
        if (texCoord) {
            glEnableVertexAttribArray(A_LOCATION_TEXCOORDS)
            glVertexAttribPointer(
                A_LOCATION_TEXCOORDS,
                2, GL_FLOAT, false,
                stride, 4 * (numCoords + numColorComponents)
            )
        }
    }

    fun bindMatrix(idProgram: Int, matrix: Matrix4f) {
        glUniformMatrix4fv(
            glGetUniformLocation(idProgram, U_MATRIX),1,
            false, matrix[MATRIX_BUFFER]
        )
    }


    fun bindMatrices(idProgram: Int, projMatrix: Matrix4f?, viewMatrix: Matrix4f?, modelMatrix: Matrix4f?) {

        projMatrix?.apply {
            glUniformMatrix4fv(
                glGetUniformLocation(idProgram, U_PROJ_MATRIX),1,
                false, this[MATRIX_BUFFER]
            )
        }

        viewMatrix?.apply {
            glUniformMatrix4fv(
                glGetUniformLocation(idProgram, U_VIEW_MATRIX),1,
                false, this[MATRIX_BUFFER]
            )
        }

        modelMatrix?.apply {
            glUniformMatrix4fv(
                glGetUniformLocation(idProgram, U_MODEL_MATRIX),1,
                false, this[MATRIX_BUFFER]
            )
        }
    }
}