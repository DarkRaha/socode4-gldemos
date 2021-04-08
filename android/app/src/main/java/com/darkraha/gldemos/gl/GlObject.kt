package com.darkraha.gldemos.gl

import com.darkraha.gldemos.gl.modelling.GlModel
import org.joml.Matrix4f

class GlObject {
    var model: GlModel? = null
    var texture: GlTexture? = null
    var normalTexture: GlTexture? = null
    var transforms: Matrix4f? = null
}