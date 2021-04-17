package com.darkraha.gldemos.gl.modelling

import android.opengl.GLES30.*
import com.darkraha.opengldemokt.gl.shader.ShaderProgramBuilder.Companion.A_LOCATION_VERTEX_BITANGENT
import com.darkraha.opengldemokt.gl.shader.ShaderProgramBuilder.Companion.A_LOCATION_VERTEX_COLOR
import com.darkraha.opengldemokt.gl.shader.ShaderProgramBuilder.Companion.A_LOCATION_VERTEX_NORMAL
import com.darkraha.opengldemokt.gl.shader.ShaderProgramBuilder.Companion.A_LOCATION_VERTEX_POS
import com.darkraha.opengldemokt.gl.shader.ShaderProgramBuilder.Companion.A_LOCATION_VERTEX_TANGENT
import com.darkraha.opengldemokt.gl.shader.ShaderProgramBuilder.Companion.A_LOCATION_VERTEX_TEXPOS
import org.joml.Vector2f
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



    fun calcTangent(): Model {
        vTangent = FloatArray(vPos.size)
        calcTangent(vPos, vTexPos!!, indices, vTangent!!, null)
        return this
    }

    fun calcTangentBitangent(): Model {
        vTangent = FloatArray(vPos.size)
        vBitangent = FloatArray(vPos.size)
        calcTangent(vPos, vTexPos!!, indices, vTangent!!, vBitangent)
        return this
    }



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

        vTangent?.apply {
            ids[4] = createVBO(this, A_LOCATION_VERTEX_TANGENT, 3)
        }

        vBitangent?.apply {
            ids[5] = createVBO(this, A_LOCATION_VERTEX_BITANGENT, 3)
        }



        indices?.apply {
            idIbo = createIBO(this)
            count = this.size
        }

        glBindVertexArray(0)
        return GlModel(idVao, ids, idIbo, drawType, count, name)
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


    /**
     * Calculates tangents/bitangents for bumping.
     *
     * @param vPos       array of the vertex positions (x,y,z)
     * @param vTexPos    array of the vertex texture positions (s,t)
     * @param srcIndices    indices of vertices to draw model with triangles
     * @param vTangent   array to store calculated tangent vectors (x,y,z)
     * @param vBitangent array to store calculated tangent vectors (x,y,z)
     */
    fun calcTangent(
        vPos: FloatArray, vTexPos: FloatArray,
        srcIndices: IntArray?,
        vTangent: FloatArray, vBitangent: FloatArray?
    ) {
        val posComponents = 3
        val texPosComponets = 2
        var offset: Int

        val indices : IntArray = srcIndices ?: IntArray(vPos.size / posComponents)

        if (srcIndices == null) {
            for (i in indices.indices) {
                indices[i] = i
            }
        }

        // coordinates of points of triangle
        val p0 = Vector3f()
        val p1 = Vector3f()
        val p2 = Vector3f()

        // texture coordinates of points of triangle
        val pTex0 = Vector2f()
        val pTex1 = Vector2f()
        val pTex2 = Vector2f()

        // for storing calculated edges (p1,p0) and (p2,p0)
        val deltaP10 = Vector3f()
        val deltaP20 = Vector3f()

        // for storing calculated edges
        // in texture coordinates (pTex1, pTex0) and (pTex2,pTex0)
        val deltaTex10 = Vector2f()
        val deltaTex20 = Vector2f()

        //
        val tmp1 = Vector3f()
        val tmp2 = Vector3f()
        val tangent = Vector3f()
        val bitangent = Vector3f()
        var i = 0
        while (i < indices.size) {
            offset = indices[i] * posComponents
            p0[vPos[offset], vPos[offset + 1]] = vPos[offset + 2]

            offset = indices[i + 1] * posComponents
            p1[vPos[offset], vPos[offset + 1]] = vPos[offset + 2]

            offset = indices[i + 2] * posComponents
            p2[vPos[offset], vPos[offset + 1]] = vPos[offset + 2]

            offset = indices[i] * texPosComponets
            pTex0[vTexPos[offset]] = vTexPos[offset + 1]

            offset = indices[i + 1] * texPosComponets
            pTex1[vTexPos[offset]] = vTexPos[offset + 1]

            offset = indices[i + 2] * texPosComponets
            pTex2[vTexPos[offset]] = vTexPos[offset + 1]


            // calc edges
            p1.sub(p0, deltaP10)
            p2.sub(p0, deltaP20)
            pTex1.sub(pTex0, deltaTex10)
            pTex2.sub(pTex0, deltaTex20)
            val r = 1.0f / (deltaTex10.x * deltaTex20.y - deltaTex10.y * deltaTex20.x)

            // tangent = (deltaP10 * deltaTex20.y   - deltaP20 * deltaTex10.y)*r;
            tmp1.set(deltaP10).mul(deltaTex20.y)
                .sub(tmp2.set(deltaP20).mul(deltaTex10.y), tangent)
            tangent.mul(r)

            // add same tangent for each points of triangle
            // if point already had tangent we will
            // average tangent from old value and new value
            offset = indices[i] * posComponents
            vTangent[offset] += tangent.x
            vTangent[offset + 1] += tangent.y
            vTangent[offset + 2] += tangent.z
            offset = indices[i + 1] * posComponents
            vTangent[offset] += tangent.x
            vTangent[offset + 1] += tangent.y
            vTangent[offset + 2] += tangent.z
            offset = indices[i + 2] * posComponents
            vTangent[offset] += tangent.x
            vTangent[offset + 1] += tangent.y
            vTangent[offset + 2] += tangent.z

            vBitangent?.also {
                // bitangent = (deltaP20 * deltaTex10.x   - deltaP10 * deltaTex20.x)*r;
                tmp1.set(deltaP20).mul(deltaTex10.x)
                    .sub(tmp2.set(deltaP10).mul(deltaTex20.x), bitangent)
                bitangent.mul(r)

                // add same bitangent for each points of triangle
                // if point already had bitangent we will
                // average bitangent from old value and new value
                offset = indices[i] * posComponents
                it[offset] += bitangent.x
                it[offset + 1] += bitangent.y
                it[offset + 2] += bitangent.z
                offset = indices[i + 1] * posComponents

                it[offset] += bitangent.x
                it[offset + 1] += bitangent.y
                it[offset + 2] += bitangent.z

                offset = indices[i + 2] * posComponents
                it[offset] += bitangent.x
                it[offset + 1] += bitangent.y
                it[offset + 2] += bitangent.z
            }
            i += 3


        }

        normalize3(vTangent)

        vBitangent?.apply {
            normalize3(this)
        }

    }


    fun normalize3(src: FloatArray) {
        val v = Vector3f()
        var i = 0

        while (i < src.size) {
            v[src[i], src[i + 1]] = src[i + 2]
            v.normalize()
            src[i] = v.x
            src[i + 1] = v.y
            src[i + 2] = v.z
            i += 3
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