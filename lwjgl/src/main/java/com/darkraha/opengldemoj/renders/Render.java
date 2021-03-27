package com.darkraha.opengldemoj.renders;

import com.darkraha.opengldemoj.gl.AppOGL;

import static org.lwjgl.opengl.GL33.*;

/**
 * Empty renderer, can be used as base class for other renderers.
 */
public class Render {

    public static final float PI_F = (float) Math.PI;
    public static final float TO_RAD = PI_F / 180.f;
    public static final float ALNGLE45 = (float) Math.toRadians(45.0f);

    protected float surfaceWidth = 0f;
    protected float surfaceHeight = 0f;
    protected float aspect = 0f;

    protected void setSurfaceSize(int w, int h) {
        surfaceWidth = w;
        surfaceHeight = h;
        aspect = surfaceWidth / surfaceHeight;
    }


    public void onSetup(AppOGL appOGL) {
        setSurfaceSize(appOGL.getWidth(), appOGL.getHeight());
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glEnable(GL_DEPTH_TEST);
    }

    public void onSurfaceChanged(AppOGL appOGL, int w, int h) {
        setSurfaceSize(w, h);
        glViewport(0, 0, w, h);
    }

    public void onDrawFrame(AppOGL appOGL) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void onDispose(AppOGL appOGL) {

    }

}
