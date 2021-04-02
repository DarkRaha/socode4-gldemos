package com.darkraha.opengldemokt.gl.shaders

import java.lang.StringBuilder

class FragmentShaderBuilder : ShaderBuilderA() {
    fun exchangeData(color: Boolean, texCoord: Boolean, lighting: Boolean): FragmentShaderBuilder {
        exColor = color
        exTexCoord = texCoord
        exLighting = lighting
        return this
    }

    override fun buildDeclareOutCustom(): String {
        return "out vec4 fragColor;\n"
    }

    override fun buildCalcCustom(): String {
        val sb = StringBuilder()
        if (exLighting) {
            sb.append("vec4 texelColor = texture(uSampler, exTexCoord);\n")
            sb.append("fragColor = vec4(texelColor.rgb * exLighting, texelColor.a);\n")
        } else if (exTexCoord) {
            sb.append("fragColor = texture(uSampler, exTexCoord);\n")
        } else if (exColor) {
            sb.append("fragColor = exColor;\n")
        }
        return sb.toString()
    }

    override fun buildDeclareUniformCustom(): String {
        // todo multiple textures
        return "uniform sampler2D uSampler;\n"
    }

    init {
        exOut = false
    }
}
