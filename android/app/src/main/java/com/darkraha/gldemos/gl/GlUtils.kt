package com.darkraha.gldemos.gl

import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLES30.*
import android.opengl.GLUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import android.graphics.BitmapFactory
import com.darkraha.opengldemokt.gl.shader.ShaderProgramBuilder


object GlUtils {

    private var FLOAT_BUFFER: FloatBuffer = ByteBuffer.allocateDirect(100 * 4)
        .order(ByteOrder.nativeOrder()).asFloatBuffer()

    private var BYTE_BUFFER: ByteBuffer = ByteBuffer.allocateDirect(100)
    private val ID_ARRAY = intArrayOf(0)


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
            glEnableVertexAttribArray(ShaderProgramBuilder.A_LOCATION_VERTEX_POS)
            glVertexAttribPointer(
                ShaderProgramBuilder.A_LOCATION_VERTEX_POS,
                numCoords, GL_FLOAT, false,
                stride, 0
            )
        }
        if (numColorComponents > 0) {
            glEnableVertexAttribArray(  ShaderProgramBuilder.A_LOCATION_VERTEX_COLOR)
            glVertexAttribPointer(
                ShaderProgramBuilder.A_LOCATION_VERTEX_COLOR,
                numColorComponents, GL_FLOAT, true,
                stride, 4 * numCoords
            )
        }
        if (texCoord) {
            glEnableVertexAttribArray(ShaderProgramBuilder.A_LOCATION_VERTEX_TEXPOS)
            glVertexAttribPointer(
                ShaderProgramBuilder.A_LOCATION_VERTEX_TEXPOS,
                2, GL_FLOAT, false,
                stride, 4 * (numCoords + numColorComponents)
            )
        }
    }


    /**
     * @param coords    x,y,z
     * @param colors    r,g,b,a
     * @param texcoords s,t
     * @param indices
     * @param normals
     * @return
     */
    fun prepareVertexData(
        coords: FloatArray?,
        colors: FloatArray?,
        texcoords: FloatArray?,
        indices: ByteArray?,
        normals: FloatArray? = null
    ): IntArray {
        val ret = IntArray(5)

        if (coords != null) {
            ret[0] = createVBO(coords)
            glEnableVertexAttribArray(ShaderProgramBuilder.A_LOCATION_VERTEX_POS)
            glVertexAttribPointer( ShaderProgramBuilder.A_LOCATION_VERTEX_POS,3, GL_FLOAT, false,0, 0)
        }
        if (colors != null) {
            ret[1] = createVBO(colors)
            glEnableVertexAttribArray(ShaderProgramBuilder.A_LOCATION_VERTEX_COLOR)
            glVertexAttribPointer( ShaderProgramBuilder.A_LOCATION_VERTEX_COLOR,4, GL_FLOAT, false,0, 0)
        }
        if (texcoords != null) {
            ret[2] = createVBO(texcoords)
            glEnableVertexAttribArray(ShaderProgramBuilder.A_LOCATION_VERTEX_TEXPOS)
            glVertexAttribPointer(ShaderProgramBuilder.A_LOCATION_VERTEX_TEXPOS,2, GL_FLOAT, false,0, 0)
        }
        if (indices != null) {
            ret[3] = createIBO(indices)
        }
        if (normals != null) {
            ret[4] = createVBO(normals)
            glEnableVertexAttribArray(ShaderProgramBuilder.A_LOCATION_VERTEX_NORMAL)
            glVertexAttribPointer(ShaderProgramBuilder.A_LOCATION_VERTEX_NORMAL, 3, GL_FLOAT, false, 0, 0)
        }

        return ret
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