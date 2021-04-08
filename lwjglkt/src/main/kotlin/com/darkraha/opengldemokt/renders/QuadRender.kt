package com.darkraha.opengldemokt.renders



import com.darkraha.opengldemokt.gl.AppOGL
import com.darkraha.opengldemokt.gl.ShaderProgram
import com.darkraha.opengldemokt.gl.ShaderProgramBuilder
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil


/**
 * Render white quad. Demonstrate how to use VBO.
 * In OpenGL 3, VBO may not work standalone without VAO.
 * I decided to reject from shaders version 120 and just added VAO.
 *
 */
class QuadRender : Render() {
    private val matrixBuffer = MemoryUtil.memAllocFloat(16)
    protected val matrix = Matrix4f()
    private lateinit var prog: ShaderProgram
    private var idVbo = 0
    private var idVao = 0
    private val solidColor = floatArrayOf(1f, 1f, 1f, 1f)

    override fun onSetup(appOGL: AppOGL) {
        setSurfaceSize(appOGL.width, appOGL.height)

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        glEnable(GL_DEPTH_TEST)

        prog = ShaderProgramBuilder().matrix().solidColor().build()
        glUseProgram(prog.idProgram)

        //-----------------------------------------------
        // prepare data for VBO, positions of the quad vertices
        val data = floatArrayOf(
            -1.0f, 1.0f,
            1.0f, 1.0f,
            -1.0f, -1.0f,
            1.0f, -1.0f
        )

        //-----------------------------------------------
        // create VAO and activate it
        idVao = glGenVertexArrays()
        glBindVertexArray(idVao)

        //-----------------------------------------------
        // create VBO and upload data into it
        MemoryStack.stackPush().use { stack ->
            idVbo = glGenBuffers()
            glBindBuffer(GL_ARRAY_BUFFER, idVbo)
            val fb = stack.mallocFloat(data.size)
            fb.put(data).flip()
            glBufferData(GL_ARRAY_BUFFER, fb, GL_STATIC_DRAW)
        }

        //-----------------------------------------------
        // specify locations of attribute in data

        // 0 means  attribute tightly packed in the array
        val stride = 0

        // 0 because in data only one attribute packed
        val offset = 0L

        // we specified location of position attribute in shader to 0
        val posAttributeLocation = 0

        // we use only x,y coordinates for vertex position (z=0 by default)
        val size = 2

        glEnableVertexAttribArray(posAttributeLocation)
        glVertexAttribPointer(posAttributeLocation, size, GL_FLOAT, false, stride, offset)

        glBindVertexArray(0) // deactivate vao

    }

    override fun onDrawFrame(appOGL: AppOGL) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        //--------------------------------------------------
        // in this simple example you can move the following piece of code
        // to the end of onSetup()
        glUseProgram(prog.idProgram)
        matrix.identity()
            .perspective(ALNGLE45, aspect, 0.01f, 100f)
            .translate(0f, 0f, -6f)

        // bind final transformation matrix to the shader matrix variable
        glUniformMatrix4fv(
            glGetUniformLocation(prog.idProgram, "m"),
            false, matrix[matrixBuffer]
        )
        glUniform4fv(prog.solidColorLocation, solidColor)
        glBindVertexArray(idVao)
        // end
        //--------------------------------------------------

        //glBindBuffer(GL_VERTEX_ARRAY, idVbo); // for shader version 120
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4) // vertex count
    }

    override fun onDispose(appOGL: AppOGL) {
        prog.dispose()
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glDeleteBuffers(idVbo)
        super.onDispose(appOGL)
    }
}