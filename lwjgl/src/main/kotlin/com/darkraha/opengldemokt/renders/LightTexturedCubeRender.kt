package com.darkraha.opengldemokt.renders

import com.darkraha.opengldemokt.gl.AppOGL
import com.darkraha.opengldemokt.gl.GlUtils
import com.darkraha.opengldemokt.gl.ShaderProgram
import com.darkraha.opengldemokt.gl.shaders.FragmentShaderBuilder
import com.darkraha.opengldemokt.gl.shaders.VertexShaderBuilder
import org.joml.Matrix4f
import org.lwjgl.opengl.GL33.*

class LightTexturedCubeRender : Render() {
    private lateinit var matrix: Matrix4f
    private lateinit var projMatrix: Matrix4f
    private lateinit var viewModelMatrix: Matrix4f
    private lateinit var normalMatrix: Matrix4f
    private lateinit var prog: ShaderProgram
    private var rotY = 0f
    private var rotX = 0f
    private var idVao = 0
    private lateinit var ids: IntArray
    private var idTexture = 0

    override fun onSetup(appOGL: AppOGL) {
        setSurfaceSize(appOGL.width, appOGL.height)
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        glEnable(GL_DEPTH_TEST)
        val vertexShader = VertexShaderBuilder()
            .vertexInputData(true, false, true, true)
            .matrixData(true)
            .directionalLightEmbeded(
                floatArrayOf(0.3f, 0.3f, 0.3f),
                floatArrayOf(1f, 1f, 1f),
                floatArrayOf(0.85f, 0.8f, 0.75f)
            )
            .exchangeData(false, true, true)
            .build()
        val fragmentShader = FragmentShaderBuilder()
            .exchangeData(false, true, true)
            .build()
        prog = ShaderProgram(vertexShader, fragmentShader)




        glUseProgram(prog!!.idProgram)
        matrix = Matrix4f()
        projMatrix = Matrix4f()
        projMatrix!!.identity()
            .perspective(45 * TO_RAD, aspect, 1f, 100f)
        viewModelMatrix = Matrix4f()
        normalMatrix = Matrix4f()
        val coords = floatArrayOf( // Front face
            -1.0f, -1.0f, 1.0f,
            1.0f, -1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f, 1.0f,  // Back face
            -1.0f, -1.0f, -1.0f,
            -1.0f, 1.0f, -1.0f,
            1.0f, 1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,  // Top face
            -1.0f, 1.0f, -1.0f,
            -1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, -1.0f,  // Bottom face
            -1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, 1.0f,
            -1.0f, -1.0f, 1.0f,  // Right face
            1.0f, -1.0f, -1.0f,
            1.0f, 1.0f, -1.0f,
            1.0f, 1.0f, 1.0f,
            1.0f, -1.0f, 1.0f,  // Left face
            -1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f, 1.0f,
            -1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f, -1.0f
        )
        val texCoords = floatArrayOf( // Front
            0.0f, 0.0f,  //
            1.0f, 0.0f,  //
            1.0f, 1.0f,  //
            0.0f, 1.0f,  //
            // Back
            0.0f, 0.0f,  //
            1.0f, 0.0f,  //
            1.0f, 1.0f,  //
            0.0f, 1.0f,  //
            // Top
            0.0f, 0.0f,  //
            1.0f, 0.0f,  //
            1.0f, 1.0f,  //
            0.0f, 1.0f,  //
            // Bottom
            0.0f, 0.0f,  //
            1.0f, 0.0f,  //
            1.0f, 1.0f,  //
            0.0f, 1.0f,  //
            // Right
            0.0f, 0.0f,  //
            1.0f, 0.0f,  //
            1.0f, 1.0f,  //
            0.0f, 1.0f,  //
            // Left
            0.0f, 0.0f,  //
            1.0f, 0.0f,  //
            1.0f, 1.0f,  //
            0.0f, 1.0f
        )
        val normals = floatArrayOf( // front
            0.0f, 0.0f, 1.0f,  //
            0.0f, 0.0f, 1.0f,  //
            0.0f, 0.0f, 1.0f,  //
            0.0f, 0.0f, 1.0f,  //
            // Back
            0.0f, 0.0f, -1.0f,  //
            0.0f, 0.0f, -1.0f,  //
            0.0f, 0.0f, -1.0f,  //
            0.0f, 0.0f, -1.0f,  //
            // Top
            0.0f, 1.0f, 0.0f,  //
            0.0f, 1.0f, 0.0f,  //
            0.0f, 1.0f, 0.0f,  //
            0.0f, 1.0f, 0.0f,  //
            // Bottom
            0.0f, -1.0f, 0.0f,  //
            0.0f, -1.0f, 0.0f,  //
            0.0f, -1.0f, 0.0f,  //
            0.0f, -1.0f, 0.0f,  //
            // Right
            1.0f, 0.0f, 0.0f,  //
            1.0f, 0.0f, 0.0f,  //
            1.0f, 0.0f, 0.0f,  //
            1.0f, 0.0f, 0.0f,  //
            // Left
            -1.0f, 0.0f, 0.0f,  //
            -1.0f, 0.0f, 0.0f,  //
            -1.0f, 0.0f, 0.0f,  //
            -1.0f, 0.0f, 0.0f //
        )
        val indices = byteArrayOf(
            0, 1, 2,  /*        */0, 2, 3,  // front
            4, 5, 6,  /*        */4, 6, 7,  // back
            8, 9, 10,  /*       */8, 10, 11,  // top
            12, 13, 14,  /*     */12, 14, 15,  // bottom
            16, 17, 18,  /*     */16, 18, 19,  // right
            20, 21, 22,  /*     */20, 22, 23
        )
        idTexture = GlUtils.loadTex2DResDefault(0, "/textures/235.jpg")
        glGenerateMipmap(idTexture)
        idVao = glGenVertexArrays()
        glBindVertexArray(idVao)
        ids = GlUtils.prepareVertexData(coords, null, texCoords, indices, normals)
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
        val samplerLocation = glGetUniformLocation(prog!!.idProgram, "uSampler")
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, idTexture)
        glUniform1i(samplerLocation, 0)
        glUseProgram(prog!!.idProgram)
        viewModelMatrix!!.identity()
            .translate(0f, 0f, -6f)
            .rotateAffineXYZ(rotX, rotY, 0f)
        viewModelMatrix!!.invert(normalMatrix)
        normalMatrix!!.transpose()
        matrix!!.set(projMatrix).mul(viewModelMatrix)
        GlUtils.bindMatrix(prog!!.idProgram, matrix)
        GlUtils.bindNormalMatrix(prog!!.idProgram, normalMatrix)
        glBindVertexArray(idVao)
        glEnable(GL_CULL_FACE)
        glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_BYTE, 0)
    }

    override fun onDispose(appOGL: AppOGL) {
        prog!!.dispose()
        glDeleteTextures(idTexture)
        glDeleteVertexArrays(idVao)
        glDeleteBuffers(ids)
    }
}
