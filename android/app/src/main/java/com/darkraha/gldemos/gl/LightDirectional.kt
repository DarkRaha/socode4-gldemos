package com.darkraha.gldemos.gl

import org.joml.Vector3f

class LightDirectional {
    constructor(ambient: Vector3f?, diffuseColor: Vector3f?, direction: Vector3f?) {
        this.ambient = ambient
        this.diffuseColor = diffuseColor
        this.direction = direction
    }

    constructor() : this(null, null, null)

    var ambient: Vector3f? = null
    var diffuseColor: Vector3f? = null
    var direction: Vector3f? = null
}