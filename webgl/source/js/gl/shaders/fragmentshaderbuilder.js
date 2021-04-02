class FragmentShaderBuilder extends ShaderBuilderA {
    constructor() {
        super();
        this.exOut = false;
    }
    exchangeData(color, texCoord, lighting) {

        this.exColor = color;
        this.exTexCoord = texCoord;
        this.exLighting = lighting;
        return this;
    }

    buildDeclareOutCustom() {
        return "out vec4 fragColor;\n";
    }

    buildCalcCustom() {
        var sb = "";

        if (this.exLighting) {
            sb = sb + "vec4 texelColor = texture(uSampler, exTexCoord);\n";
            sb = sb + "fragColor = vec4(texelColor.rgb * exLighting, texelColor.a);\n";
        } else if (this.exTexCoord) {
            sb = sb + "fragColor = texture(uSampler, exTexCoord);\n";
        } else if (this.exColor) {
            sb = sb + "fragColor = exColor;\n";
        }
        return sb;
    }

    buildDeclareUniformCustom() {
        // todo multiple textures
        return "uniform sampler2D uSampler;\n";
    }


}