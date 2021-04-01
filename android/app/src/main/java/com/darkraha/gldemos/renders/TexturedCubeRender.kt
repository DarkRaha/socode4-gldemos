package com.darkraha.gldemos.renders
import android.content.Context
import android.opengl.GLES30.*
import com.darkraha.gldemos.R
import com.darkraha.gldemos.gl.GlUtils
import com.darkraha.gldemos.gl.ShaderProgram
import org.joml.Matrix4f
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class TexturedCubeRender(private val context: Context) : Render(){

    private lateinit var matrix: Matrix4f
    private lateinit var prog: ShaderProgram
    private var rotY = 0f
    private var rotX = 0f
    private var idVao = 0
    private lateinit var ids: IntArray
    private var idTexture = 0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        glEnable(GL_DEPTH_TEST)
        prog = ShaderProgram()
        glUseProgram(prog.idProgram)
        matrix = Matrix4f()

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
            // Right
            0.0f, 0.0f,  //
            1.0f, 0.0f,  //
            1.0f, 1.0f,  //
            0.0f, 1.0f,  //
            // Left
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
            0.0f, 1.0f
        )

        val indices = byteArrayOf(
            0, 1, 2,  /*        */0, 2, 3,  // front
            4, 5, 6,  /*        */4, 6, 7,  // back
            8, 9, 10,  /*       */8, 10, 11,  // top
            12, 13, 14,  /*     */12, 14, 15,  // bottom
            16, 17, 18,  /*     */16, 18, 19,  // right
            20, 21, 22,  /*     */20, 22, 23
        )
        idTexture = GlUtils.loadTex2DResDefault(0, context, R.drawable.t235)
        glGenerateMipmap(idTexture)
        idVao = GlUtils.createVAO()
        ids = GlUtils.prepareVertexData(coords, null, texCoords, indices)
    }


    override fun onDrawFrame(arg0: GL10?) {
        rotY += 1.5f * TO_RAD
        if (rotY > 2 * PI_F) {
            rotY = rotY - 2 * PI_F
        }
        rotX += 1f * TO_RAD
        if (rotX > 2 * PI_F) {
            rotX = rotX - 2 * PI_F
        }

        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        val samplerLocation = glGetUniformLocation(prog.idProgram, "texSampler")
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, idTexture)
        glUniform1i(samplerLocation, 0)

        GlUtils.bindMatrix(
            prog.idProgram, matrix.identity()
                .perspective(
                    45 * TO_RAD,
                    aspect,
                    1f, 100f
                )
                .translate(0f, 0f, -6f)
                .rotateAffineXYZ(rotX, rotY, 0f)
        )

        glUseProgram(prog!!.idProgram)
        glUniform1i(glGetUniformLocation(prog!!.idProgram, "withTexture"), 1)
        glUniform4f(glGetUniformLocation(prog!!.idProgram, "uColor"), -1f, -1f, -1f, -1f)

        glBindVertexArray(idVao)
        glEnable(GL_CULL_FACE)
        glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_BYTE, 0)

        // for demo
        //glBindVertexArray(idVao);
        //glEnable(GL_CULL_FACE);
        //glDrawElements(GL_TRIANGLES, 18, GL_UNSIGNED_BYTE,0);
    }

    override fun onDispose() {
        prog.dispose()
        GlUtils.delete(idVao, 0,0,idTexture)
        glDeleteBuffers(ids.size, ids,0)
    }


}