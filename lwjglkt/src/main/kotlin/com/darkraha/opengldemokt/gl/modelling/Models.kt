package com.darkraha.opengldemokt.gl.modelling


import org.lwjgl.opengl.GL33


object Models {
    fun quad(width: Float, height: Float, name: String): Model {
        val builder = Model.Builder()
        val coordx = width / 2.0f
        val coordy = height / 2.0f
        builder.drawType(GL33.GL_TRIANGLE_STRIP)
            .coordinates2d(
                floatArrayOf(
                    -coordx, coordy,
                    coordx, coordy,
                    -coordx, -coordy,
                    coordx, -coordy
                )
            )
            .colorsRGB(
                floatArrayOf(
                    1.0f, 0.0f, 0.0f,
                    0.0f, 1.0f, 0.0f,
                    0.0f, 0.0f, 1.0f,
                    1.0f, 1.0f, 1.0f
                )
            )
            .textureCoordinates(
                floatArrayOf(
                    0.0f, 0.0f,
                    1.0f, 0.0f,
                    0.0f, 1.0f,
                    1.0f, 1.0f
                )
            )
            .normals(
                floatArrayOf(
                    0f, 0f, 1f,  //
                    0f, 0f, 1f,  //
                    0f, 0f, 1f,  //
                    0f, 0f, 1f //
                )
            )
            .name(name)
        return builder.build()
    }

    /**
     * Generate sphere with center (0, 0, 0).
     *
     * @param radius  radius of sphere
     * @param sectors count of segments horizontally
     * @param stacks  count of segments vertically
     * @param name
     * @return
     */
    fun sphere(radius: Float, sectors: Int, stacks: Int, r: Float, g: Float, b: Float, name: String): Model {
        val builder = Model.Builder()
        val pi = Math.PI.toFloat()
        var x: Float
        var y: Float
        var z: Float
        var xy: Float // vertex position
        var nx: Float
        var ny: Float
        var nz: Float
        val lengthInv = 1.0f / radius // vertex normal
        var s: Float
        var t: Float // vertex texture coordinates
        val sectorStep = 2 * pi / sectors
        val stackStep = pi / stacks
        var sectorAngle: Float
        var stackAngle: Float
        var indVertex = 0
        val vertexCount = (stacks + 1) * (sectors + 1)
        val coords = FloatArray(vertexCount * 3)
        val colors = FloatArray(vertexCount * 3)
        val normals = FloatArray(vertexCount * 3)
        val st = FloatArray(vertexCount * 2)
        for (i in 0..stacks) {
            stackAngle = pi / 2f - i * stackStep // starting from pi/2 to -pi/2
            xy = radius * Math.cos(stackAngle.toDouble()).toFloat() // r * cos(u)
            z = radius * Math.sin(stackAngle.toDouble()).toFloat() // r * sin(u)

            // add (sectorCount+1) vertices per stack
            // the first and last vertices have same position and normal, but different tex coords
            var j = 0
            while (j <= sectors) {
                sectorAngle = j * sectorStep // starting from 0 to 2pi

                // vertex position (x, y, z)
                x = xy * Math.cos(sectorAngle.toDouble()).toFloat() // r * cos(u) * cos(v)
                y = xy * Math.sin(sectorAngle.toDouble()).toFloat() // r * cos(u) * sin(v)
                val vertexPos = indVertex * 3
                coords[vertexPos] = x
                coords[vertexPos + 1] = y
                coords[vertexPos + 2] = z

                // white
                colors[vertexPos] = r
                colors[vertexPos + 1] = g
                colors[vertexPos + 2] = b

                // normalized vertex normal (nx, ny, nz)
                nx = x * lengthInv
                ny = y * lengthInv
                nz = z * lengthInv
                normals[vertexPos] = nx
                normals[vertexPos + 1] = ny
                normals[vertexPos + 2] = nz

                // vertex tex coord (s, t) range between [0, 1]
                s = 1f - j.toFloat() / sectors
                t = 1f - i.toFloat() / stacks
                st[indVertex + indVertex] = s
                st[indVertex + indVertex + 1] = t
                ++j
                ++indVertex
            }
        }
        var ind = 0
        val indices = IntArray(stacks * sectors * 6 - 2 * sectors * 3)

        // generate CCW index list of sphere triangles
        // k1--k1+1
        // |  / |
        // | /  |
        // k2--k2+1
        var k1: Int
        var k2: Int
        for (i in 0 until stacks) {
            k1 = i * (sectors + 1) // beginning of current stack
            k2 = k1 + sectors + 1 // beginning of next stack
            var j = 0
            while (j < sectors) {


                // 2 triangles per sector excluding first and last stacks
                // k1 => k2 => k1+1
                if (i != 0) {
                    indices[ind] = k1
                    indices[ind + 1] = k2
                    indices[ind + 2] = k1 + 1
                    ind += 3
                }

                // k1+1 => k2 => k2+1
                if (i != stacks - 1) {
                    indices[ind] = k1 + 1
                    indices[ind + 1] = k2
                    indices[ind + 2] = k2 + 1
                    ind += 3
                }
                ++j
                ++k1
                ++k2
            }
        }
        builder.coordinates(coords)
            .colorsRGB(colors)
            .normals(normals)
            .textureCoordinates(st)
            .indices(indices)
            .name(name)
        return builder.build()
    }

