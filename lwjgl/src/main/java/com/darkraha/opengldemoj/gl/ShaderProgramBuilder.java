package com.darkraha.opengldemoj.gl;

public class ShaderProgramBuilder {

    public static final int A_LOCATION_VERTEX_POS = 0;
    public static final int A_LOCATION_VERTEX_COLOR = 1;
    public static final int A_LOCATION_VERTEX_NORMAL = 2;
    public static final int A_LOCATION_VERTEX_TEXPOS = 3;

    public static final int IND_VERTEX_POS = 0;
    public static final int IND_VERTEX_COLOR = 1;
    public static final int IND_VERTEX_NORMAL = 2;
    public static final int IND_VERTEX_TEX_POS = 3;
    public static final int IND_VERTEX_TANGENT = 4;
    public static final int IND_VERTEX_BITANGENT = 5;

    public static final int IND_MATRIX = 0;
    public static final int IND_MATRIX_PROJECTION = 1;
    public static final int IND_MATRIX_VIEW_MODEL = 2;
    public static final int IND_MATRIX_VIEW = 3;
    public static final int IND_MATRIX_MODEL = 4;
    public static final int IND_MATRIX_NORMAL = 5;

    public static final int LIGHT_TYPE_DIRECTIONAL_SINGLE = 0;
    public static final int LIGHT_TYPE_GOURAD = 1;
    public static final int LIGHT_TYPE_PHONG = 2;


    public static final int IND_EXCHANGE_COLOR = 0;
    public static final int IND_EXCHANGE_TEX_POS = 1;
    public static final int IND_EXCHANGE_LIGHT = 2;

    public static final String[] U_MATRIX_NAMES = new String[]{
            "m", "mP", "mVM", "mV", "mM", "mNormal"
    };

    public static final String[] INPUT_DATA_NAMES = new String[]{
            "vPos", "vColor", "vNormal", "vTexPos", "vTangent", "vBitangent"
    };


    public static final String[] U_LIGHT_NAMES = new String[]{
            "lightAmbient",
            "lightDiffuse",
            "lightDirection"
    };



    public static final String U_SOLID_COLOR_NAME = "solidColor";
    public static final String U_SAMPLER_NAME = "sampler";


    private final String[] inputDataDeclaration = {
            "layout(location=0) in vec4 vPos;\n",
            "layout(location=1) in vec4 vColor;\n",
            "layout(location=2) in vec3 vNormal;\n",
            "layout(location=3) in vec2 vTexPos ;\n",
            "layout(location=4) in vec3 vTangent ;\n",
            "layout(location=5) in vec3 vBitangent ;\n",
    };

    private final boolean[] useInputData = new boolean[inputDataDeclaration.length];


    private final String[] matricesDeclaration = new String[]{
            "uniform mat4 m;\n",
            "uniform mat4 mP;\n",
            "uniform mat4 mVM;\n",
            "uniform mat4 mV;\n",
            "uniform mat4 mM;\n",
            "uniform mat4 mNormal;\n"
    };

    private final boolean[] useMatrices = new boolean[matricesDeclaration.length];


    private final String solidColorDeclaration = "uniform vec4 solidColor;\n";
    private boolean useSolidColor;

    private int samplerCount = 0;
    private boolean useNormalSampler;
    private String[] samplerDeclaration = new String[]{
            "uniform sampler2D sampler;\n"

    };


    private int lightType = -1;

    private final String[] exchangeDataVS = new String[]{
            "out vec4 exColor;\n",
            "out vec2 exTexPos;\n",
            "out vec3 exLighting;\n"
    };

    private final String[] exchangeDataFS = new String[]{
            "in vec4 exColor;\n",
            "in vec2 exTexPos;\n",
            "in vec3 exLighting;\n"
    };

    private final String[] vertexSimpleLighting = new String[]{
            "uniform vec3 lightAmbient;\n",
            "uniform vec3 lightDiffuse;\n",
            "uniform vec3 lightDirection;\n",
    };


    private String version = "330 core";
    private String precisionFloat = "mediump";

