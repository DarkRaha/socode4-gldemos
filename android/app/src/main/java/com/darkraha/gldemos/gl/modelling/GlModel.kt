package com.darkraha.gldemos.gl.modelling
import android.opengl.GLES30.*
import com.darkraha.gldemos.gl.GlCommon

class GlModel(
    val idVao: Int,
    private val ids: IntArray,
    val idIbo: Int,
    val drawType: Int,
    val count: Int,
    val name: String
) {

    constructor(idVao: Int, ids: IntArray, drawType: Int, count: Int, name: String)
            : this(idVao, ids, 0, drawType, count, name)

    fun draw() {
        glBindVertexArray(idVao)
        if (idIbo > 0) {
            glDrawElements(drawType, count, GL_UNSIGNED_INT, 0)
        } else {
            glDrawArrays(drawType, 0, count)
        }
    }

    fun dispose() {
        val idArray = GlCommon.idArray
        idArray[0]=idVao
        idArray[1]=idIbo
        glDeleteVertexArrays(1, idArray, 0)
        glDeleteBuffers(ids.size, ids,0)
        glDeleteBuffers(1, idArray,1)
    }
}