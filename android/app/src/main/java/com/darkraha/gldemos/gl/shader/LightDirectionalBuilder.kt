package com.darkraha.opengldemokt.gl.shader

import com.darkraha.opengldemokt.gl.shader.ShaderProgramBuilder.Companion.SHADER_TYPE_FRAGMENT
import com.darkraha.opengldemokt.gl.shader.ShaderProgramBuilder.Companion.SHADER_TYPE_VERTEX
import java.lang.StringBuilder


class DirectionLightBuilder : LightBuilder() {
    
    override fun addExchangeDeclarations(sb: StringBuilder, shaderType: Int) {
        if (withBumping) {
            if (shaderType == SHADER_TYPE_VERTEX) {
                sb.append("out Light exLight;\n")
            }
            if (shaderType == SHADER_TYPE_FRAGMENT) {
                sb.append("in Light exLight;\n")
            }
        } else {
            if (shaderType == SHADER_TYPE_VERTEX) {
                sb.append("out vec3 exLighting;\n")
            }
            if (shaderType == SHADER_TYPE_FRAGMENT) {
                sb.append("in vec3 exLighting;\n")
            }
        }
    }

    override fun addFuncsDeclarations(sb: StringBuilder, shaderType: Int) {
        if (shaderType == SHADER_TYPE_VERTEX) {
            if (withBumping) {
                sb.append("\nvoid prepareLight(){\n")
                sb.append("  vec3 normal = normalize(m*vec4(vNormal,0)).xyz;\n")
                sb.append("  vec3 tangent = normalize(m*vec4(vTangent,0)).xyz;\n")
                sb.append("  tangent = normalize(tangent - dot(tangent, normal) * normal);\n")
                sb.append("  vec3 bitangent = cross(normal, tangent);\n")
                sb.append("  mat3 miTBN = transpose(mat3(tangent, bitangent, normal));\n")
                sb.append("  exLight = Light(light.ambient,light.diffuse,  normalize(miTBN*(light.direction)));\n")
                sb.append("}\n")
            } else {
                sb.append("vec3 calcLight(){\n")
                sb.append("  vec4 transformedNormal = mNormal *vec4 (vNormal,0.0);\n")
                sb.append("  float f = max(dot(transformedNormal.xyz,light.direction), 0.0);\n")
                sb.append("  return  light.ambient + (light.diffuse * f);\n")
                sb.append("}\n")
            }
        }
        if (shaderType == SHADER_TYPE_FRAGMENT) {
            if (withBumping) {
                sb.append("vec3 calcLight(){\n")
                sb.append("  vec3 bumpNormal = texture(normalSampler, exTexPos).xyz;\n")
                sb.append("  bumpNormal = 2.0 * bumpNormal - vec3(1.0, 1.0, 1.0);\n")
                sb.append("  float f = max(0.0, dot(bumpNormal.xyz, exLight.direction));\n")
                //   sb.append("  float f = dot(bumpNormal.xyz, exLight.direction);\n");
                sb.append("  return  clamp(exLight.ambient + (exLight.diffuse * f), 0.0,1.0);\n")
                //   sb.append("  return  exLight.ambient + (exLight.diffuse * f);\n");
                sb.append("}\n")
            }
        }
    }

    override fun addCalculations(sb: StringBuilder, shaderType: Int) {
        if (shaderType == SHADER_TYPE_VERTEX) {
            if (withBumping) {
                sb.append("  prepareLight();\n")
            } else {
                sb.append("  exLighting = calcLight();\n")
            }
        }
        if (shaderType == SHADER_TYPE_FRAGMENT) {
            if (withBumping) {
                sb.append("vec3 exLighting = calcLight();\n")
            }
            sb.append("  fragColor = vec4(fragColor.rgb * exLighting, fragColor.a);\n")
        }
    }
}