class Matrices {
    projection = glMatrix.mat4.create();
    view = glMatrix.mat4.create();
    model = glMatrix.mat4.create();
    viewModel = glMatrix.mat4.create();
    normals = glMatrix.mat4.create();
    matrix = glMatrix.mat4.create();


    constructor() {
        glMatrix.mat4.perspective(this.projection, 45 * Matrices.TO_RAD,
            1.0, 1.0, 100.0);
        const eyePos = glMatrix.vec3.fromValues(0, 0, 0);
        const ptPos = glMatrix.vec3.fromValues(0, 0, -1);
        const up = glMatrix.vec3.fromValues(0, 1, 0);
        glMatrix.mat4.lookAt(this.view, eyePos, ptPos, up);

    }

    applyModel(modelMatrix) {
        glMatrix.mat4.copy(this.model, modelMatrix);
        glMatrix.mat4.multiply(this.viewModel, this.view, modelMatrix);
        glMatrix.mat4.copy(this.matrix, this.projection)

        glMatrix.mat4.multiply(this.matrix, this.matrix, this.viewModel);

        glMatrix.mat4.invert(this.normals, this.viewModel);
        glMatrix.mat4.transpose(this.normals, this.normals);
    }

    applyCurrentModel() {
        glMatrix.mat4.multiply(this.viewModel, this.view, this.model);
        glMatrix.mat4.copy(this.matrix, this.projection)
        glMatrix.mat4.multiply(this.matrix, this.projection, this.viewModel);

        glMatrix.mat4.invert(this.normals, this.viewModel);
        glMatrix.mat4.transpose(this.normals, this.normals);
    }

    applyCameraModel(cameraMatrix, modelMatrix) {
        glMatrix.mat4.copy(this.model, modelMatrix);
        glMatrix.mat4.copy(this.camera, cameraMatrix);

        glMatrix.mat4.invert(this.view, this.camera);
        glMatrix.mat4.multiply(this.viewModel, this.view, modelMatrix);
        glMatrix.mat4.copy(this.matrix, this.projection)

        glMatrix.mat4.multiply(this.matrix, this.matrix, this.viewModel);

        glMatrix.mat4.invert(this.normals, this.viewModel);
        glMatrix.mat4.transpose(this.normals, this.normals);
    }
}

Matrices.TO_RAD = Math.PI / 180.0;