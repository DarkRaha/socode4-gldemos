package com.darkraha.opengldemokt.gl


import org.lwjgl.system.MemoryStack


import org.lwjgl.opengl.GL33.*
import org.lwjgl.stb.STBImage
import org.lwjgl.system.MemoryUtil
import java.io.File
import java.lang.Exception
import java.nio.ByteBuffer



object GlUtils {
    const val A_LOCATION_COORDS = 0
    const val A_LOCATION_COLORS = 1
    const val A_LOCATION_NORMALS = 2
    const val A_LOCATION_TEXCOORDS = 3

    /**
     * Create 2D texture and upload data to it.
     *
     * @param idTex
     * @param imageData
     * @param width
     * @param height
     * @return
     */
    fun createTexture(
        idTex: Int, imageData: ByteBuffer?,
        width: Int, height: Int
    ): Int {
        val idTexture = if (idTex == 0) glGenTextures() else idTex
        glBindTexture(GL_TEXTURE_2D, idTexture)
        glTexImage2D(
            GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0,
            GL_RGBA, GL_UNSIGNED_BYTE, imageData
        )
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        return idTexture
    }

    fun loadTex2DResDefault(idTex: Int, resPath: String?): Int {
        var idTexture = 0
        try {
            MemoryStack.stackPush().use { stack ->
                val w = stack.mallocInt(1)
                val h = stack.mallocInt(1)
                val channels = stack.mallocInt(1)
                val resource = Thread.currentThread().javaClass.getResource(resPath)
                val filePath = File(resource.toURI()).absolutePath // encode spaces
                val buffer = STBImage.stbi_load(filePath, w, h, channels, 4)
                    ?: throw Exception("Can't load file " + filePath + " " + STBImage.stbi_failure_reason())
                idTexture = createTexture(idTex, buffer, w.get(), h.get())
                STBImage.stbi_image_free(buffer)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return idTexture
    }


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


    fun createVBO(data: FloatArray, locations: Int, size: Int): Int {
        MemoryStack.stackPush().use { stack ->
            val idVbo = glGenBuffers()
            glBindBuffer(GL_ARRAY_BUFFER, idVbo)
            val fb = stack.mallocFloat(data.size)
            fb.put(data).flip()
            glBufferData(GL_ARRAY_BUFFER, fb, GL_STATIC_DRAW)
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
     * @param coords    x,y,z
     * @param colors    r,g,b,a
     * @param texcoords s,t
     * @param indices
     * @return
     */
    fun prepareVertexData(
        coords: FloatArray?,
        colors: FloatArray?,
        texcoords: FloatArray?,
        indices: ByteArray?
    ): IntArray? {
        return prepareVertexData(coords, colors, texcoords, indices, null)
    }


    fun prepareVertexData(
        coords: FloatArray?,
        colors: FloatArray?,
        texcoords: FloatArray?,
        indices: ByteArray?,
        normals: FloatArray?
    ): IntArray? {
        val ret = IntArray(5)
        if (coords != null) {
            ret[0] = createVBO(coords)
            glEnableVertexAttribArray(GlUtils.A_LOCATION_COORDS)
            glVertexAttribPointer(
                GlUtils.A_LOCATION_COORDS,
                3, GL_FLOAT, false,
                0, 0
            )
        }
        if (colors != null) {
            ret[1] = createVBO(colors)
            glEnableVertexAttribArray(GlUtils.A_LOCATION_COLORS)
            glVertexAttribPointer(
                GlUtils.A_LOCATION_COLORS,
                4, GL_FLOAT, false,
                0, 0
            )
        }
        if (texcoords != null) {
            ret[2] = createVBO(texcoords)
            glEnableVertexAttribArray(GlUtils.A_LOCATION_TEXCOORDS)
            glVertexAttribPointer(
                GlUtils.A_LOCATION_TEXCOORDS,
                2, GL_FLOAT, false,
                0, 0
            )
        }
        if (indices != null) {
            ret[3] = createIBO(indices)
        }
        if (normals != null) {
            ret[4] = createVBO(normals)
            glEnableVertexAttribArray(GlUtils.A_LOCATION_NORMALS)
            glVertexAttribPointer(
                GlUtils.A_LOCATION_NORMALS,
                3, GL_FLOAT, false,
                0, 0
            )
        }
        return ret
    }

    fun createIBO(data: ByteArray): Int {
        MemoryStack.stackPush().use { stack ->
            val byteBuffer = stack.malloc(data.size)
            byteBuffer.put(data).flip()
            val idIbo = glGenBuffers()
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idIbo)
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, byteBuffer, GL_STATIC_DRAW)
            return idIbo
        }
    }


    fun createIBO(data: IntArray): Int {
        MemoryStack.stackPush().use { stack ->
            val byteBuffer = stack.malloc(data.size * 4).asIntBuffer()
            byteBuffer.put(data).flip()
            val idIbo = glGenBuffers()
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idIbo)
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, byteBuffer, GL_STATIC_DRAW)
            return idIbo
        }
    }

    fun bindAttributes(idVBO: Int, numCoords: Int, numColorComponents: Int, texCoord: Boolean) {
        glBindBuffer(GL_ARRAY_BUFFER, idVBO)

        // size of float is 4
        val stride = 4 * (numCoords + numColorComponents + if (texCoord) 2 else 0)
        if (numCoords > 0) {
            glEnableVertexAttribArray(GlUtils.A_LOCATION_COORDS)
            glVertexAttribPointer(
                GlUtils.A_LOCATION_COORDS,
                numCoords, GL_FLOAT, false,
                stride, 0
            )
        }
        if (numColorComponents > 0) {
            glEnableVertexAttribArray(GlUtils.A_LOCATION_COLORS)
            glVertexAttribPointer(
                GlUtils.A_LOCATION_COLORS,
                numColorComponents, GL_FLOAT, true,
                stride, 4L * numCoords
            )
        }
        if (texCoord) {
            glEnableVertexAttribArray(GlUtils.A_LOCATION_TEXCOORDS)
            glVertexAttribPointer(
                GlUtils.A_LOCATION_TEXCOORDS,
                2, GL_FLOAT, false,
                stride, 4L * (numCoords + numColorComponents)
            )
        }
    }


}
