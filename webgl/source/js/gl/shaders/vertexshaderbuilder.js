class VertexShaderBuilder extends ShaderBuilderA {
    mLightAmbient
    mLightDirectionalColor
    mLightDirectionalVector

    directionalLightEmbeded(
        ambient, // float array
        lightDirectionalColor, // float array
        lightDirectionalVector // float array
    ) {
        this.mLightAmbient = ambient;
        this.mLightDirectionalColor = lightDirectionalColor;
        this.mLightDirectionalVector = lightDirectionalVector;
        return this;
    }

    buildCalcLightEmbeded() {
        if (this.mLightAmbient == null ||
            this.mLightDirectionalColor == null ||
            this.mLightDirectionalVector == null) {
            return "";
        }
        var sb = "";
        sb = sb + `vec3 lightAmbient = vec3(${this.mLightAmbient[0]}, ${this.mLightAmbient[1]}, ${this.mLightAmbient[2]});\n `;
        sb = sb + `vec3 lightDirectionalColor = vec3(${this.mLightDirectionalColor[0]}, ${this.mLightDirectionalColor[1]}, ${this.mLightDirectionalColor[2]});\n `;
        sb = sb + `vec3 lightDirectionalVector = vec3(${this.mLightDirectionalVector[0]}, ${this.mLightDirectionalVector[1]}, ${this.mLightDirectionalVector[2]});\n `;
        sb = sb + "vec4 transformedNormal = normalMatrix*vec4(vNormal,1.0);\n";
        sb = sb + "float directional = max(dot(transformedNormal.xyz, lightDirectionalVector), 0.0);\n";
        sb = sb + "exLighting =  lightAmbient + (lightDirectionalColor * directional);";

        return sb;
    }

    exchangeData(color, texCoord, lighting) {
        this.setExchangeData(color, texCoord, lighting);
        return this;
    }

    matrixData(normal) {
        this.mMatrix = true;
        this.mNormalMatrix = normal;
        this.mModelMatrix = false;
        this.mViewMatrix = false;
        this.mViewModelMatrix = false;
        this.mProjectionMatrix = false;
        return this;
    }

    matrixViewModelData(normal) {
        this.mMatrix = false;
        this.mNormalMatrix = normal;
        this.mModelMatrix = false;
        this.mViewMatrix = false;
        this.mViewModelMatrix = true;
        this.mProjectionMatrix = true;
        return this;
    }

    matrix3Data(normal) {
        this.mMatrix = false;
        this.mNormalMatrix = normal;
        this.mModelMatrix = true;
        this.mViewMatrix = true;
        this.mViewModelMatrix = false;
        this.mProjectionMatrix = true;
        return this;
    }

    vertexInputData(coord, color, texCoord, normal) {
        this.setVertexInputData(coord, color, texCoord, normal)
        return this;
    }

    version(versionValue) {
        this.setVersion(versionValue);
        return this;
    }

    precision(val) {
        this.setPrecision(val);
        return this;
    }

    buildCalcCustom() {
        var sb = "";
        sb = sb + this.buildCalcPosition();
        sb = sb + this.buildCalcTexCoord();
        sb = sb + this.buildCalcColor();
        sb = sb + this.buildCalcLightEmbeded();
        return sb;
    }
}