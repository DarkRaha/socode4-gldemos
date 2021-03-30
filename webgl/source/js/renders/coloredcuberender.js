class ColoredCubeRender extends Render {


    matrix
    prog
    idVao
    idVbo
    idIbo
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
            // front
            -1.0, -1.0, 1, /* */ 1.0, 0.0, 0.0, //
            1.0, -1.0, 1.0, /* */ 0.0, 1.0, 0.0, //
            1.0, 1.0, 1.0, /* */ 0.0, 0.0, 1.0, //
            -1.0, 1.0, 1.0, /* */ 1.0, 1.0, 1.0, //
            // back
            -1.0, -1.0, -1.0, /* */ 1.0, 0.0, 0.0, //
            1.0, -1.0, -1.0, /* */ 0.0, 1.0, 0.0, //
            1.0, 1.0, -1.0, /* */ 0.0, 0.0, 1.0, //
            -1.0, 1.0, -1.0, /* */ 1.0, 1.0, 1.0 //
        ];


        const indices = [
            // front
            0, 1, 2,
            2, 3, 0,
            // right
            1, 5, 6,
            6, 2, 1,
            // back
            7, 6, 5,
            5, 4, 7,
            // left
            4, 0, 3,
            3, 7, 4,
            // bottom
            4, 5, 1,
            1, 0, 4,
            // top
            3, 2, 6,
            6, 7, 3
        ];

        this.idVao = GlUtils.createVAO(gl);
        this.idVbo = GlUtils.createVBO(gl, vData);
        GlUtils.bindAttributes(gl, this.idVbo, 3, 3, false);
        this.idIbo = GlUtils.createIBO(gl, indices);

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
        gl.uniform1i(gl.getUniformLocation(prog.idProgram, "withTexture"), false);
        gl.uniform4f(gl.getUniformLocation(prog.idProgram, "uColor"), -1, -1, -1, -1);

        gl.uniformMatrix4fv(
            gl.getUniformLocation(prog.idProgram, 'matrix'),
            false, matrix);

        gl.bindVertexArray(this.idVao);
        //  gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, this.idIbo);
        gl.drawElements(gl.TRIANGLES, 36, gl.UNSIGNED_BYTE, 0);
    }

}