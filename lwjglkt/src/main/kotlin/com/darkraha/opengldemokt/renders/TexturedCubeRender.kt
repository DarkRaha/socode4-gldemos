package com.darkraha.opengldemokt.renders

import com.darkraha.opengldemokt.gl.AppOGL
import com.darkraha.opengldemokt.gl.GlUtils
import com.darkraha.opengldemokt.gl.ShaderProgram
import com.darkraha.opengldemokt.gl.ShaderProgramBuilder
import com.darkraha.opengldemokt.gl.modelling.GlModel
import com.darkraha.opengldemokt.gl.modelling.Models
import org.joml.Matrix4f
import org.lwjgl.opengl.GL33.*

class TexturedCubeRender : Render() {
    private lateinit var matrix: Matrix4f
    private lateinit var prog: ShaderProgram
    private val rotY = 1.5f * TO_RAD
    private val rotX = TO_RAD
    private lateinit var cube: GlModel

    private var idTexture = 0

    override fun onSetup(appOGL: AppOGL) {
        setSurfaceSize(appOGL.width, appOGL.height)
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        glEnable(GL_DEPTH_TEST)

        prog = ShaderProgramBuilder()
            .vertexAttributes(false, true, false)
            .matrix(false)
            .build()

        glUseProgram(prog.idProgram)

        matrix = Matrix4f()

        matrix.identity()
            .perspective(45 * TO_RAD, aspect, 1f, 100f)
            .translate(0f, 0f, -6f)

        cube = Models.cube(1f, 1f, 1f, "cube-0").toGlModel()

        idTexture = GlUtils.loadTex2DResDefault(0, "/textures/235.jpg")
        glGenerateMipmap(idTexture)
    }

    override fun onSurfaceChanged(appOGL: AppOGL, width: Int, height: Int) {
        super.onSurfaceChanged(appOGL, width, height)
        matrix.identity()
            .perspective(45 * TO_RAD, aspect, 1f, 100f)
            .translate(0f, 0f, -6f)
    }


    override fun onDrawFrame(appOGL: AppOGL) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        glUseProgram(prog.idProgram)
        matrix.rotateAffineXYZ(rotX, rotY, 0f)
        prog.uniformTexture(idTexture)
        prog.uniformMatrix(matrix)
        // glEnable(GL_CULL_FACE)
        cube.draw()
    }

    override fun onDispose(appOGL: AppOGL) {
        prog.dispose()
        glDeleteTextures(idTexture)
        cube.dispose()
    }
}
