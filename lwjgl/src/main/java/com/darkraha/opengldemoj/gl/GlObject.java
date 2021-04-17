package com.darkraha.opengldemoj.gl;

import com.darkraha.opengldemoj.gl.modelling.GlModel;
import org.joml.Matrix4f;

public class GlObject {
    public GlModel model;
    public GlTexture texture; // primary texture
    public GlTexture normalTexture;
    public Matrix4f transforms;
    public GlTexture []extraTextures;
}
