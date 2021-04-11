package com.darkraha.opengldemokt.gl.shader

import com.darkraha.opengldemokt.gl.shader.ShaderProgramBuilder.Companion.SHADER_TYPE_FRAGMENT
import com.darkraha.opengldemokt.gl.shader.ShaderProgramBuilder.Companion.SHADER_TYPE_VERTEX
import java.lang.StringBuilder

class Texture2DBuilder : HelperBuilder {

    override fun addTypeDeclarations(sb: StringBuilder, shaderType: Int) {}

    override fun addInputDeclarations(sb: StringBuilder, shaderType: Int) {
        if (shaderType == SHADER_TYPE_VERTEX) {
            sb.append("layout(location=3) in vec2 vTexPos ;\n")
        }
    }

    override fun addExchangeDeclarations(sb: StringBuilder, shaderType: Int) {
        if (shaderType == SHADER_TYPE_VERTEX) {
            sb.append("out vec2 exTexPos;\n")
        }
        if (shaderType == SHADER_TYPE_FRAGMENT) {
            sb.append("in vec2 exTexPos;\n")
        }
    }

    override fun addUniformDeclarations(sb: StringBuilder, shaderType: Int) {
        if (shaderType == SHADER_TYPE_FRAGMENT) {
            sb.append("uniform sampler2D sampler;\n")
        }
    }

    override fun addFuncsDeclarations(sb: StringBuilder, shaderType: Int) {}

    override fun addCalculations(sb: StringBuilder, shaderType: Int) {
        if (shaderType == SHADER_TYPE_VERTEX) {
            sb.append("exTexPos = vTexPos;\n")
        }
        if (shaderType == SHADER_TYPE_FRAGMENT) {
            sb.append("fragColor = fragColor*texture(sampler, exTexPos);\n")
        }
    }
}