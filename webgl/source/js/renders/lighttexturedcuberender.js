class LightTexturedCubeRender extends Render {


    matrices = new Matrices();
    prog
    cube = new GlObject();

    rotY = 1.5 * Math.PI / 180;
    rotX = Math.PI / 180;

    lightAmbient
    lightDiffuse
    lightDirection

    onSetup(appOGL) {

        this.lightAmbient = glMatrix.vec3.fromValues(0.3, 0.3, 0.3);
        this.lightDiffuse = glMatrix.vec3.fromValues(1.0, 1.0, 1.0);
        this.lightDirection = glMatrix.vec3.fromValues(0.85, 0.8, 0.75);
        glMatrix.vec3.normalize(this.lightDirection, this.lightDirection);

        const gl = appOGL.gl;


        this.prog = new ShaderProgramBuilder(gl)
            .lightDirectional(false)
            .texture2D()
            .build();

        gl.useProgram(this.prog.idProgram);

        glMatrix.mat4.translate(this.matrices.model, this.matrices.model, [0.0, 0.0, -6.0]);

        this.cube.model = Models.cube(gl, 1, 1, 1, "cube").toGlModel(gl);
        this.cube.texture = GlTexture.newTexture2D(gl, "images/textures/235.jpg", "test");

        gl.clearColor(0.0, 0.0, 0.0, 1.0); // Clear to black, fully opaque
        gl.clearDepth(1.0); // Clear everything
        gl.enable(gl.DEPTH_TEST); // Enable depth testing
        gl.depthFunc(gl.LEQUAL); // Near things obscure far things
    }

    onDrawFrame(appOGL) {

        const gl = appOGL.gl;
        gl.clear(gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT);

        glMatrix.mat4.rotate(this.matrices.model, this.matrices.model,
            this.rotX, [1, 0, 0]);
        glMatrix.mat4.rotate(this.matrices.model, this.matrices.model,
            this.rotY, [0, 1, 0]);

        gl.useProgram(this.prog.idProgram);

        this.matrices.applyCurrentModel();

        this.prog.uniformTexture(this.cube.texture);
        this.prog.uniformMatrices(this.matrices);
        this.prog.uniformDirectionalLight(this.lightAmbient,
            this.lightDiffuse, this.lightDirection);

        this.cube.model.draw(gl);
    }

}