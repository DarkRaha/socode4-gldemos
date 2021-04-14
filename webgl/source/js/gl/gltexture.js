class GlTexture {
    idTexture
    textureUnit
    textureType
    name

    constructor(idTexture, textureUnit, textureType, name) {
        this.idTexture = idTexture;
        this.textureType = textureType;
        this.textureUnit = textureUnit;
        this.name = name;
    }


    dispose(gl) {
        //  gl.deleteTextures(idTexture);
    }

}


class TextureBuilder {

    gl
    mResPath
    mName
    mIdTexture = 0
    mTextureUnit
    mTextureType = 0
    mBorder = 0
    mWidth = 0
    mHeight = 0
    mFormat
    mImageData
    mGenMipmap = true
    mClampToEdge = false

    constructor(gl, resPath, name) {
        this.gl = gl;
        this.mResPath = resPath;
        this.mName = name;
        this.mTextureUnit = gl.TEXTURE0;
        this.mFormat = gl.RGBA;
    }

    genMipmap(v) {
        this.mGenMipmap = v;
        return this;
    }

    clampToEdge(v) {
        this.mClampToEdge = v;
        return this;
    }

    build2D() {
        const gl = this.gl;
        const ret = GlTexture.newSolidColorTexture(gl);
        const image = new Image();

        image.onload = () => {
            ret.textureType = gl.TEXTURE_2D;
            gl.bindTexture(ret.textureType, ret.idTexture);
            gl.texImage2D(ret.textureType, 0, gl.RGBA, gl.RGBA,
                gl.UNSIGNED_BYTE, image);

            gl.texParameteri(ret.textureType, gl.TEXTURE_MIN_FILTER, gl.LINEAR);
            gl.texParameteri(ret.textureType, gl.TEXTURE_MAG_FILTER, gl.LINEAR);


            if (this.mGenMipmap) {
                gl.generateMipmap(ret.textureType);
                gl.texParameteri(ret.textureType,
                    gl.TEXTURE_MIN_FILTER,
                    gl.LINEAR_MIPMAP_LINEAR);
            }

            if (this.mClampToEdge) {
                gl.texParameteri(ret.textureType, gl.TEXTURE_WRAP_S, gl.CLAMP_TO_EDGE);
                gl.texParameteri(ret.textureType, gl.TEXTURE_WRAP_T, gl.CLAMP_TO_EDGE);
            }


            //gl.bindTexture(ret.textureType, 0);
        };
        image.src = this.mResPath;

        return ret;
    }
}



GlTexture.newTexture2D = function(gl, resPath, name) {
    return new TextureBuilder(gl, resPath, name).build2D();
}


/**
 * 
 * @param {*} gl 
 * @param {*} r [0; 255]
 * @param {*} g [0; 255]
 * @param {*} b [0; 255]
 * @param {*} name 
 * @returns 
 */
GlTexture.newSolidColorTexture = function(gl, r, g, b, name) {
    const idTexture = gl.createTexture();
    gl.bindTexture(gl.TEXTURE_2D, idTexture);

    const level = 0;
    const internalFormat = gl.RGBA;
    const width = 1;
    const height = 1;
    const border = 0;
    const srcFormat = gl.RGBA;
    const srcType = gl.UNSIGNED_BYTE;
    const pixel = new Uint8Array([r, g, b, 255]);
    gl.texImage2D(gl.TEXTURE_2D, level, internalFormat,
        width, height, border, srcFormat, srcType,
        pixel);
    //gl.bindTexture(gl.TEXTURE_2D, 0);
    return new GlTexture(idTexture, gl.TEXTURE0, gl.TEXTURE_2D, name);
}