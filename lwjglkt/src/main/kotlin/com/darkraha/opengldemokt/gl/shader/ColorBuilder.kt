package com.darkraha.opengldemokt.gl.shader

import com.darkraha.opengldemokt.gl.shader.ShaderProgramBuilder.Companion.SHADER_TYPE_FRAGMENT
import com.darkraha.opengldemokt.gl.shader.ShaderProgramBuilder.Companion.SHADER_TYPE_VERTEX
import java.lang.StringBuilder


class ColorBuilder : HelperBuilder {
    var usePerVertex = false
    var useSolidColor = false

    override fun addTypeDeclarations(sb: StringBuilder, shaderType: Int) {

    }

    override fun addInputDeclarations(sb: StringBuilder, shaderType: Int) {
        if (shaderType == SHADER_TYPE_VERTEX) {
            if (usePerVertex) {
                sb.append("layout(location=1) in vec4 vColor;\n")
            }
        }
    }

    override fun addExchangeDeclarations(sb: StringBuilder, shaderType: Int) {
        if (usePerVertex || useSolidColor) {
            if (shaderType == SHADER_TYPE_VERTEX) {
                sb.append("out vec4 exColor;\n")
            }
            if (shaderType == SHADER_TYPE_FRAGMENT) {
                sb.append("in vec4 exColor;\n")
            }
        }
        if (shaderType == SHADER_TYPE_FRAGMENT) {
            sb.append("out vec4 fragColor;\n")
        }
    }

    override fun addUniformDeclarations(sb: StringBuilder, shaderType: Int) {
        if (useSolidColor) {
            sb.append("uniform vec4 solidColor;\n")
        }
    }

    override fun addFuncsDeclarations(sb: StringBuilder, shaderType: Int) {}

    override fun addCalculations(sb: StringBuilder, shaderType: Int) {
        if (shaderType == SHADER_TYPE_VERTEX) {
            if (usePerVertex && useSolidColor) {
                sb.append("exColor = vColor * solidColor")
            } else if (usePerVertex) {
                sb.append("exColor = vColor;\n")
            } else if (useSolidColor) {
                sb.append("exColor = solidColor;\n")
            }
        }
        if (shaderType == SHADER_TYPE_FRAGMENT) {
            if (usePerVertex || useSolidColor) {
                sb.append("fragColor = exColor;\n")
            } else {
                sb.append("fragColor = vec4(1,1,1,1);\n")
            }
        }
    }
}