    public ShaderProgramBuilder() {
        useInputData[0] = true;
        useMatrices[0] = true;
    }

    private void buildDeclareVertexLight(StringBuilder sb) {

        switch (lightType) {
            case LIGHT_TYPE_DIRECTIONAL_SINGLE:
                for (String s : vertexSimpleLighting) {
                    sb.append(s);
                }

                sb.append(exchangeDataVS[IND_EXCHANGE_LIGHT]);
                break;

            // TODO
            case LIGHT_TYPE_PHONG:
            case LIGHT_TYPE_GOURAD:
            default:
        }

    }

    private void addCalcLight(StringBuilder sb) {
        switch (lightType) {
            case LIGHT_TYPE_DIRECTIONAL_SINGLE:
                sb.append("vec3 calcLight(){\n");
                sb.append("  vec4 transformedNormal = mNormal *vec4 (vNormal,1.0);\n");
                sb.append("  float direction = max(dot(transformedNormal.xyz, lightDirection), 0.0);\n");
                sb.append("  return  lightAmbient + (lightDiffuse * direction);\n");
                sb.append("}\n");
                break;

            // TODO
            case LIGHT_TYPE_PHONG:
            case LIGHT_TYPE_GOURAD:
            default:
        }
    }

    private void buildAddVertexFunctions(StringBuilder sb) {
        addCalcLight(sb);
    }

    private void buildDeclareVertexIn(StringBuilder sb) {
        for (int i = 0; i < useInputData.length; ++i) {
            if (useInputData[i]) {
                sb.append(inputDataDeclaration[i]);
            }
        }
    }


    private void buildDeclareMatrices(StringBuilder sb) {
        for (int i = 0; i < useMatrices.length; ++i) {
            if (useMatrices[i]) {
                sb.append(matricesDeclaration[i]);
            }
        }
    }


    private String getCalcPositionV() {
        if (useMatrices[IND_MATRIX]) {
            return "    gl_Position = m  * vPos;\n";
        } else if (useMatrices[IND_MATRIX_VIEW_MODEL]) {
            return "    gl_Position = mP  * mVM * vPos;\n";
        } else if (useMatrices[IND_MATRIX_MODEL]) {
            return "    gl_Position = mP  * mV * mM * vPos;\n";
        } else {
            return "";
        }
    }

    private String getCalcColorV() {
        if (useInputData[IND_VERTEX_COLOR]) {
            return "exColor = vColor;\n";
        } else if (useSolidColor) {
            return "exColor = solidColor;\n";
        }

        return "";
    }

    private void buildCalcObjectColor(StringBuilder sb) {
        boolean isColor = useSolidColor || useInputData[IND_VERTEX_COLOR];
        boolean isTexture = useInputData[IND_VERTEX_TEX_POS];
        sb.append("vec4 objectColor = ");

        if (isTexture) {
            sb.append("texture(sampler, exTexPos)");
        }
        if (isColor && isTexture) {
            sb.append(" * ");
        }
        if (isColor) {
            sb.append("exColor ");
        }

        sb.append(";\n");

    }

    private void buildCalcColorF(StringBuilder sb) {
        switch (lightType) {
            case LIGHT_TYPE_DIRECTIONAL_SINGLE:
                sb.append("fragColor = vec4(objectColor.rgb * exLighting, objectColor.a);\n");
                break;
            case LIGHT_TYPE_PHONG:
            case LIGHT_TYPE_GOURAD:
            default:
                sb.append("fragColor = objectColor;\n");

        }
    }


    private String getCalcTexPositionV() {
        if (useInputData[IND_VERTEX_TEX_POS]) {
            return "exTexPos = vTexPos;\n";
        }
        return "";
    }

    private String getCalcLightV() {
        switch (lightType) {
            case LIGHT_TYPE_DIRECTIONAL_SINGLE:
            case LIGHT_TYPE_GOURAD:
                return " exLighting = calcLight();\n";

            default:
                return "";
        }
    }

