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
        @JvmStatic
        fun main(args: Array<String>) {
            val app = AppOGL(LightTexturedCubeRender(), 300, 300)
          //  val app = AppOGL(TexturedCubeRender(), 300, 300)
           // AppOGL.isV120 = true; AppOGL(QuadRender(), 300, 300)
          //  val app = AppOGL(300, 300)
           // val app = AppOGL(ColoredQuadRender(), 300, 300)
          // val app = AppOGL(ColoredCubeRender(), 300, 300)
           // val app = AppOGL(TexturedQuadRender(), 300, 300)
        }
    }
}