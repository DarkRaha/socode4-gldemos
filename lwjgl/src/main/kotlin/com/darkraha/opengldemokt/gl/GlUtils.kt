package com.darkraha.opengldemokt.gl



import org.joml.Matrix4f
import org.lwjgl.system.MemoryStack


import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryUtil


object GlUtils {
    val MATRIX_BUFFER = MemoryUtil.memAllocFloat(16)
    const val A_LOCATION_COORDS = 0
    const val A_LOCATION_COLORS = 1
    const val A_LOCATION_NORMALS = 2
    const val A_LOCATION_TEXCOORDS = 3
    const val U_MATRIX = "matrix"
    const val U_PROJ_MATRIX = "projMatrix"
    const val U_VIEW_MATRIX = "viewMatrix"
    const val U_MODEL_MATRIX = "modelMatrix"

    fun createVAO(): Int {
        val idVao = glGenVertexArrays()
        glBindVertexArray(idVao)
        return idVao
    }

    fun createVBO(data: FloatArray): Int {
        MemoryStack.stackPush().use { stack ->
            val idVbo = glGenBuffers()
            glBindBuffer(GL_ARRAY_BUFFER, idVbo)
            val fb = stack.mallocFloat(data.size)
            fb.put(data).flip()
            glBufferData(GL_ARRAY_BUFFER, fb, GL_STATIC_DRAW)
            return idVbo
        }
    }

    fun createIBO(data: ByteArray): Int {
        MemoryStack.stackPush().use { stack ->
            val byteBuffer = stack.malloc(data.size)
            byteBuffer.put(data).flip()
            val idIbo = glGenBuffers()
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idIbo)
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, byteBuffer, GL_STATIC_DRAW)
            //glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
            return idIbo
        }
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
                stride, 4L * numCoords
            )
        }
        if (texCoord) {
            glEnableVertexAttribArray(A_LOCATION_TEXCOORDS)
            glVertexAttribPointer(
                A_LOCATION_TEXCOORDS,
                2, GL_FLOAT, false,
                stride, 4L * (numCoords + numColorComponents)
            )
        }
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    fun bindMatrix(idProgram: Int, matrix: Matrix4f) {
        glUniformMatrix4fv(
            glGetUniformLocation(idProgram, U_MATRIX),
            false, matrix[MATRIX_BUFFER]
        )
    }


    fun bindMatrices(idProgram: Int, projMatrix: Matrix4f?, viewMatrix: Matrix4f?, modelMatrix: Matrix4f?) {

        projMatrix?.apply {
            glUniformMatrix4fv(
                glGetUniformLocation(idProgram, U_PROJ_MATRIX),
                false, this[MATRIX_BUFFER]
            )
        }

        viewMatrix?.apply {
            glUniformMatrix4fv(
                glGetUniformLocation(idProgram, U_VIEW_MATRIX),
                false, this[MATRIX_BUFFER]
            )
        }

        modelMatrix?.apply {
            glUniformMatrix4fv(
                glGetUniformLocation(idProgram, GlUtils.U_MODEL_MATRIX),
                false, this[GlUtils.MATRIX_BUFFER] )
        }

    }
}
