class BaseBuilder {
    matricesDeclaration = [
        "uniform mat4 m;\n",
        "uniform mat4 mP;\n",
        "uniform mat4 mVM;\n",
        "uniform mat4 mV;\n",
        "uniform mat4 mM;\n"
    ];

    useMatrices = [true, false, false, false, false];

    addTypeDeclarations(sb, shaderType) {
        return sb;
    }

    addInputDeclarations(sb, shaderType) {
        if (shaderType == 1) {
            sb = sb + "layout(location=0) in vec4 vPos;\n";
        }

        return sb;
    }

    addExchangeDeclarations(sb, shaderType) {
        return sb;
    }

    addUniformDeclarations(sb, shaderType) {
        if (shaderType == 1) {
            for (var i = 0; i < this.useMatrices.length; ++i) {
                if (this.useMatrices[i]) {
                    sb = sb + this.matricesDeclaration[i];
                }
            }
        }
        return sb;
    }

    addFuncsDeclarations(sb, shaderType) { return sb; }

    addCalculations(sb, shaderType) {

        if (1 == shaderType) {
            if (this.useMatrices[0]) {
                sb = sb + "    gl_Position = m  * vPos;\n";
            } else if (this.useMatrices[2]) {
                sb = sb + "    gl_Position = mP  * mVM * vPos;\n";
            } else if (this.useMatrices[4]) {
                sb = sb + "    gl_Position = mP  * mV * mM * vPos;\n";
            }
        }
        return sb;
    }
};


class ColorBuilder {
    usePerVertex = false;
    useSolidColor = false;

    addTypeDeclarations(sb, shaderType) {
        return sb;
    }

    addInputDeclarations(sb, shaderType) {
        if (shaderType == 1) {
            if (this.usePerVertex) {
                sb = sb + "layout(location=1) in vec4 vColor;\n";
            }
        }
        return sb;
    }

    addExchangeDeclarations(sb, shaderType) {
        if (this.usePerVertex || this.useSolidColor) {
            if (shaderType == 1) {
                sb = sb + "out vec4 exColor;\n";
            }
            if (shaderType == 2) {
                sb = sb + "in vec4 exColor;\n";
            }
        }
        if (shaderType == 2) {
            sb = sb + "out vec4 fragColor;\n";
        }

        return sb;
    }

    addUniformDeclarations(sb, shaderType) {
        if (this.useSolidColor) {
            sb = sb + "uniform vec4 solidColor;\n";
        }

        return sb;
    }

    addFuncsDeclarations(sb, shaderType) { return sb; }

    addCalculations(sb, shaderType) {
        if (shaderType == 1) {
            if (this.usePerVertex && this.useSolidColor) {
                sb = sb + "exColor = vColor * solidColor;\n";
            } else if (this.usePerVertex) {
                sb = sb + "exColor = vColor;\n";
            } else if (this.useSolidColor) {
                sb = sb + "exColor = solidColor;\n";
            }
        }
        if (shaderType == 2) {
            if (this.usePerVertex || this.useSolidColor) {
                sb = sb + "fragColor = exColor;\n";
            } else {
                sb = sb + "fragColor = vec4(1,1,1,1);\n";
            }
        }

        return sb;
    }
};

class Texture2DBuilder {

    addTypeDeclarations(sb, shaderType) { return sb; }

    addInputDeclarations(sb, shaderType) {
        if (shaderType == 1) {
            sb = sb + "layout(location=3) in vec2 vTexPos ;\n";
        }
        return sb;
    }

    addExchangeDeclarations(sb, shaderType) {
        if (shaderType == 1) {
            sb = sb + "out vec2 exTexPos;\n";
        }
        if (shaderType == 2) {
            sb = sb + "in vec2 exTexPos;\n";
        }
        return sb;
    }

    addUniformDeclarations(sb, shaderType) {
        if (shaderType == 2) {
            sb = sb + "uniform sampler2D sampler;\n";
        }
        return sb;
    }

    addFuncsDeclarations(sb, shaderType) { return sb; }

    addCalculations(sb, shaderType) {
        if (shaderType == 1) {
            sb = sb + "exTexPos = vTexPos;\n";
        }
        if (shaderType == 2) {
            sb = sb + "fragColor = fragColor*texture(sampler, exTexPos);\n";
        }
        return sb;
    }
};


class LightBuilder {
    withBumping = false;

    addTypeDeclarations(sb, shaderType) {
        sb = sb + "struct Light {\n";
        sb = sb + " vec3 ambient; \n";
        sb = sb + " vec3 diffuse; \n";
        sb = sb + " vec3 direction; \n";
        sb = sb + "};\n";
        return sb;
    }

