class QuadRender extends Render {

    matrix
    prog
    idVbo
    solidColor = [1.0, 1.0, 1.0, 1.0];

    onSetup(appOGL) {
        super.onSetup(appOGL);
        const gl = appOGL.gl;
        const mat4 = glMatrix.mat4;
        const prog = new ShaderProgramBuilder(gl).colors(false, true).build();
        const matrix = glMatrix.mat4.create();


        gl.useProgram(prog.idProgram);
        gl.uniform4fv(prog.solidColorLocation, this.solidColor);


        mat4.perspective(matrix,
            45 * Math.PI / 180,
            gl.canvas.clientWidth / gl.canvas.clientHeight,
            0.1, 100.0
        );

        mat4.translate(matrix, matrix, [-0.0, 0.0, -6.0]);

        // m is uniform variable of matrix in shader
        gl.uniformMatrix4fv(
            gl.getUniformLocation(prog.idProgram, 'm'),
            false, matrix);

        const positions = [-1.0, 1.0,
            1.0, 1.0, -1.0, -1.0,
            1.0, -1.0,
        ];
        /*
                const idVbo = gl.createBuffer();
                gl.bindBuffer(gl.ARRAY_BUFFER, idVbo);
                gl.bufferData(gl.ARRAY_BUFFER,
                    new Float32Array(positions),
                    gl.STATIC_DRAW);
          */

        const idVbo = GlUtils.createVBO(gl, positions);
        GlUtils.bindAttributes(gl, idVbo, 2, 0, false);

        this.idVbo = idVbo;
        this.matrix = matrix;
        this.prog = prog;
    }

    onDrawFrame(appOGL) {
        super.onDrawFrame(appOGL);
        const gl = appOGL.gl;

        gl.useProgram(this.prog.idProgram);
        gl.bindBuffer(gl.ARRAY_BUFFER, this.idVbo);
        gl.drawArrays(gl.TRIANGLE_STRIP, 0, 4);
    }
}