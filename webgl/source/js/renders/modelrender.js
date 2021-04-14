/**
 * For testing different models.
 */
class ModelRender extends Render {


    matrices = new Matrices();
    prog
    globject = new GlObject();
    matrix = glMatrix.mat4.create();
    rotY = 1.5 * Math.PI / 180.0;
    rotX = Math.PI / 180.0;

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
            .matrixP_V_M()
            .matrix(false)
            .colors(false, false)
            .lightDirectional(false)
            .texture2D()
            .build();

        gl.useProgram(this.prog.idProgram);

        glMatrix.mat4.translate(this.matrices.model, this.matrices.model, [0.0, 0.0, -6.0]);
        this.globject.model = Models.cube(gl, 1, 0, 0, "cube").toGlModel(gl);

        //  this.globject.model = Models.quad(gl, 2, 2, "quad").toGlModel(gl);

        this.globject.texture = GlTexture.newTexture2D(gl,
            "images/textures/235.jpg", "test");

        gl.clearColor(0.5, 0.5, 0.5, 1.0);
        gl.clearDepth(1.0); // Clear everything
        gl.enable(gl.DEPTH_TEST); // Enable depth testing
        gl.depthFunc(gl.LEQUAL); // Near things obscure far things

        console.log("max uniforms blocks", gl.getParameter(gl.MAX_COMBINED_UNIFORM_BLOCKS));
        console.log("active uniforms",
            gl.getProgramParameter(this.prog.idProgram, gl.ACTIVE_UNIFORMS));


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

        this.prog.uniformTexture(this.globject.texture);
        this.prog.uniformMatrices(this.matrices);
        this.prog.uniformDirectionalLight(this.lightAmbient,
            this.lightDiffuse, this.lightDirection);

        this.globject.model.draw(gl);
    }


}