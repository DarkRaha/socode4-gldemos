package com.darkraha.opengldemokt.gl


import com.darkraha.opengldemokt.gl.shader.ShaderProgramBuilder
import com.darkraha.opengldemokt.gl.shader.ShaderProgramBuilder.Companion.U_NORMAL_SAMPLER_NAME
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryUtil


class ShaderProgram {
    private lateinit var ids: IntArray

    val idProgram
        get() = ids[0]

    private val matricesLocations = IntArray(ShaderProgramBuilder.U_MATRIX_NAMES.size)
    private val samplersLocations = IntArray(8)
    private val light = IntArray(3)
    private var normalSamplerLocation = 0

    var solidColorLocation = 0
        private set

    constructor() {
        val builder = ShaderProgramBuilder()
        builder.colors(false, true)
        init(builder.buildVertexShader(), builder.buildFragmentShader())
    }

    constructor(vertexShaderCode: String, fragmentShaderCode: String) {
        init(vertexShaderCode, fragmentShaderCode)
    }

    protected fun init(vertexShaderCode: String, fragmentShaderCode: String) {
        ids = createProgram(vertexShaderCode, fragmentShaderCode)
        setupLocations()
    }


    fun setupLocations() {
        glUseProgram(idProgram)

        for (i in matricesLocations.indices) {
            matricesLocations[i] = glGetUniformLocation(
                idProgram,
                ShaderProgramBuilder.U_MATRIX_NAMES[i]
            )
        }

        samplersLocations[0] = glGetUniformLocation(idProgram, ShaderProgramBuilder.U_SAMPLER_NAME)
        for (i in 1 until samplersLocations.size) {
            samplersLocations[i] = glGetUniformLocation(idProgram, ShaderProgramBuilder.U_SAMPLER_NAME + i)
        }

        solidColorLocation = glGetUniformLocation(idProgram, ShaderProgramBuilder.U_SOLID_COLOR_NAME)

        normalSamplerLocation = glGetUniformLocation(idProgram, U_NORMAL_SAMPLER_NAME)

        light[0] = glGetUniformLocation(idProgram, ShaderProgramBuilder.U_LIGHT_NAMES[0])
        light[1] = glGetUniformLocation(idProgram, ShaderProgramBuilder.U_LIGHT_NAMES[1])
        light[2] = glGetUniformLocation(idProgram, ShaderProgramBuilder.U_LIGHT_NAMES[2])
    }


    fun use() {
        glUseProgram(ids[0])
    }

    fun uniformTexture(texture: GlTexture) {
        glActiveTexture(texture.textureUnit)
        glBindTexture(texture.textureType, texture.idTexture)
        val ind = texture.textureUnit - GL_TEXTURE0
        glUniform1i(samplersLocations[ind], ind)
    }



