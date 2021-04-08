package com.darkraha.opengldemoj.gl;

import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_image_free;

public class GlTexture {
    public final String name;
    public final int idTexture;
    public final int textureUnit;
    public final int textureType;



    public GlTexture(int idTexture, String name) {
        this(idTexture, GL_TEXTURE0, GL_TEXTURE_2D, name);
    }

    public GlTexture(int idTexture, int textureUnitDefault, int textureType, String name) {
        this.idTexture = idTexture;
        this.name = name;
        this.textureUnit = textureUnitDefault;
        this.textureType = textureType;
    }


    public void dispose() {
        glBindTexture(textureType,0);
        glDeleteTextures(idTexture);
    }


    public static GlTexture newTexture2D(String resPath, String name) {
        return new TextureBuilder(resPath, name).build2D();
    }


    public static GlTexture newSolidColorTexture(int r, int g, int b, String name) {
        int level = 0;
        int width = 1;
        int height = 1;
        int border = 0;
        int idTexture = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, idTexture);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer buffer = stack.bytes((byte) r, (byte) g, (byte) b, (byte) 0xff);
            glTexImage2D(GL_TEXTURE_2D, level, GL_RGBA, width, height, border,
                    GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        }

        glBindTexture(GL_TEXTURE_2D, 0);
        return new GlTexture(idTexture, GL_TEXTURE0, GL_TEXTURE_2D, name);
    }


    public static void loadTexture(String resPath, TextureBuilder tbuilder) {
        try (MemoryStack stack = MemoryStack.stackPush()) {

            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);
            URL resource = Thread.currentThread().getClass().getResource(resPath);
            String filePath = new File(resource.toURI()).getAbsolutePath(); // encode spaces
            ByteBuffer buffer = STBImage.stbi_load(filePath, w, h, channels, 4);

            if (buffer == null) {
                throw new Exception("Can't load file " + filePath + " " + stbi_failure_reason());
            }

            tbuilder.mImageData = buffer;
            tbuilder.mWidth = w.get();
            tbuilder.mHeight = h.get();
            tbuilder.mFormat = GL_RGBA;

            // stbi_image_free(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static class TextureBuilder {
        String mName;
        int mIdTexture;
        int mTextureUnit = GL_TEXTURE0;
        int mTextureType;

        String mResPath;
        int mBorder;
        int mWidth;
        int mHeight;
        int mFormat = GL_RGBA;
        ByteBuffer mImageData;

        boolean mGenMipmap = true;
        boolean mClampToEdge = false;

        public TextureBuilder(String resPath, String name) {
            mName = name;
            mResPath = resPath;
        }


        public TextureBuilder genMipmap(boolean v) {
            mGenMipmap = v;
            return this;
        }

        public TextureBuilder clampToEdge(boolean v) {
            mClampToEdge = v;
            return this;
        }

        public GlTexture build2D() {
            mTextureType = GL_TEXTURE_2D;
            mIdTexture = glGenTextures();
            glBindTexture(mTextureType, mIdTexture);
            loadTexture(mResPath, this);
            glTexImage2D(
                    mTextureType, 0, GL_RGBA, mWidth, mHeight, 0,
                    GL_RGBA, GL_UNSIGNED_BYTE, mImageData
            );

            glTexParameteri(mTextureType, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(mTextureType, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            if (mGenMipmap) {
                glGenerateMipmap(mTextureType);
                glTexParameteri(mTextureType, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
            }

            if (mClampToEdge) {
                glTexParameteri(mTextureType, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
                glTexParameteri(mTextureType, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            }

            stbi_image_free(mImageData);
            glBindTexture(GL_TEXTURE_2D, 0);
            return new GlTexture(mIdTexture, mTextureUnit, mTextureType, mName);
        }

    }

}