    addInputDeclarations(sb, shaderType) {
        if (shaderType == 1) {
            sb = sb + "layout(location=2) in vec3 vNormal;\n";
        }
        return sb;
    }

    addUniformDeclarations(sb, shaderType) {
        sb = sb + "uniform mat4 mNormal;\n";

        if (shaderType == 1) {
            sb = sb + "uniform Light light;\n";
            sb = sb + "uniform vec3 ambient;\n";
        }
        return sb;
    }
};

class DirectionLightBuilder extends LightBuilder {
    addExchangeDeclarations(sb, shaderType) {
        if (shaderType == 1) {
            sb = sb + "out vec3 exLighting;\n";
        }

        if (shaderType == 2) {
            sb = sb + "in vec3 exLighting;\n";
        }

        return sb;
    }

    addFuncsDeclarations(sb, shaderType) {
        if (shaderType == 1) {
            sb = sb + "vec3 calcLight(){\n";
            sb = sb + "  vec4 transformedNormal = mNormal *vec4 (vNormal,1.0);\n";
            sb = sb + "  float direction = max(dot(transformedNormal.xyz, light.direction), 0.0);\n";
            sb = sb + "  return  light.ambient + (light.diffuse * direction);\n";
            //sb = sb + "  return vec3(0.5,0.5,0.5);\n";
            sb = sb + "}\n";
        }

        return sb;
    }

    addCalculations(sb, shaderType) {
        if (shaderType == 1) {
            sb = sb + " exLighting = calcLight();\n";
        }
        if (shaderType == 2) {
            sb = sb + "fragColor = vec4(fragColor.rgb * exLighting, fragColor.a);\n";

        }
        return sb;
    }
};



class ShaderProgramBuilder {
    gl
    version = "300 es";
    precisionFloat = "mediump";
    baseBuilder = new BaseBuilder();
    colorBuilder = new ColorBuilder();
    texture2DBuilder;
    lightBuilder;

    constructor(gl) {
        this.gl = gl;
    }

    buildShader(shaderType) {
        var sb = "";
        sb = sb + "#version " + this.version + "\n";
        sb = sb + "precision " + this.precisionFloat + " float;\n";


        sb = this.addTypeDeclarations(sb, shaderType);
        sb = this.addInputDeclarations(sb, shaderType);
        sb = this.addUniformDeclarations(sb, shaderType);
        sb = this.addExchangeDeclarations(sb, shaderType);
        sb = this.addFuncsDeclarations(sb, shaderType);

        sb = sb + "void main() {\n";
        sb = this.addCalculations(sb, shaderType);
        sb = sb + "}\n";
        console.log("Shader " + shaderType + " :\n" + sb);
        return sb;
    }

    buildVertexShader() {
        return this.buildShader(1);
    }

    buildFragmentShader() {
        return this.buildShader(2);
    }

    //------------------------------------------------------------------
    colors(usePerVertex, useSolidColor) {
        this.colorBuilder.useSolidColor = useSolidColor;
        this.colorBuilder.usePerVertex = usePerVertex;
        return this;
    }

    version(v) {
        this.version = v;
        return this;
    }

    precision(v) {
        this.precisionFloat = v;
        return this;
    }

    texture2D() {
        if (this.texture2DBuilder == null) {
            this.texture2DBuilder = new Texture2DBuilder();
        }
        return this;
    }

    lightDirectional(withBumping) {
        this.lightBuilder = new DirectionLightBuilder();
        this.lightBuilder.withBumping = withBumping;
        return this;
    }



    matrix(vBool) {
        this.baseBuilder.useMatrices[0] = vBool == undefined ? true : vBool; //!!v;
        return this;
    }

    matrixP_VM(v) {
        const newValue = v == undefined ? true : v;
        this.baseBuilder.useMatrices[3] = newValue;
        this.baseBuilder.useMatrices[1] = newValue;
        return this;
    }


    matrixP_V_M(v) {
        const newValue = v == undefined ? true : v;
        this.baseBuilder.useMatrices[3] = newValue;
        this.baseBuilder.useMatrices[4] = newValue;
        this.baseBuilder.useMatrices[1] = newValue;
        return this;
    }

    build() {
        return new ShaderProgram(this.gl,
            this.buildVertexShader(), this.buildFragmentShader());
    }

    //========================================================================
    addTypeDeclarations(sb, shaderType) {
        sb = this.baseBuilder.addTypeDeclarations(sb, shaderType);
        sb = this.colorBuilder.addTypeDeclarations(sb, shaderType);

        if (!!this.texture2DBuilder) {
            sb = this.texture2DBuilder.addTypeDeclarations(sb, shaderType);
        }

        if (!!this.lightBuilder) {
            sb = this.lightBuilder.addTypeDeclarations(sb, shaderType);
        }

        return sb;
    }

