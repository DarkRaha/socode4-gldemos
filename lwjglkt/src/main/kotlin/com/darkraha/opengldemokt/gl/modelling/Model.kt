package com.darkraha.opengldemokt.gl.modelling

import com.darkraha.opengldemokt.gl.GlUtils
import org.joml.Vector3f
import org.lwjgl.opengl.GL33.*


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
        val ids = IntArray(6)
        ids[0] = glGenVertexArrays()
        glBindVertexArray(ids[0])

        ids[1] = GlUtils.createVBO(vPos, GlUtils.A_LOCATION_COORDS, posComponets)
        var count = vPos.size / posComponets

        vColor?.apply {
            ids[2] = GlUtils.createVBO(this, GlUtils.A_LOCATION_COLORS, colorComponets)
        }

        vNormal?.apply {
            ids[3] = GlUtils.createVBO(this, GlUtils.A_LOCATION_NORMALS, normalComponets)
        }

        vTexPos?.apply {
            ids[4] = GlUtils.createVBO(this, GlUtils.A_LOCATION_TEXCOORDS, texPosComponets)
        }

        indices?.apply {
            ids[5] = GlUtils.createIBO(this)
            count = this.size
        }

        glBindVertexArray(0)
        return GlModel(ids, drawType, count, name)
    }

    fun calcTangent() {
        val edge1 = Vector3f()
        val edge2 = Vector3f()
    }

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