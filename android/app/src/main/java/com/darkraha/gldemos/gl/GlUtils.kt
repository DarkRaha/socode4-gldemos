package com.darkraha.gldemos.gl

import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLES30.*
import android.opengl.GLUtils
import org.joml.Matrix4f
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import android.graphics.BitmapFactory
import android.opengl.GLES20


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

    fun createTexture(idTex: Int, imageData: Bitmap): Int {

        val idTexture = if (idTex == 0) {
            glGenTextures(1, ID_ARRAY, 0)
            ID_ARRAY[0]
        } else {
            idTex
        }

        glBindTexture(GL_TEXTURE_2D, idTexture)

        GLUtils.texImage2D(GL_TEXTURE_2D, 0, imageData, 0);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        return idTexture
    }

    fun loadTex2DResDefault(idTexture: Int, context: Context, resId: Int): Int {

        val options = BitmapFactory.Options()
        options.inScaled = false // without pre-scaling

        // Read in the resource
        // on the pixel emulator  a 1024x1024 jpg texture worked successful
        val bitmap = BitmapFactory.decodeResource(context.resources, resId, options)

        val retIdTexture = createTexture(idTexture, bitmap)

        // recycle the bitmap, since its data has been loaded into OpenGL.
        bitmap.recycle()
        return retIdTexture
    }

    fun createVAO(): Int {
        glGenVertexArrays(1, ID_ARRAY, 0)
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
            glGetUniformLocation(idProgram, U_MATRIX), 1,
            false, matrix[MATRIX_BUFFER]
        )
    }


    fun bindMatrices(idProgram: Int, projMatrix: Matrix4f?, viewMatrix: Matrix4f?, modelMatrix: Matrix4f?) {

        projMatrix?.apply {
            glUniformMatrix4fv(
                glGetUniformLocation(idProgram, U_PROJ_MATRIX), 1,
                false, this[MATRIX_BUFFER]
            )
        }

        viewMatrix?.apply {
            glUniformMatrix4fv(
                glGetUniformLocation(idProgram, U_VIEW_MATRIX), 1,
                false, this[MATRIX_BUFFER]
            )
        }

        modelMatrix?.apply {
            glUniformMatrix4fv(
                glGetUniformLocation(idProgram, U_MODEL_MATRIX), 1,
                false, this[MATRIX_BUFFER]
            )
        }
    }

    fun delete(idVao: Int, idVbo: Int, idIbo: Int, idTex: Int) {
        if (idVao > 0) {
            ID_ARRAY[0] = idVao
            glDeleteVertexArrays(1, ID_ARRAY, 0)
        }

        if (idVbo > 0) {
            ID_ARRAY[0] = idVbo
            glDeleteBuffers(1, ID_ARRAY, 0)
        }

        if (idIbo > 0) {
            ID_ARRAY[0] = idIbo
            glDeleteBuffers(1, ID_ARRAY, 0)
        }

        if (idTex > 0) {
            ID_ARRAY[0] = idTex
            glDeleteTextures(1, ID_ARRAY, 0)
        }

    }
}