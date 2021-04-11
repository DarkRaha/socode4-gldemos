package com.darkraha.opengldemoj.gl.shader;

import static com.darkraha.opengldemoj.gl.shader.ShaderProgramBuilder.SHADER_TYPE_FRAGMENT;
import static com.darkraha.opengldemoj.gl.shader.ShaderProgramBuilder.SHADER_TYPE_VERTEX;

public class DirectionLightBuilder extends LightBuilder{


    @Override
    public void addExchangeDeclarations(StringBuilder sb, int shaderType) {

        if (shaderType == SHADER_TYPE_VERTEX) {
            sb.append("out vec3 exLighting;\n");
        }

        if (shaderType == SHADER_TYPE_FRAGMENT) {
            sb.append("in vec3 exLighting;\n");
        }

    }


    @Override
    public void addFuncsDeclarations(StringBuilder sb, int shaderType) {

        if (shaderType == SHADER_TYPE_VERTEX) {
            sb.append("vec3 calcLight(){\n");
            sb.append("  vec4 transformedNormal = mNormal *vec4 (vNormal,1.0);\n");
            sb.append("  float direction = max(dot(transformedNormal.xyz, light.direction), 0.0);\n");
            sb.append("  return  light.ambient + (light.diffuse * direction);\n");
            sb.append("}\n");
        }
    }

    @Override
    public void addCalculations(StringBuilder sb, int shaderType) {

        if(shaderType==SHADER_TYPE_VERTEX){
            sb.append(" exLighting = calcLight();\n");
        }

        if(shaderType==SHADER_TYPE_FRAGMENT){
            sb.append("fragColor = vec4(fragColor.rgb * exLighting, fragColor.a);\n");
        }
    }
}
