package com.darkraha.opengldemokt.renders

import com.darkraha.opengldemokt.gl.AppOGL
import org.lwjgl.opengl.GL33.*

open class Render {

    protected var surfaceWidth = 0f
    protected var surfaceHeight = 0f
    protected var aspect = 0f

    open fun onSetup(appOGL: AppOGL) {
        setSurfaceSize(appOGL.width, appOGL.height)
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        glEnable(GL_DEPTH_TEST)
    }

    protected fun setSurfaceSize(w: Int, h: Int){
        surfaceWidth = w.toFloat()
        surfaceHeight = h.toFloat()
        aspect = surfaceWidth/surfaceHeight
    }

    open fun onSurfaceChanged(appOGL: AppOGL, width: Int, height: Int) {
        setSurfaceSize(width,height)
        glViewport(0, 0, width, height)
    }

    open fun onDrawFrame(appOGL: AppOGL) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
    }

    open fun onDispose(appOGL: AppOGL) {

    }

    companion object {
        @JvmStatic
        val PI_F = Math.PI.toFloat()

        @JvmStatic
        val TO_RAD = PI_F / 180.0f

        @JvmStatic
        val ALNGLE45 = 45f * TO_RAD
    }
}