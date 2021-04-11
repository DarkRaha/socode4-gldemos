package com.darkraha.opengldemokt.gl.shader

import com.darkraha.opengldemokt.gl.shader.ShaderProgramBuilder.Companion.SHADER_TYPE_FRAGMENT
import com.darkraha.opengldemokt.gl.shader.ShaderProgramBuilder.Companion.SHADER_TYPE_VERTEX
import java.lang.StringBuilder


class DirectionLightBuilder : LightBuilder() {
    override fun addExchangeDeclarations(sb: StringBuilder, shaderType: Int) {
        if (shaderType == SHADER_TYPE_VERTEX) {
            sb.append("out vec3 exLighting;\n")
        }
        if (shaderType == SHADER_TYPE_FRAGMENT) {
            sb.append("in vec3 exLighting;\n")
        }
    }

    override fun addFuncsDeclarations(sb: StringBuilder, shaderType: Int) {
        if (shaderType == SHADER_TYPE_VERTEX) {
            sb.append("vec3 calcLight(){\n")
            sb.append("  vec4 transformedNormal = mNormal *vec4 (vNormal,1.0);\n")
            sb.append("  float direction = max(dot(transformedNormal.xyz, light.direction), 0.0);\n")
            sb.append("  return  light.ambient + (light.diffuse * direction);\n")
            sb.append("}\n")
        }
    }

    override fun addCalculations(sb: StringBuilder, shaderType: Int) {
        if (shaderType == SHADER_TYPE_VERTEX) {
            sb.append(" exLighting = calcLight();\n")
        }
        if (shaderType == SHADER_TYPE_FRAGMENT) {
            sb.append("fragColor = vec4(fragColor.rgb * exLighting, fragColor.a);\n")
        }
    }
}