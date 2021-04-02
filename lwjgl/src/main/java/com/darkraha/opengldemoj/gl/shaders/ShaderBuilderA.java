package com.darkraha.opengldemoj.gl.shaders;

abstract public class ShaderBuilderA {
    protected String mVersion = "#version 330 core\n";
    protected String mPrecision = "precision mediump float;\n";


    protected boolean mVertexCoords = false;
    protected boolean mVertexColors = false;
    protected boolean mVertexTextureCoords = false;
    protected boolean mVertexNormals = false;

    protected boolean mMatrix = false;
    protected boolean mProjectionMatrix = false;
    protected boolean mViewModelMatrix = false;
    protected boolean mViewMatrix = false;
    protected boolean mModelMatrix = false;
    protected boolean mNormalMatrix = false;

    protected boolean exOut = true;
    protected boolean exColor = false;
    protected boolean exTexCoord = false;
    protected boolean exLighting = false;


    protected String buildCalcTexCoord() {
        return mVertexTextureCoords ? "exTexCoord = vTexCoord;\n" : "";
    }

    protected String buildCalcColor() {
        return mVertexColors ? "exColor = vColor;\n" : "";
    }

    protected void setVertexInputData(boolean vCoords, boolean vColors, boolean vTexCoords, boolean vNormals) {
        mVertexCoords = vCoords;
        mVertexColors = vColors;
        mVertexTextureCoords = vTexCoords;
        mVertexNormals = vNormals;
    }


    protected String buildDeclareVertexInputData() {
        StringBuilder sb = new StringBuilder();

        if (mVertexCoords) {
            sb.append("layout(location=0) in vec4 vCoord;\n");
        }

        if (mVertexColors) {
            sb.append("layout(location=1) in vec4 vColor;\n");
        }

        if (mVertexNormals) {
            sb.append("layout(location=2) in vec3 vNormal;\n");
        }

        if (mVertexTextureCoords) {
            sb.append("layout(location=3) in vec2 vTexCoord ;\n");
        }

        return sb.toString();
    }

    protected void setMatrix(boolean matrix, boolean projMatrix,
                             boolean viewModelMatrix,
                             boolean viewMatrix,
                             boolean modelMatrix,
                             boolean normalMatrix
    ) {
        mMatrix = matrix;
        mProjectionMatrix = projMatrix;
        mViewMatrix = viewMatrix;
        mViewModelMatrix = viewModelMatrix;
        mModelMatrix = modelMatrix;
        mNormalMatrix = normalMatrix;
    }


    protected String buildCalcPosition() {
        if (mMatrix) {
            return "    gl_Position = matrix  * vCoord;\n";
        } else if (mViewModelMatrix) {
            return "    gl_Position = projMatrix  * viewModelMatrix * vCoord;\n";
        } else if (mModelMatrix) {
            return "    gl_Position = projMatrix  * viewMatrix * modelMatrix * vCoord;\n";
        } else {
            return "";
        }
    }


    protected String buildMatrixDeclare() {
        StringBuilder sb = new StringBuilder();

        if(mMatrix) {
            sb.append("uniform mat4 matrix;\n");
        }
        if(mProjectionMatrix) {
            sb.append("uniform mat4 projectionMatrix;\n");
        }
        if(mViewModelMatrix) {
            sb.append("uniform mat4 viewModelMatrix;\n");
        }
        if(mViewMatrix) {
            sb.append("uniform mat4 viewMatrix;\n");
        }
        if(mModelMatrix) {
            sb.append("uniform mat4 modelMatrix;\n");
        }
        if(mNormalMatrix) {
            sb.append("uniform mat4 normalMatrix;\n");
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

        return sb.toString();
    }

    protected void setExchangeData(boolean color, boolean texCoord, boolean lighting) {
        exColor = color;
        exTexCoord = texCoord;
        exLighting = lighting;
    }

    protected String buildDeclareExchangeOut() {

        StringBuilder sb = new StringBuilder();

        if (exColor) {
            sb.append("out vec4 exColor;\n");
        }

        if (exTexCoord) {
            sb.append("out vec2 exTexCoord;\n");
        }
        if (exLighting) {
            sb.append("out vec3 exLighting;\n");
        }

        return sb.toString();
    }


    protected String buildDeclareExchangeIn() {

        StringBuilder sb = new StringBuilder();

        if (exColor) {
            sb.append("in vec4 exColor;\n");
        }

        if (exTexCoord) {
            sb.append("in vec2 exTexCoord;\n");
        }
        if (exLighting) {
            sb.append("in vec3 exLighting;\n");
        }

        return sb.toString();
    }

    protected void setPrecision(int val) {
        String sval;
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
            default:
                throw new IllegalArgumentException(" only 0,1,2 values are allowed.");
        }
        mPrecision = "precision " + sval + " float;\n";
    }

    protected void setVersion(String versionValue) {
        mVersion = "#version " + versionValue + " \n";
    }


    protected String buildDeclareOutCustom() {
        return "";
    }

    protected String buildDeclareExchangeCustom() {
        return "";
    }

    protected String buildDeclareUniformCustom() {
        return "";
    }

    protected String buildCalcCustom() {
        return "";
    }


    public String build() {
        StringBuilder sb = new StringBuilder();
        sb.append(mVersion)
                .append(mPrecision)
                .append(buildDeclareVertexInputData())
                .append(buildMatrixDeclare())
                .append(buildDeclareUniformCustom())
                .append(exOut ? buildDeclareExchangeOut() : buildDeclareExchangeIn())
                .append(buildDeclareExchangeCustom())
                .append(buildDeclareOutCustom())
                .append("void main() {\n")
                .append(buildCalcCustom())
                .append("}\n");
        return sb.toString();
    }

}
