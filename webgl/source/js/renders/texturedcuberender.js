class TexturedCubeRender extends Render {


    matrix
    prog
    cube
    rotY = 1.5 * Math.PI / 180.0;
    rotX = Math.PI / 180.0;
    texture
    solidColor = [1.0, 1.0, 1.0, 1.0];

    onSetup(appOGL) {
        super.onSetup(appOGL);
        const gl = appOGL.gl;

        const prog = new ShaderProgramBuilder(gl)
            //.colors(false, true)
            .texture2D()
            .build();
        this.prog = prog;
        gl.useProgram(prog.idProgram);

        this.matrix = glMatrix.mat4.create();
        glMatrix.mat4.perspective(this.matrix, 45 * Math.PI / 180.0,
            gl.canvas.clientWidth / gl.canvas.clientHeight,
            0.1, 100.0
        );

        glMatrix.mat4.translate(this.matrix, this.matrix, [0.0, 0.0, -6.0]);


        this.cube = Models.cube(1, 1, 1, "cube-0").toGlModel(gl);
        this.texture = GlTexture.newTexture2D(gl, "images/textures/235.jpg", "test");
    }

    onDrawFrame(appOGL) {
        super.onDrawFrame(appOGL);
        const mat4 = glMatrix.mat4;
        const gl = appOGL.gl;
        const prog = this.prog;

        mat4.rotate(this.matrix, this.matrix, this.rotX, [1, 0, 0]);
        mat4.rotate(this.matrix, this.matrix, this.rotY, [0, 1, 0]);

        gl.useProgram(prog.idProgram);
        prog.uniformTexture(this.texture);
        prog.uniformMatrix(this.matrix);
        //gl.uniform4fv(prog.solidColorLocation, this.solidColor);
        // gl.enable(gl.CULL_FACE);

        this.cube.draw(gl);
    }

}