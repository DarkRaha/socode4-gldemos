package com.darkraha.opengldemokt.gl.shader

import com.darkraha.opengldemokt.gl.shader.ShaderProgramBuilder.Companion.SHADER_TYPE_VERTEX
import java.lang.StringBuilder


abstract class LightBuilder : HelperBuilder {
    var withBumping = false

    override fun addTypeDeclarations(sb: StringBuilder, shaderType: Int) {
        sb.append("struct Light {\n")
        sb.append(" vec3 ambient; \n")
        sb.append(" vec3 diffuse; \n")
        sb.append(" vec3 direction; \n")
        sb.append("};\n")
    }

    override fun addInputDeclarations(sb: StringBuilder, shaderType: Int) {
        if (shaderType == SHADER_TYPE_VERTEX) {
            sb.append("layout(location=2) in vec3 vNormal;\n")
            if (withBumping) {
                sb.append("layout(location=4) in vec3 vTangent;\n")
            }
        }
    }

    override fun addUniformDeclarations(sb: StringBuilder, shaderType: Int) {
        sb.append("uniform mat4 mNormal;\n")
        if (shaderType == SHADER_TYPE_VERTEX) {
            sb.append("uniform Light light;\n")
        }
        if (shaderType == ShaderProgramBuilder.SHADER_TYPE_FRAGMENT) {
            if (withBumping) {
                sb.append("uniform sampler2D normalSampler;\n")
            }
        }
    }

}