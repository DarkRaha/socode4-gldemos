class TexturedQuadRender extends Render {


    matrix
    prog
    idVao
    idVbo
    rotY = 1.5 * Math.PI / 180.0;
    rotX = Math.PI / 180.0;
    texture


    onSetup(appOGL) {
        super.onSetup(appOGL);
        const gl = appOGL.gl;

        const prog = new ShaderProgramBuilder(gl)
            .texture2D()
            .build();
        this.prog = prog;

        this.matrix = glMatrix.mat4.create();
        glMatrix.mat4.perspective(this.matrix, 45 * Math.PI / 180.0,
            gl.canvas.clientWidth / gl.canvas.clientHeight,
            0.1, 100.0
        );

        glMatrix.mat4.translate(this.matrix, this.matrix, [-0.0, 0.0, -6.0]);

        gl.useProgram(prog.idProgram);

        this.texture = GlTexture.newTexture2D(gl, "images/textures/235.jpg", "test");

        const vData = [
            // coords         texture coords
            -1.0, -1.0, /**/ 0.0, 1.0,
            1.0, -1.0, /**/ 1.0, 1.0,
            1.0, 1.0, /**/ 1.0, 0.0,
            1.0, 1.0, /**/ 1.0, 0.0, -1.0, 1.0, /**/ 0.0, 0.0, -1.0, -1.0, /**/ 0.0, 1.0
        ];

        this.idVao = GlUtils.createVAO(gl);
        this.idVbo = GlUtils.createVBO(gl, vData);
        GlUtils.bindAttributes(gl, this.idVbo, 2, 0, true);
    }

    onDrawFrame(appOGL) {
        super.onDrawFrame(appOGL);
        const mat4 = glMatrix.mat4;
        const matrix = this.matrix;
        const gl = appOGL.gl;
        const prog = this.prog;
        const TO_RAD = Math.PI / 180.0;


        mat4.rotate(matrix, matrix, this.rotX, [1, 0, 0]);
        mat4.rotate(matrix, matrix, this.rotY, [0, 1, 0]);

        gl.useProgram(prog.idProgram);
        gl.uniformMatrix4fv(gl.getUniformLocation(prog.idProgram, 'm'), false, matrix);


        // Tell WebGL we want to affect texture unit 0
        gl.activeTexture(gl.TEXTURE0);

        // Bind the texture to texture unit 0
        gl.bindTexture(gl.TEXTURE_2D, this.texture.idTexture);

        const sampleLocation = gl.getUniformLocation(prog.idProgram, 'sampler');

        // Tell the shader we bound the texture to texture unit 0
        gl.uniform1i(sampleLocation, 0);

        gl.bindVertexArray(this.idVao);

        // gl.enable(gl.CULL_FACE);
        gl.drawArrays(gl.TRIANGLES, 0, 6);
    }

}