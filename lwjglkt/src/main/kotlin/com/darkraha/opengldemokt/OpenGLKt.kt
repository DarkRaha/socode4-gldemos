package com.darkraha.opengldemokt


import com.darkraha.opengldemokt.gl.AppOGL
import com.darkraha.opengldemokt.renders.*
import org.lwjgl.Version
import org.lwjgl.glfw.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil


class OpenGLKt {


    companion object {

        /**
         * MacOS users should start their application passing "-XstartOnFirstThread" as a VM option.
         */
        @JvmStatic
        fun main(args: Array<String>) {

            // AppOGL.isV120 = true; AppOGL(QuadRender(), 300, 300) // rejected
             //AppOGL(QuadRender(), 300, 300)
            // AppOGL(ColoredQuadRender(), 300, 300)
             AppOGL(ColoredCubeRender(), 300, 300)
            // AppOGL(TexturedQuadRender(), 300, 300)
            // AppOGL(TexturedCubeRender(), 300, 300)
            // AppOGL(LightTexturedCubeRender(), 300, 300)
           // AppOGL(LightTexturedSphereRender(), 300, 300)

        }
    }
}