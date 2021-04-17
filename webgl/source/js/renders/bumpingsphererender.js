class BumpingSphereRender extends Render {
    withBump = true;
    matrices = new Matrices();
    prog
    progWithBump
    sphere = new GlObject();

    rotY = 1.5 * Math.PI / 180;
    rotX = Math.PI / 180;

    light = new LightDirectional(glMatrix.vec3.fromValues(0.3, 0.3, 0.3),
        glMatrix.vec3.fromValues(1, 1, 1),
        glMatrix.vec3.fromValues(0.85, 0.8, 0.75));

    onSetup(appOGL) {

        glMatrix.vec3.normalize(this.light.direction, this.light.direction);

        const gl = appOGL.gl;

        this.prog = new ShaderProgramBuilder(gl)
            .lightDirectional(false)
            .texture2D()
            .build();

        this.progWithBump = new ShaderProgramBuilder(gl)
            .lightDirectional(true)
            .texture2D()
            .build();

        gl.useProgram(this.prog.idProgram);

        this.sphere.model = Models
            .sphere(gl, 1,
                36, 36, 1, 0.5, 0.5, "sphere")
            .calcTangent()
            .toGlModel(gl);

        //this.sphere.texture = GlTexture.newTexture2D(gl, "images/textures/235.jpg", "test");
        this.sphere.texture = GlTexture.newTexture2D(gl, "images/textures/bricks/bricks053_1k.jpg", "bricks");
        this.sphere.normalTexture = GlTexture.newTexture2D(gl, "images/textures/bricks/bricks053_1k_normal.jpg", "bricks-normal");

        this.sphere.transforms = glMatrix.mat4.create();
        glMatrix.mat4.translate(this.sphere.transforms, this.sphere.transforms, [0, 0, -6]);

        gl.clearColor(0.5, 0.5, 0.5, 1.0); // Clear to black, fully opaque
        gl.clearDepth(1.0); // Clear everything
        gl.enable(gl.DEPTH_TEST); // Enable depth testing
        gl.depthFunc(gl.LEQUAL); // Near things obscure far things
    }

    onDrawFrame(appOGL) {
        const gl = appOGL.gl;
        gl.clear(gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT);

        const prog = this.withBump ? this.progWithBump : this.prog;

        glMatrix.mat4.rotate(this.sphere.transforms, this.sphere.transforms,
            this.rotX, [1, 0, 0]);
        glMatrix.mat4.rotate(this.sphere.transforms, this.sphere.transforms,
            this.rotY, [0, 1, 0]);

        gl.useProgram(prog.idProgram);
        prog.uniform(this.sphere, this.matrices, this.light);
        this.sphere.model.draw(gl);
    }

}