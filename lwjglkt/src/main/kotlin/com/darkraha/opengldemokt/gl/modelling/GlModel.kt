package com.darkraha.opengldemokt.gl.modelling



import org.lwjgl.opengl.GL33.*

class GlModel(private val ids: IntArray, val drawType: Int, val count: Int, val name: String) {
    val vao: Int
        get() = ids[0]

    fun draw() {
        glBindVertexArray(ids[0])
        if (ids[IND_INDICES] > 0) {
            glDrawElements(drawType, count, GL_UNSIGNED_INT, 0)
        } else {
            glDrawArrays(drawType, 0, count)
        }
    }

    fun dispose() {
        glDeleteVertexArrays(ids[0])
        ids[0] = 0
        glDeleteBuffers(ids)
    }

    companion object {
        const val IND_VAO = 0
        const val IND_COORDS = 1
        const val IND_COLORS = 2
        const val IND_NORMALS = 3
        const val IND_TEXCOORDS = 4
        const val IND_INDICES = 5
    }
}