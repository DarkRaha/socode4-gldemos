package com.darkraha.gldemos.renders

import android.opengl.GLES30.*
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

open class Render : GLSurfaceView.Renderer {
    protected var surfaceWidth = 0f
    protected var surfaceHeight = 0f
    protected var aspect = 0f

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        glEnable(GL_DEPTH_TEST)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
        surfaceWidth = width.toFloat()
        surfaceHeight = height.toFloat()
        aspect = surfaceWidth / surfaceHeight
    }

    override fun onDrawFrame(arg0: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
    }


    open fun onDispose() {

    }

    companion object {
        @JvmStatic
        val PI_F = Math.PI.toFloat()

        @JvmStatic
        val TO_RAD = PI_F / 180.0f

        @JvmStatic
        val ALNGLE45 = Math.toRadians(45.0).toFloat()
    }

}