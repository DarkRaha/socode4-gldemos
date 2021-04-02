class ShaderBuilderA {
    mVersion = "#version 300 es\n"
    mPrecision = "precision mediump float;\n"
    mVertexCoords = false
    mVertexColors = false
    mVertexTextureCoords = false
    mVertexNormals = false
    mMatrix = false
    mProjectionMatrix = false
    mViewModelMatrix = false
    mViewMatrix = false
    mModelMatrix = false
    mNormalMatrix = false
    exOut = true
    exColor = false
    exTexCoord = false
    exLighting = false

    buildCalcTexCoord() {
        return this.mVertexTextureCoords ? "exTexCoord = vTexCoord;\n" : "";
    }

    buildCalcColor() {
        return this.mVertexColors ? "exColor = vColor;\n" : "";
    }

    setVertexInputData(vCoords, vColors, vTexCoords, vNormals) {
        this.mVertexCoords = vCoords;
        this.mVertexColors = vColors;
        this.mVertexTextureCoords = vTexCoords;
        this.mVertexNormals = vNormals;
    }

    buildDeclareVertexInputData() {
        var sb = "";

        if (this.mVertexCoords) {
            sb = sb + "layout(location=0) in vec4 vCoord;\n";
        }
        if (this.mVertexColors) {
            sb = sb + "layout(location=1) in vec4 vColor;\n";
        }
        if (this.mVertexNormals) {
            sb = sb + "layout(location=2) in vec3 vNormal;\n";
        }
        if (this.mVertexTextureCoords) {
            sb = sb + "layout(location=3) in vec2 vTexCoord ;\n";
        }
        return sb;
    }

    setMatrix(
        matrix, projMatrix,
        viewModelMatrix,
        viewMatrix,
        modelMatrix,
        normalMatrix
    ) {
        this.mMatrix = matrix;
        this.mProjectionMatrix = projMatrix;
        this.mViewMatrix = viewMatrix;
        this.mViewModelMatrix = viewModelMatrix;
        this.mModelMatrix = modelMatrix;
        this.mNormalMatrix = normalMatrix;
    }

    buildCalcPosition() {
        var sb = "";
        if (this.mMatrix) {
            sb = "    gl_Position = matrix  * vCoord;\n";
        } else if (this.mViewModelMatrix) {
            sb = "    gl_Position = projMatrix  * viewModelMatrix * vCoord;\n";
        } else if (this.mModelMatrix) {
            sb = "    gl_Position = projMatrix  * viewMatrix * modelMatrix * vCoord;\n";
        }
        return sb;
    }

    buildMatrixDeclare() {
        var sb = "";

        if (this.mMatrix) {
            sb = sb + "uniform mat4 matrix;\n";
        }
        if (this.mProjectionMatrix) {
            sb = sb + "uniform mat4 projectionMatrix;\n";
        }
        if (this.mViewModelMatrix) {
            sb = sb + "uniform mat4 viewModelMatrix;\n";
        }
        if (this.mViewMatrix) {
            sb = sb + "uniform mat4 viewMatrix;\n";
        }
        if (this.mModelMatrix) {
            sb = sb + "uniform mat4 modelMatrix;\n";
        }
        if (this.mNormalMatrix) {
            sb = sb + "uniform mat4 normalMatrix;\n";
        }
        /*  //binding not supported
        if (mMatrix) {
            sb.append("layout(binding=0) uniform mat4 matrix;\n");
        }

        if (mProjectionMatrix) {
            sb.append("layout(binding=1) uniform mat4 projectionMatrix;\n");
        }

        if (mViewModelMatrix) {
            sb.append("layout(binding=2) uniform mat4 viewModelMatrix;\n");
        }

        if (mViewMatrix) {
            sb.append("layout(binding=3) uniform mat4 viewMatrix;\n");
        }

        if (mModelMatrix) {
            sb.append("layout(binding=4) uniform mat4 modelMatrix;\n");
        }

        if (mNormalMatrix) {
            sb.append("layout(binding=5) uniform mat4 normalMatrix;\n");
        }*/
        return sb;
    }

    setExchangeData(color, texCoord, lighting) {
        this.exColor = color;
        this.exTexCoord = texCoord;
        this.exLighting = lighting;
    }

    buildDeclareExchangeOut() {
        var sb = "";
        if (this.exColor) {
            sb = sb + "out vec4 exColor;\n";
        }
        if (this.exTexCoord) {
            sb = sb + "out vec2 exTexCoord;\n";
        }
        if (this.exLighting) {
            sb = sb + "out vec3 exLighting;\n";
        }
        return sb;
    }

    buildDeclareExchangeIn() {
        var sb = "";
        if (this.exColor) {
            sb = sb + "in vec4 exColor;\n";
        }
        if (this.exTexCoord) {
            sb = sb + "in vec2 exTexCoord;\n";
        }
        if (this.exLighting) {
            sb = sb + "in vec3 exLighting;\n";
        }
        return sb;
    }

    setPrecision(val) {
        var sval = "";
        switch (val) {

            case 0:
                sval = "lowp";
                break;
            case 1:
                sval = "mediump";
                break;
            case 2:
                sval = "highp";
                break;
        }

        mPrecision = `precision ${sval} float;\n`;
    }


    buildDeclareOutCustom() {
        return "";
    }

    buildDeclareExchangeCustom() {
        return "";
    }

    buildDeclareUniformCustom() {
        return "";
    }

    buildCalcCustom() {
        return "";
    }

    build() {
        var sb = "";
        sb = sb + this.mVersion;
        sb = sb + this.mPrecision;
        sb = sb + this.buildDeclareVertexInputData();
        sb = sb + this.buildMatrixDeclare();
        sb = sb + this.buildDeclareUniformCustom();
        sb = sb + (this.exOut ? this.buildDeclareExchangeOut() : this.buildDeclareExchangeIn());
        sb = sb + this.buildDeclareExchangeCustom();
        sb = sb + this.buildDeclareOutCustom();
        sb = sb + "void main() {\n";
        sb = sb + this.buildCalcCustom();
        sb = sb + "}\n";
        return sb;
    }
}