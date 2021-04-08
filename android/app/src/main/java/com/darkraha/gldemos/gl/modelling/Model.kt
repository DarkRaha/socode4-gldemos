package com.darkraha.gldemos.gl.modelling

import android.opengl.GLES30.*
import com.darkraha.gldemos.gl.ShaderProgramBuilder.Companion.A_LOCATION_VERTEX_COLOR
import com.darkraha.gldemos.gl.ShaderProgramBuilder.Companion.A_LOCATION_VERTEX_NORMAL
import com.darkraha.gldemos.gl.ShaderProgramBuilder.Companion.A_LOCATION_VERTEX_POS
import com.darkraha.gldemos.gl.ShaderProgramBuilder.Companion.A_LOCATION_VERTEX_TEXPOS
import org.joml.Vector3f
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Contains the vertex data of model in different arrays.
 */
class Model private constructor() {
    lateinit var name: String
    lateinit var vPos: FloatArray
    var vColor: FloatArray? = null
    var vTexPos: FloatArray? = null
    var vNormal: FloatArray? = null
    var vTangent: FloatArray? = null
    var vBitangent: FloatArray? = null
    var indices: IntArray? = null
    var color: FloatArray? = null
    var posComponets = 0
    var colorComponets = 0
    var texPosComponets = 0
    var normalComponets = 0
    var drawType = GL_TRIANGLES

    fun toGlModel(): GlModel {
        val id = intArrayOf(0)

        glGenVertexArrays(1, id, 0)
        val idVao = id[0]
        glBindVertexArray(idVao)

        var idIbo = 0
        val ids = IntArray(6)

        ids[0] = createVBO(vPos, A_LOCATION_VERTEX_POS, posComponets)
        var count = vPos.size / posComponets

        vColor?.apply {
            ids[1] = createVBO(this, A_LOCATION_VERTEX_COLOR, colorComponets)
        }

        vNormal?.apply {
            ids[2] = createVBO(this, A_LOCATION_VERTEX_NORMAL, normalComponets)
        }

        vTexPos?.apply {
            ids[3] = createVBO(this, A_LOCATION_VERTEX_TEXPOS, texPosComponets)
        }

        indices?.apply {
            idIbo = createIBO(this)
            count = this.size
        }

        glBindVertexArray(0)
        return GlModel(idVao, ids, idIbo, drawType, count, name)
    }

    fun calcTangent() {
        val edge1 = Vector3f()
        val edge2 = Vector3f()
    }

    //--------------------------------------------------------------
    companion object {

        fun createIBO(data: IntArray): Int {

            val buffer = ByteBuffer.allocateDirect(data.size * 4)
                .order(ByteOrder.nativeOrder()).asIntBuffer()
            buffer.put(data).position(0)

            val idArray = intArrayOf(0)
            glGenBuffers(1, idArray, 0)
            val idIbo = idArray[0]

            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idIbo)
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, data.size*4, buffer, GL_STATIC_DRAW)
            return idIbo
        }

        fun createVBO(data: FloatArray, locations: Int, size: Int): Int {

            val buffer = ByteBuffer.allocateDirect(data.size * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer()

            val idArray = intArrayOf(0)
            glGenBuffers(1, idArray, 0)
            val idVbo = idArray[0]
            glBindBuffer(GL_ARRAY_BUFFER, idVbo)
            buffer.put(data).position(0)
            glBufferData(GL_ARRAY_BUFFER, data.size * 4, buffer, GL_STATIC_DRAW)

            glEnableVertexAttribArray(locations)
            glVertexAttribPointer(
                locations,
                size, GL_FLOAT, false,
                0, 0
            )

            return idVbo
        }
    }

    //--------------------------------------------------------------
    class Builder {
        private val model = Model()

        fun coordinates(pos: FloatArray): Builder {
            model.vPos = pos
            model.posComponets = 3
            return this
        }

        fun coordinates2d(pos: FloatArray): Builder {
            model.vPos = pos
            model.posComponets = 2
            return this
        }

        fun colorsRGB(colorsRGB: FloatArray?): Builder {
            model.vColor = colorsRGB
            model.colorComponets = 3
            return this
        }

        fun textureCoordinates(st: FloatArray?): Builder {
            model.vTexPos = st
            model.texPosComponets = 2
            return this
        }

        fun textureCoordinates3(st: FloatArray?): Builder {
            model.vTexPos = st
            model.texPosComponets = 3
            return this
        }

        fun textureCoordinates4(st: FloatArray?): Builder {
            model.vTexPos = st
            model.texPosComponets = 4
            return this
        }

        fun normals(n: FloatArray?): Builder {
            model.vNormal = n
            model.normalComponets = 3
            return this
        }

        fun indices(i: IntArray?): Builder {
            model.indices = i
            return this
        }

        fun solidColor(r: Float, g: Float, b: Float, a: Float): Builder {
            model.color = floatArrayOf(r, g, b, a)
            return this
        }

        fun name(n: String): Builder {
            model.name = n
            return this
        }

        /**
         * @param t default GL_TRIANGLES
         * @return
         */
        fun drawType(t: Int): Builder {
            model.drawType = t
            return this
        }

        fun build(): Model {
            return model
        }
    }


}