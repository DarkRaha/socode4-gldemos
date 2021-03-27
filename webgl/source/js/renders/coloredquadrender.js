class ColoredQuadRender extends Render {


    matrix
    prog
    idVao
    idVbo

    onSetup(appOGL) {
        super.onSetup(appOGL);
        const gl = appOGL.gl;
        const mat4 = glMatrix.mat4;
        const prog = ShaderProgram.createProgram(gl);
        this.prog = prog;
        this.matrix = glMatrix.mat4.create();

        gl.useProgram(prog.idProgram);
        gl.uniform1i(gl.getUniformLocation(prog.idProgram, "withTexture"), 0);
        gl.uniform4f(gl.getUniformLocation(prog.idProgram, "uColor"), -1, -1, -1, -1);

        const vData = [ //
            -1.0, 1.0, 1.0, 0.0, 0.0, //
            1.0, 1.0, 0.0, 1.0, 0.0, //
            -1.0, -1.0, 0.0, 0.0, 1.0, //
            1.0, -1.0, 1.0, 1.0, 1.0 //
        ];

        this.idVao = GlUtils.createVAO(gl);
        this.idVbo = GlUtils.createVBO(gl, vData);
        GlUtils.bindAttributes(gl, this.idVbo, 2, 3, false);


    }

    onDrawFrame(appOGL) {
        super.onDrawFrame(appOGL);
        const mat4 = glMatrix.mat4;
        const matrix = this.matrix;
        const gl = appOGL.gl;
        const prog = this.prog;
        const TO_RAD = Math.PI / 180.0;

        mat4.perspective(matrix, 45 * TO_RAD,
            gl.canvas.clientWidth / gl.canvas.clientHeight,
            0.1, 100.0
        );

        mat4.translate(matrix, matrix, [-0.0, 0.0, -6.0]);

        gl.useProgram(prog.idProgram);

        gl.uniformMatrix4fv(
            gl.getUniformLocation(prog.idProgram, 'matrix'),
            false, matrix);


        gl.bindVertexArray(this.idVao);
        gl.drawArrays(gl.TRIANGLE_STRIP, 0, 4);
    }

}