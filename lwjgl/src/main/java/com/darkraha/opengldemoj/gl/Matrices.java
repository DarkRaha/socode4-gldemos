package com.darkraha.opengldemoj.gl;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static com.darkraha.opengldemoj.gl.GlUtils.*;

public class Matrices {

    public static final float PI_F = (float) Math.PI;
    public static final float TO_RAD = PI_F / 180.f;

    public final Matrix4f projection = new Matrix4f().perspective(45 * TO_RAD, 1, 1f, 100);
    public final Matrix4f camera = new Matrix4f().lookAlong(0f,0f,-1f,0,1,0);
    public Matrix4f model;
    public final Matrix4f viewModel = new Matrix4f();
    public final Matrix4f normals = new Matrix4f();
    public final Matrix4f matrix = new Matrix4f();



    public void applyModel(Matrix4f modelMatrix) {
        this.model = modelMatrix;
        camera.mul(modelMatrix, viewModel);
        matrix.set(projection).mul(viewModel);
        viewModel.invert(normals);
        normals.transpose();
    }


}