    fun cube(r: Float, g: Float, b: Float, name: String): Model {
        val pos = floatArrayOf( // Front face
            -1.0f, -1.0f, 1.0f,
            1.0f, -1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f, 1.0f,  // Back face
            -1.0f, -1.0f, -1.0f,
            -1.0f, 1.0f, -1.0f,
            1.0f, 1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,  // Top face
            -1.0f, 1.0f, -1.0f,
            -1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, -1.0f,  // Bottom face
            -1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, 1.0f,
            -1.0f, -1.0f, 1.0f,  // Right face
            1.0f, -1.0f, -1.0f,
            1.0f, 1.0f, -1.0f,
            1.0f, 1.0f, 1.0f,
            1.0f, -1.0f, 1.0f,  // Left face
            -1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f, 1.0f,
            -1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f, -1.0f
        )

        val texPos = floatArrayOf( // Front
            0.0f, 0.0f,  //
            1.0f, 0.0f,  //
            1.0f, 1.0f,  //
            0.0f, 1.0f,  //
            // Back
            0.0f, 0.0f,  //
            1.0f, 0.0f,  //
            1.0f, 1.0f,  //
            0.0f, 1.0f,  //
            // Right
            0.0f, 0.0f,  //
            1.0f, 0.0f,  //
            1.0f, 1.0f,  //
            0.0f, 1.0f,  //
            // Left
            0.0f, 0.0f,  //
            1.0f, 0.0f,  //
            1.0f, 1.0f,  //
            0.0f, 1.0f,  //
            // Top
            0.0f, 0.0f,  //
            1.0f, 0.0f,  //
            1.0f, 1.0f,  //
            0.0f, 1.0f,  //
            // Bottom
            0.0f, 0.0f,  //
            1.0f, 0.0f,  //
            1.0f, 1.0f,  //
            0.0f, 1.0f
        )

        val indices = intArrayOf(
            0, 1, 2,  /*        */0, 2, 3,  // front
            4, 5, 6,  /*        */4, 6, 7,  // back
            8, 9, 10,  /*       */8, 10, 11,  // top
            12, 13, 14,  /*     */12, 14, 15,  // bottom
            16, 17, 18,  /*     */16, 18, 19,  // right
            20, 21, 22,  /*     */20, 22, 23
        )

        val color = FloatArray(pos.size)
        var i = 0
        while (i < color.size) {
            color[i] = r
            color[i + 1] = g
            color[i + 2] = b
            i += 3
        }

        val normals = floatArrayOf(
            0.0f, 0.0f, 1.0f,  //
            0.0f, 0.0f, 1.0f,  //
            0.0f, 0.0f, 1.0f,  //
            0.0f, 0.0f, 1.0f,  //
            // Back
            0.0f, 0.0f, -1.0f,  //
            0.0f, 0.0f, -1.0f,  //
            0.0f, 0.0f, -1.0f,  //
            0.0f, 0.0f, -1.0f,  //
            // Top
            0.0f, 1.0f, 0.0f,  //
            0.0f, 1.0f, 0.0f,  //
            0.0f, 1.0f, 0.0f,  //
            0.0f, 1.0f, 0.0f,  //
            // Bottom
            0.0f, -1.0f, 0.0f,  //
            0.0f, -1.0f, 0.0f,  //
            0.0f, -1.0f, 0.0f,  //
            0.0f, -1.0f, 0.0f,  //
            // Right
            1.0f, 0.0f, 0.0f,  //
            1.0f, 0.0f, 0.0f,  //
            1.0f, 0.0f, 0.0f,  //
            1.0f, 0.0f, 0.0f,  //
            // Left
            -1.0f, 0.0f, 0.0f,  //
            -1.0f, 0.0f, 0.0f,  //
            -1.0f, 0.0f, 0.0f,  //
            -1.0f, 0.0f, 0.0f //
        )
        return Model.Builder()
            .coordinates(pos)
            .textureCoordinates(texPos)
            .indices(indices)
            .colorsRGB(color)
            .normals(normals)
            .name(name)
            .build()
    }
}