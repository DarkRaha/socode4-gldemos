package com.darkraha.opengldemoj.gl.shaders;

public class FragmentShaderBuilder extends ShaderBuilderA {

    public FragmentShaderBuilder() {
        exOut = false;
    }

    public FragmentShaderBuilder exchangeData(boolean color, boolean texCoord, boolean lighting) {
        exColor = color;
        exTexCoord = texCoord;
        exLighting = lighting;
        return this;
    }

    @Override
    protected String buildDeclareOutCustom() {
        return "out vec4 fragColor;\n";
    }

    @Override
    protected String buildCalcCustom() {
        StringBuilder sb = new StringBuilder();

        if (exLighting) {
            sb.append("vec4 texelColor = texture(uSampler, exTexCoord);\n");
            sb.append("fragColor = vec4(texelColor.rgb * exLighting, texelColor.a);\n");
        } else if (exTexCoord) {
            sb.append("fragColor = texture(uSampler, exTexCoord);\n");
        } else if (exColor) {
            sb.append("fragColor = exColor;\n");
        }

        return sb.toString();
    }

    @Override
    protected String buildDeclareUniformCustom() {
        // todo multiple textures
        return "uniform sampler2D uSampler;\n";
    }
}
