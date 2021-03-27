package com.darkraha.opengldemokt.gl

import org.lwjgl.opengl.GL33.*


class ShaderProgram {
    private lateinit var ids: IntArray
    val idProgram
        get() = ids[0]

    constructor() {
        init(VERTEX_SHADER_DEFAULT, FRAGMENT_SHADER_DEFAULT)
    }

    constructor(vertexShaderCode: String, fragmentShaderCode: String) {
        init(vertexShaderCode, fragmentShaderCode)
    }

    protected fun init(vertexShaderCode: String, fragmentShaderCode: String) {
        ids = createProgram(vertexShaderCode, fragmentShaderCode)
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
        val VERTEX_SHADER_DEFAULT =
            """
#version 330 core
precision mediump float;
layout(location=0) in vec4 vCoord;
layout(location=1) in vec4 vColor ;
layout(location=2) in vec3 vNormal;
layout(location=3) in vec2 vTexCoord ;
out vec4 exColor;
out vec2 exTexCoord;
uniform mat4 matrix;
uniform vec4 uColor = vec4(-1.0, -1.0, -1.0, -1.0);
void main() {
     gl_Position = matrix  * vCoord;
     if(uColor.w != -1.0) { exColor=uColor; } else {exColor=vColor;} 
     exTexCoord = vTexCoord;
}
""".trimIndent()

        @JvmStatic
        val FRAGMENT_SHADER_DEFAULT =
            """
#version 330 core
precision mediump float;
in vec4 exColor;
in vec2 exTexCoord;
out vec4 fragColor;
uniform int withTexture = 1;
uniform sampler2D texSampler;

void main() {
    if(withTexture==1) {fragColor = texture(texSampler, exTexCoord);}
    else {fragColor = exColor;}
}
""".trimIndent()


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