    addInputDeclarations(sb, shaderType) {
        sb = this.baseBuilder.addInputDeclarations(sb, shaderType);
        sb = this.colorBuilder.addInputDeclarations(sb, shaderType);

        if (!!this.texture2DBuilder) {
            sb = this.texture2DBuilder.addInputDeclarations(sb, shaderType);
        }

        if (!!this.lightBuilder) {
            sb = this.lightBuilder.addInputDeclarations(sb, shaderType)
        }
        return sb;
    }

    addExchangeDeclarations(sb, shaderType) {
        sb = this.baseBuilder.addExchangeDeclarations(sb, shaderType)
        sb = this.colorBuilder.addExchangeDeclarations(sb, shaderType)

        if (!!this.texture2DBuilder) {
            sb = this.texture2DBuilder.addExchangeDeclarations(sb, shaderType);
        }

        if (!!this.lightBuilder) {
            sb = this.lightBuilder.addExchangeDeclarations(sb, shaderType);
        }
        return sb;
    }

    addUniformDeclarations(sb, shaderType) {
        sb = this.baseBuilder.addUniformDeclarations(sb, shaderType)
        sb = this.colorBuilder.addUniformDeclarations(sb, shaderType)

        if (!!this.texture2DBuilder) {
            sb = this.texture2DBuilder.addUniformDeclarations(sb, shaderType);
        }


        if (!!this.lightBuilder) {
            sb = this.lightBuilder.addUniformDeclarations(sb, shaderType);
        }

        return sb;
    }

    addFuncsDeclarations(sb, shaderType) {
        sb = this.baseBuilder.addFuncsDeclarations(sb, shaderType)
        sb = this.colorBuilder.addFuncsDeclarations(sb, shaderType)

        if (!!this.texture2DBuilder) {
            sb = this.texture2DBuilder.addFuncsDeclarations(sb, shaderType);
        }

        if (!!this.lightBuilder) {
            sb = this.lightBuilder.addFuncsDeclarations(sb, shaderType);
        }

        return sb;
    }

    addCalculations(sb, shaderType) {
        sb = this.baseBuilder.addCalculations(sb, shaderType);
        sb = this.colorBuilder.addCalculations(sb, shaderType);

        if (!!this.texture2DBuilder) {
            sb = this.texture2DBuilder.addCalculations(sb, shaderType);
        }

        if (!!this.lightBuilder) {
            sb = this.lightBuilder.addCalculations(sb, shaderType);
        }

        return sb;
    }
};

ShaderProgramBuilder.SHADER_TYPE_VERTEX = 1;
ShaderProgramBuilder.SHADER_TYPE_FRAGMENT = 2;
ShaderProgramBuilder.A_LOCATION_VERTEX_POS = 0;
ShaderProgramBuilder.A_LOCATION_VERTEX_COLOR = 1;
ShaderProgramBuilder.A_LOCATION_VERTEX_NORMAL = 2;
ShaderProgramBuilder.A_LOCATION_VERTEX_TEXPOS = 3;
ShaderProgramBuilder.IND_VERTEX_POS = 0;
ShaderProgramBuilder.IND_VERTEX_COLOR = 1;
ShaderProgramBuilder.IND_VERTEX_NORMAL = 2;
ShaderProgramBuilder.IND_VERTEX_TEX_POS = 3;
ShaderProgramBuilder.IND_VERTEX_TANGENT = 4;
ShaderProgramBuilder.IND_VERTEX_BITANGENT = 5;
ShaderProgramBuilder.IND_MATRIX = 0;
ShaderProgramBuilder.IND_MATRIX_PROJECTION = 1;
ShaderProgramBuilder.IND_MATRIX_VIEW_MODEL = 2;
ShaderProgramBuilder.IND_MATRIX_VIEW = 3;
ShaderProgramBuilder.IND_MATRIX_MODEL = 4;
ShaderProgramBuilder.IND_MATRIX_NORMAL = 5;
ShaderProgramBuilder.U_SOLID_COLOR_NAME = "solidColor";
ShaderProgramBuilder.U_SAMPLER_NAME = "sampler";

ShaderProgramBuilder.U_MATRIX_NAMES = [
    "m", "mP", "mVM", "mV", "mM", "mNormal"
];
ShaderProgramBuilder.INPUT_DATA_NAMES = [
    "vPos", "vColor", "vNormal", "vTexPos", "vTangent", "vBitangent"
];
ShaderProgramBuilder.U_LIGHT_NAMES = [
    "light.ambient",
    "light.diffuse",
    "light.direction"
];