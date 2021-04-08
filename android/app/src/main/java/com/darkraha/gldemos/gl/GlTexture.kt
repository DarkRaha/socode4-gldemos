package com.darkraha.gldemos.gl


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES30.*
import android.opengl.GLUtils

class GlTexture(val idTexture: Int, val textureUnit: Int, val textureType: Int, val name: String) {
    constructor(idTexture: Int, name: String) : this(idTexture, GL_TEXTURE0, GL_TEXTURE_2D, name)

    fun dispose() {
        glBindTexture(textureType, 0)
        glDeleteTextures(1, intArrayOf(idTexture), 0)
    }

    class TextureBuilder(val idRes: Int, var mName: String) {

        private val idArray = intArrayOf(0)
        var mIdTexture = 0
        var mTextureUnit = GL_TEXTURE0
        var mTextureType = 0
        var mBorder = 0
        var mWidth = 0
        var mHeight = 0
        var mFormat = GL_RGBA
        var mImageData: Bitmap? = null
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

            glGenTextures(1, idArray, 0)
            mIdTexture = idArray[0]

            glBindTexture(mTextureType, mIdTexture)
            loadTexture(idRes, this)

            GLUtils.texImage2D(GL_TEXTURE_2D, 0, mImageData, 0);


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
            mImageData?.recycle()
            glBindTexture(GL_TEXTURE_2D, 0)
            return GlTexture(mIdTexture, mTextureUnit, mTextureType, mName)
        }
    }

    companion object {
        fun newTexture2D(idRes: Int, name: String): GlTexture {
            return TextureBuilder(idRes, name).build2D()
        }


        fun loadTexture(idRes: Int, tbuilder: TextureBuilder) {
            val options = BitmapFactory.Options()
            options.inScaled = false // without pre-scaling

            // Read in the resource
            // on the pixel emulator  a 1024x1024 jpg texture worked successful
            tbuilder.mImageData = BitmapFactory.decodeResource(GlCommon.appContext.resources, idRes, options)
        }
    }
}