    fun uniformTexture(idTexture: Int) {
        val samplerLocation = glGetUniformLocation(idProgram, "sampler")
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, idTexture)
        glUniform1i(samplerLocation, 0)
    }

    fun uniformMatrix(m: Matrix4f) {
        glUniformMatrix4fv(
            matricesLocations[ShaderProgramBuilder.IND_MATRIX], false,
            m[MATRIX_BUFFER]
        )
    }

    fun uniformSolidColor(color: FloatArray) {
        glUniform4fv(solidColorLocation, color)
    }

    fun uniformMatrices(m: Matrices) {
        glUniformMatrix4fv(
            matricesLocations[ShaderProgramBuilder.IND_MATRIX], false,
            m.matrix[MATRIX_BUFFER]
        )
        glUniformMatrix4fv(
            matricesLocations[ShaderProgramBuilder.IND_MATRIX_NORMAL], false,
            m.normals[MATRIX_BUFFER]
        )
        glUniformMatrix4fv(
            matricesLocations[ShaderProgramBuilder.IND_MATRIX_PROJECTION], false,
            m.projection[MATRIX_BUFFER]
        )
        glUniformMatrix4fv(
            matricesLocations[ShaderProgramBuilder.IND_MATRIX_VIEW], false,
            m.view[MATRIX_BUFFER]
        )
        glUniformMatrix4fv(
            matricesLocations[ShaderProgramBuilder.IND_MATRIX_VIEW_MODEL], false,
            m.viewModel[MATRIX_BUFFER]
        )
        glUniformMatrix4fv(
            matricesLocations[ShaderProgramBuilder.IND_MATRIX_MODEL], false,
            m.model[MATRIX_BUFFER]
        )
    }


    fun uniformDirectionalLight(ambient: FloatArray, lightDiffuse: FloatArray, lightDirection: FloatArray) {
        glUniform3fv(light[0], ambient)
        glUniform3fv(light[1], lightDiffuse)
        glUniform3fv(light[2], lightDirection)
    }

    fun uniformDirectionalLight(ambient: Vector3f, lightDiffuse: Vector3f, lightDirection: Vector3f) {
        glUniform3f(light[0], ambient.x, ambient.y, ambient.z)
        glUniform3f(light[1], lightDiffuse.x, lightDiffuse.y, lightDiffuse.z)
        glUniform3f(light[2], lightDirection.x, lightDirection.y, lightDirection.z)
    }


    private fun uniformObjectTexture(glObject: GlObject) {
        var usedUnit = 0

        glObject.texture?.also {
            glActiveTexture(GL_TEXTURE0)
            glBindTexture(it.textureType, it.idTexture)
            glUniform1i(samplersLocations[0], 0)
            ++usedUnit
        }

        glObject.normalTexture?.also {
            glActiveTexture(GL_TEXTURE0 + usedUnit)
            glBindTexture(it.textureType, it.idTexture)
            glUniform1i(normalSamplerLocation, usedUnit)
            ++usedUnit
        }

        glObject.extraTextures?.also {
            for (i in it.indices) {
                glActiveTexture(GL_TEXTURE0 + usedUnit)
                glBindTexture(it[i].textureType, it[i].idTexture)
                glUniform1i(samplersLocations[i + 1], usedUnit)
                ++usedUnit
            }
        }
    }

    fun uniform(glObject: GlObject, m: Matrices, light: LightDirectional?) {
        uniformObjectTexture(glObject)

        glObject.transforms?.apply {  m.applyModel(this) }
        uniformMatrices(m)

        light?.apply {
            uniformDirectionalLight(ambient!!, diffuseColor!!, direction!!)
        }
    }


    fun dispose() {
        glDetachShader(ids[0], ids[1])
        glDetachShader(ids[0], ids[2])
        glDeleteProgram(ids[0])
        glDeleteShader(ids[1])
        glDeleteShader(ids[2])
    }

    fun delete() {
        glDetachShader(idProgram, ids[1])
        glDetachShader(idProgram, ids[2])
        glDeleteProgram(ids[0])
    }

    companion object {
        val MATRIX_BUFFER = MemoryUtil.memAllocFloat(16)

        fun createProgram(vertexShaderSource: String, fragmentShaderSource: String): IntArray {
            val idVertexShader: Int = compileShader(GL_VERTEX_SHADER, vertexShaderSource)

            val idFragmentShader: Int =
                compileShader(GL_FRAGMENT_SHADER, fragmentShaderSource)

            return intArrayOf(
                linkProgram(idVertexShader, idFragmentShader),
                idVertexShader,
                idFragmentShader
            )
        }

        fun linkProgram(idVertexShader: Int, idFragmentShader: Int): Int {
            val idProgram = glCreateProgram()
            glAttachShader(idProgram, idVertexShader)
            glAttachShader(idProgram, idFragmentShader)
            glLinkProgram(idProgram)
            check(glGetProgrami(idProgram, GL_LINK_STATUS) != GL_FALSE) { "Could not link shader program." }
            return idProgram
        }

        /**
         * @param type Possible values GL_VERTEX_SHADER, GL_FRAGMENT_SHADER
         */
        fun compileShader(type: Int, source: String): Int {
            val idShader = glCreateShader(type)
            glShaderSource(idShader, source)
            glCompileShader(idShader)
            if (glGetShaderi(idShader, GL_COMPILE_STATUS) == GL_FALSE) {
                val reason = glGetShaderInfoLog(idShader, 500)
                glDeleteShader(idShader)
                throw IllegalStateException("Could not compile shader: $reason\nSource:\n $source")
            }
            return idShader
        }


        @JvmStatic
        val VERTEX_SHADER_120 =
            """
#version 120 
attribute vec4 vCoord;
varying vec4 exColor;
uniform mat4 matrix;
void main() {
    gl_Position = matrix  * vCoord;
    exColor = vec4(1.0,1.0,1.0,1.0);
}
""".trimIndent()

        @JvmStatic
        val FRAGMENT_SHADER_120 =
            """
#version 120 
varying vec4 exColor;
void main() {
    gl_FragColor = exColor;
}
""".trimIndent()
    }
}