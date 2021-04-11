package com.darkraha.opengldemokt.gl.shader

import java.lang.StringBuilder

import com.darkraha.opengldemokt.gl.shader.ShaderProgramBuilder.Companion.IND_MATRIX
import com.darkraha.opengldemokt.gl.shader.ShaderProgramBuilder.Companion.IND_MATRIX_MODEL
import com.darkraha.opengldemokt.gl.shader.ShaderProgramBuilder.Companion.IND_MATRIX_VIEW_MODEL
import com.darkraha.opengldemokt.gl.shader.ShaderProgramBuilder.Companion.SHADER_TYPE_VERTEX

internal class BaseBuilder : HelperBuilder {
    val matricesDeclaration = arrayOf(
        "uniform mat4 m;\n",
        "uniform mat4 mP;\n",
        "uniform mat4 mVM;\n",
        "uniform mat4 mV;\n",
        "uniform mat4 mM;\n"
    )
    val useMatrices = BooleanArray(matricesDeclaration.size)

    override fun addTypeDeclarations(sb: StringBuilder, shaderType: Int) {}

    override fun addInputDeclarations(sb: StringBuilder, shaderType: Int) {
        if (shaderType == SHADER_TYPE_VERTEX) {
            sb.append("layout(location=0) in vec4 vPos;\n")
        }
    }

    override fun addExchangeDeclarations(sb: StringBuilder, shaderType: Int) {}

    override fun addUniformDeclarations(sb: StringBuilder, shaderType: Int) {
        if (shaderType == SHADER_TYPE_VERTEX) {
            for (i in useMatrices.indices) {
                if (useMatrices[i]) {
                    sb.append(matricesDeclaration[i])
                }
            }
        }
    }

    override fun addFuncsDeclarations(sb: StringBuilder, shaderType: Int) {}

    override fun addCalculations(sb: StringBuilder, shaderType: Int) {
        if (SHADER_TYPE_VERTEX == shaderType) {
            if (useMatrices[IND_MATRIX]) {
                sb.append("    gl_Position = m  * vPos;\n")
            } else if (useMatrices[IND_MATRIX_VIEW_MODEL]) {
                sb.append("    gl_Position = mP  * mVM * vPos;\n")
            } else if (useMatrices[IND_MATRIX_MODEL]) {
                sb.append("    gl_Position = mP  * mV * mM * vPos;\n")
            }
        }
    }

    init {
        useMatrices[0] = true
    }
}