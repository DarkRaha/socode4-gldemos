package com.darkraha.opengldemoj.gl;

import org.joml.Vector3f;

/**
 * For simple directional light
 */
public class LightDirectional {

    public LightDirectional() {

    }

    public LightDirectional(Vector3f ambient, Vector3f diffuseColor, Vector3f direction) {
        this.ambient = ambient;
        this.diffuseColor = diffuseColor;
        this.direction = direction;
    }

    public Vector3f ambient;
    public Vector3f diffuseColor;
    public Vector3f direction;

}