    public String buildVertexShader() {
        StringBuilder sb = new StringBuilder();

        sb.append("#version ").append(version).append("\n");
        sb.append("precision ").append(precisionFloat).append(" float;\n");

        buildDeclareVertexIn(sb);
        buildDeclareMatrices(sb);
        buildDeclareVertexLight(sb);

        if (useSolidColor) {
            sb.append(solidColorDeclaration);
        }

        if (useSolidColor || useInputData[IND_VERTEX_COLOR]) {
            sb.append(exchangeDataVS[IND_EXCHANGE_COLOR]);
        }

        if (useInputData[IND_VERTEX_TEX_POS]) {
            sb.append(exchangeDataVS[IND_EXCHANGE_TEX_POS]);
        }

        buildAddVertexFunctions(sb);

        sb.append("void main() {\n");
        sb.append(getCalcPositionV());
        sb.append(getCalcTexPositionV());
        sb.append(getCalcColorV());
        sb.append(getCalcLightV());
        sb.append("}\n");
        System.out.println("VertexShader:\n " + sb.toString());
        return sb.toString();
    }

    public String buildFragmentShader() {
        StringBuilder sb = new StringBuilder();

        sb.append("#version ").append(version).append("\n");
        sb.append("precision ").append(precisionFloat).append(" float;\n");
        sb.append("out vec4 fragColor;\n");

        if (useSolidColor || useInputData[IND_VERTEX_COLOR]) {
            sb.append(exchangeDataFS[IND_EXCHANGE_COLOR]);
        }

        if (useInputData[IND_VERTEX_TEX_POS]) {
            sb.append(exchangeDataFS[IND_EXCHANGE_TEX_POS]);
            sb.append(samplerDeclaration[0]);
        }

        if (lightType == 0) {
            sb.append(exchangeDataFS[IND_EXCHANGE_LIGHT]);
        }

        sb.append("void main() {\n");
        buildCalcObjectColor(sb);
        buildCalcColorF(sb);

        sb.append("}\n");
        System.out.println("FragmentShader:\n " + sb.toString());
        return sb.toString();
    }

    //------------------------------------------------------------------
    public ShaderProgramBuilder vertexAttributes(boolean color, boolean texPos, boolean normals) {
        useInputData[IND_VERTEX_COLOR] = color;
        useInputData[IND_VERTEX_TEX_POS] = texPos;
        useInputData[IND_VERTEX_NORMAL] = normals;
        return this;
    }

    public ShaderProgramBuilder solidColor() {
        useSolidColor = true;
        return this;
    }

    public ShaderProgramBuilder version(String v) {
        version = v;
        return this;
    }

    public ShaderProgramBuilder precision(String v) {
        precisionFloat = v;
        return this;
    }

    public ShaderProgramBuilder lightDirectional() {
        lightType = LIGHT_TYPE_DIRECTIONAL_SINGLE;
        return this;
    }

    public ShaderProgramBuilder bumping() {
        useNormalSampler = true;
        return this;
    }


    public ShaderProgramBuilder matrix(boolean withNormal) {
        useMatrices[IND_MATRIX] = true;
        useMatrices[IND_MATRIX_NORMAL] = withNormal;
        return this;
    }

    public ShaderProgramBuilder matrix() {
        useMatrices[IND_MATRIX] = true;
        return this;
    }

    public ShaderProgramBuilder matrixP_VM() {
        useMatrices[IND_MATRIX_VIEW_MODEL] = true;
        useMatrices[IND_MATRIX_PROJECTION] = true;
        return this;
    }

    public ShaderProgramBuilder matrixP_V_M() {
        useMatrices[IND_MATRIX_VIEW] = true;
        useMatrices[IND_MATRIX_MODEL] = true;
        useMatrices[IND_MATRIX_PROJECTION] = true;
        return this;
    }


    public ShaderProgramBuilder normalMatrix() {
        useMatrices[IND_MATRIX_NORMAL] = true;
        return this;
    }



    public ShaderProgram build() {
        return new ShaderProgram(buildVertexShader(), buildFragmentShader());
    }
}
