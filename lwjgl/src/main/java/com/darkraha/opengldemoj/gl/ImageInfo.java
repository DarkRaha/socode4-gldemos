package com.darkraha.opengldemoj.gl;

import java.nio.ByteBuffer;

import static org.lwjgl.stb.STBImage.stbi_image_free;

public class ImageInfo {
    ByteBuffer imageData;
    int width;
    int height;


    public void dispose(){
        stbi_image_free(imageData);
    }

}
