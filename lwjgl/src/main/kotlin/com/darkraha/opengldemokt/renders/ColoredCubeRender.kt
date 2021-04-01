package com.darkraha.opengldemokt.renders


import com.darkraha.opengldemokt.gl.AppOGL
import com.darkraha.opengldemokt.gl.GlUtils
import com.darkraha.opengldemokt.gl.ShaderProgram

import org.joml.Matrix4f

import org.lwjgl.opengl.GL33.*


class ColoredCubeRender : Render() {
    private lateinit var matrix: Matrix4f
    private lateinit var prog: ShaderProgram
    private var idVao = 0
    private var idVbo = 0
    private var idIbo = 0
    private var rotY = 0f
    private var rotX = 0f

    override fun onSetup(appOGL: AppOGL) {
        setSurfaceSize(appOGL.width, appOGL.height)
        glEnable(GL_DEPTH_TEST)
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        prog = ShaderProgram()
        matrix = Matrix4f()

        idVao = GlUtils.createVAO()
        idVbo = GlUtils.createVBO(
            floatArrayOf(
                // coords                colors
                // front
                -1.0f, -1.0f, 1.0f, /* */ 1.0f, 0.0f, 0.0f,
                1.0f, -1.0f, 1.0f,  /* */ 0.0f, 1.0f, 0.0f,
                1.0f, 1.0f, 1.0f,   /* */ 0.0f, 0.0f, 1.0f,
                -1.0f, 1.0f, 1.0f,  /* */ 1.0f, 1.0f, 1.0f,
                // back
                -1.0f, -1.0f, -1.0f, /* */ 1.0f, 0.0f, 0.0f,
                1.0f, -1.0f, -1.0f,  /* */ 0.0f, 1.0f, 0.0f,
                1.0f, 1.0f, -1.0f,   /* */ 0.0f, 0.0f, 1.0f,
                -1.0f, 1.0f, -1.0f,  /* */ 1.0f, 1.0f, 1.0f
            )
        )

        GlUtils.bindAttributes(idVbo, 3, 3, false)

        idIbo = GlUtils.createIBO(
            byteArrayOf( // front
                0, 1, 2,
                2, 3, 0,  // right
                1, 5, 6,
                6, 2, 1,  // back
                7, 6, 5,
                5, 4, 7,  // left
                4, 0, 3,
                3, 7, 4,  // bottom
                4, 5, 1,
                1, 0, 4,  // top
                3, 2, 6,
                6, 7, 3
            )
        )

        glBindVertexArray(0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    override fun onDrawFrame(appOGL: AppOGL) {
        rotY += 1.5f * TO_RAD
        if (rotY > 2 * PI_F) {
            rotY = rotY - 2 * PI_F
        }
        rotX += 1f * TO_RAD
        if (rotX > 2 * PI_F) {
            rotX = rotX - 2 * PI_F
        }


        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        glUseProgram(prog.idProgram)
        glUniform1i(glGetUniformLocation(prog.idProgram, "withTexture"), 0)
        glUniform4f(glGetUniformLocation(prog.idProgram, "uColor"), -1f, -1f, -1f, -1f)

        matrix.identity()
            .perspective(45 * TO_RAD, aspect,1f, 100f )
            .translate(0f, 0f, -6f)
            .rotateAffineXYZ(rotX, rotY, 0f)

        GlUtils.bindMatrix(prog.idProgram, matrix)

        glBindVertexArray(idVao)
        glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_BYTE, 0)
    }

    override fun onDispose(appOGL: AppOGL) {
        prog.dispose()
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindVertexArray(idVao)
        glDeleteVertexArrays(idVao)
        glDeleteBuffers(idVbo)
        glDeleteBuffers(idIbo)

    }
}