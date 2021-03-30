class TexturedQuadRender extends Render {


    matrix
    prog
    idVao
    idVbo
    idTexture
    rotY = 0
    rotX = 0

    onSetup(appOGL) {
        super.onSetup(appOGL);
        const gl = appOGL.gl;
        const prog = ShaderProgram.createProgram(gl);
        this.prog = prog;
        this.matrix = glMatrix.mat4.create();

        gl.useProgram(prog.idProgram);

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

        this.idTexture = GlUtils.loadTextureDefault(gl, "img/textures/235.jpg");
        //      this.idTexture = GlUtils.loadTextureDefault(gl, "/images/textures/235.jpg");
    }

    onDrawFrame(appOGL) {
        super.onDrawFrame(appOGL);
        const mat4 = glMatrix.mat4;
        const matrix = this.matrix;
        const gl = appOGL.gl;
        const prog = this.prog;
        const TO_RAD = Math.PI / 180.0;


        var rotY = this.rotY;
        var rotX = this.rotX;

        rotY += 1.5 * TO_RAD
        if (rotY > 2 * Math.PI) {
            rotY = rotY - 2 * Math.PI
        }
        rotX += 1 * TO_RAD
        if (rotX > 2 * Math.PI) {
            rotX = rotX - 2 * Math.PI
        }

        this.rotX = rotX;
        this.rotY = rotY;


        mat4.perspective(matrix, 45 * TO_RAD,
            gl.canvas.clientWidth / gl.canvas.clientHeight,
            0.1, 100.0
        );

        mat4.translate(matrix, matrix, [-0.0, 0.0, -6.0]);
        mat4.rotate(matrix, matrix, rotX, [1, 0, 0]);
        mat4.rotate(matrix, matrix, rotY, [0, 1, 0]);

        gl.useProgram(prog.idProgram);
        gl.uniform1i(gl.getUniformLocation(prog.idProgram, "withTexture"), true);
        gl.uniform4f(gl.getUniformLocation(prog.idProgram, "uColor"), -1, -1, -1, -1);

        gl.uniformMatrix4fv(
            gl.getUniformLocation(prog.idProgram, 'matrix'),
            false, matrix);


        // Tell WebGL we want to affect texture unit 0
        gl.activeTexture(gl.TEXTURE0);

        // Bind the texture to texture unit 0
        gl.bindTexture(gl.TEXTURE_2D, this.idTexture);

        const sampleLocation = gl.getUniformLocation(prog.idProgram, 'texSampler');

        // Tell the shader we bound the texture to texture unit 0
        gl.uniform1i(sampleLocation, 0);

        gl.bindVertexArray(this.idVao);

        gl.enable(gl.CULL_FACE);
        gl.drawArrays(gl.TRIANGLES, 0, 6);
    }

}