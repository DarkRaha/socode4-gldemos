package com.darkraha.opengldemoj.gl.shaders;

public class VertexShaderBuilder extends ShaderBuilderA {

    private float[] mLightAmbient;
    private float[] mLightDirectionalColor;
    private float[] mLightDirectionalVector;


    public VertexShaderBuilder directionalLightEmbeded(float[] ambient,
                                                       float[] lightDirectionalColor,
                                                       float[] lightDirectionalVector
    ) {
        mLightAmbient = ambient;
        mLightDirectionalColor = lightDirectionalColor;
        mLightDirectionalVector = lightDirectionalVector;
        return this;
    }


    protected String buildCalcLightEmbeded() {
        if (mLightAmbient == null || mLightDirectionalColor == null
                || mLightDirectionalVector == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("vec3 lightAmbient = vec3(")
                .append(mLightAmbient[0]).append(",")
                .append(mLightAmbient[1]).append(",")
                .append(mLightAmbient[2]).append(");\n");

        sb.append("vec3 lightDirectionalColor = vec3(")
                .append(mLightDirectionalColor[0]).append(",")
                .append(mLightDirectionalColor[1]).append(",")
                .append(mLightDirectionalColor[2]).append(");\n");

        sb.append("vec3 lightDirectionalVector = vec3(")
                .append(mLightDirectionalVector[0]).append(",")
                .append(mLightDirectionalVector[1]).append(",")
                .append(mLightDirectionalVector[2]).append(");\n");


        sb.append("vec4 transformedNormal = normalMatrix*vec4(vNormal,1.0);\n");
        sb.append("float directional = max(dot(transformedNormal.xyz, lightDirectionalVector), 0.0);\n");
        sb.append("exLighting =  lightAmbient + (lightDirectionalColor * directional);");
        return sb.toString();
    }


    public VertexShaderBuilder exchangeData(boolean color, boolean texCoord, boolean lighting) {
        setExchangeData(color, texCoord, lighting);
        return this;
    }

    public VertexShaderBuilder matrixData(boolean normal) {
        mMatrix = true;
        mNormalMatrix = normal;
        mModelMatrix = false;
        mViewMatrix = false;
        mViewModelMatrix = false;
        mProjectionMatrix = false;
        return this;
    }

    public VertexShaderBuilder matrixViewModelData(boolean normal) {
        mMatrix = false;
        mNormalMatrix = normal;
        mModelMatrix = false;
        mViewMatrix = false;
        mViewModelMatrix = true;
        mProjectionMatrix = true;
        return this;
    }


    public VertexShaderBuilder matrix3Data(boolean normal) {
        mMatrix = false;
        mNormalMatrix = normal;
        mModelMatrix = true;
        mViewMatrix = true;
        mViewModelMatrix = false;
        mProjectionMatrix = true;
        return this;
    }


    public VertexShaderBuilder vertexInputData(boolean coord, boolean color, boolean texCoord, boolean normal) {
        setVertexInputData(coord, color, texCoord, normal);
        return this;
    }

    public VertexShaderBuilder version(String versionValue) {
        setVersion(versionValue);
        return this;
    }

    public VertexShaderBuilder precision(int val) {
        setPrecision(val);
        return this;
    }


    @Override
    protected String buildCalcCustom() {
        StringBuilder sb = new StringBuilder();
        sb.append(buildCalcPosition());
        sb.append(buildCalcTexCoord());
        sb.append(buildCalcColor());
        sb.append(buildCalcLightEmbeded());
        return sb.toString();
    }
}
