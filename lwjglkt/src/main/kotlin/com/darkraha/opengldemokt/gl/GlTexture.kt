package com.darkraha.opengldemokt.gl


import org.lwjgl.opengl.GL33.*

import org.lwjgl.stb.STBImage
import org.lwjgl.system.MemoryStack
import java.io.File
import java.lang.Exception
import java.nio.ByteBuffer

class GlTexture(val idTexture: Int, val textureUnit: Int, val textureType: Int, val name: String) {
    constructor(idTexture: Int, name: String) : this(idTexture, GL_TEXTURE0, GL_TEXTURE_2D, name) {}

    fun dispose() {
        glBindTexture(textureType, 0)
        glDeleteTextures(idTexture)
    }

    class TextureBuilder(var mResPath: String, var mName: String) {
        var mIdTexture = 0
        var mTextureUnit = GL_TEXTURE0
        var mTextureType = 0
        var mBorder = 0
        var mWidth = 0
        var mHeight = 0
        var mFormat = GL_RGBA
        var mImageData: ByteBuffer? = null
        var mGenMipmap = true
        var mClampToEdge = false
        fun genMipmap(v: Boolean): TextureBuilder {
            mGenMipmap = v
            return this
        }

        fun clampToEdge(v: Boolean): TextureBuilder {
            mClampToEdge = v
            return this
        }

        fun build2D(): GlTexture {
            mTextureType = GL_TEXTURE_2D
            mIdTexture = glGenTextures()
            glBindTexture(mTextureType, mIdTexture)
            loadTexture(mResPath, this)

            glTexImage2D(
                mTextureType, 0, GL_RGBA, mWidth, mHeight, 0,
                GL_RGBA, GL_UNSIGNED_BYTE, mImageData
            )

            glTexParameteri(mTextureType, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
            glTexParameteri(mTextureType, GL_TEXTURE_MAG_FILTER, GL_LINEAR)

            if (mGenMipmap) {
                glGenerateMipmap(mTextureType)
                glTexParameteri(mTextureType, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR)
            }
            if (mClampToEdge) {
                glTexParameteri(mTextureType, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
                glTexParameteri(mTextureType, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
            }
            STBImage.stbi_image_free(mImageData)
            glBindTexture(GL_TEXTURE_2D, 0)
            return GlTexture(mIdTexture, mTextureUnit, mTextureType, mName)
        }
    }

    companion object {
        fun newTexture2D(resPath: String, name: String): GlTexture {
            return TextureBuilder(resPath, name).build2D()
        }

        fun newSolidColorTexture( r: Int, g: Int, b: Int, name: String): GlTexture {
            val level = 0
            val width = 1
            val height = 1
            val border = 0
            val idTexture = glGenTextures()
            glBindTexture(GL_TEXTURE_2D, idTexture)
            MemoryStack.stackPush().use { stack ->
                val buffer = stack.bytes(
                    r.toByte(), g.toByte(), b.toByte(),
                    0xff.toByte()
                )
                glTexImage2D(
                    GL_TEXTURE_2D, level, GL_RGBA, width, height, border,
                    GL_RGBA, GL_UNSIGNED_BYTE, buffer
                )
            }
            glBindTexture(GL_TEXTURE_2D, 0)
            return GlTexture(idTexture, GL_TEXTURE0, GL_TEXTURE_2D, name)
        }

        fun loadTexture(resPath: String?, tbuilder: TextureBuilder) {
            try {
                MemoryStack.stackPush().use { stack ->
                    val w = stack.mallocInt(1)
                    val h = stack.mallocInt(1)
                    val channels = stack.mallocInt(1)
                    val resource = Thread.currentThread().javaClass.getResource(resPath)
                    val filePath = File(resource.toURI()).absolutePath // encode spaces
                    val buffer = STBImage.stbi_load(filePath, w, h, channels, 4)
                        ?: throw Exception("Can't load file " + filePath + " " + STBImage.stbi_failure_reason())
                    tbuilder.mImageData = buffer
                    tbuilder.mWidth = w.get()
                    tbuilder.mHeight = h.get()
                    tbuilder.mFormat = GL_RGBA
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}