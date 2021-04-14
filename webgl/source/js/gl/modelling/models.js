function Models() {}


Models.quad = function(gl, width, height, name) {
    const builder = new ModelBuilder();
    const coordx = width / 2.0;
    const coordy = height / 2.0;

    builder.drawType(gl.TRIANGLE_STRIP)
        .coordinates2d(
            [-coordx, coordy,
                coordx, coordy, -coordx, -coordy,
                coordx, -coordy
            ]

        ).colorsRGB(
            [
                1.0, 0.0, 0.0,
                0.0, 1.0, 0.0,
                0.0, 0.0, 1.0,
                1.0, 1.0, 1.0
            ])
        .textureCoordinates(
            [
                0.0, 0.0,
                1.0, 0.0,
                0.0, 1.0,
                1.0, 1.0
            ]

        )
        .normals(
            [0, 0, 1, //
                0, 0, 1, //
                0, 0, 1, //
                0, 0, 1
            ])
        .name(name);

    console.log("models.js posComponents=", builder.model.posComponents);
    return builder.build();
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
Models.sphere = function(gl, radius, sectors, stacks, r, g, b, name) {
    const builder = new ModelBuilder();
    const pi = Math.PI;
    var x;
    var y;
    var z;
    var xy; // vertex position
    var nx;
    var ny;
    var nz;
    const lengthInv = 1.0 / radius; // vertex normal
    var s;
    var t; // vertex texture coordinates
    const sectorStep = 2 * pi / sectors;
    const stackStep = pi / stacks;
    var sectorAngle;
    var stackAngle;
    var indVertex = 0;
    var vertexCount = (stacks + 1) * (sectors + 1);
    const coords = new Array(vertexCount * 3);
    const colors = new Array(vertexCount * 3);
    const normals = new Array(vertexCount * 3);
    const st = new Array(vertexCount * 2);

    for (var i = 0; i <= stacks; ++i) {
        stackAngle = pi / 2.0 - i * stackStep; // starting from pi/2 to -pi/2
        xy = radius * Math.cos(stackAngle); // r * cos(u)
        z = radius * Math.sin(stackAngle); // r * sin(u)

        // add (sectorCount+1) vertices per stack
        // the first and last vertices have same position and normal, but different tex coords
        var j = 0;
        while (j <= sectors) {
            sectorAngle = j * sectorStep; // starting from 0 to 2pi

            // vertex position (x, y, z)
            x = xy * Math.cos(sectorAngle); // r * cos(u) * cos(v)
            y = xy * Math.sin(sectorAngle); // r * cos(u) * sin(v)
            const vertexPos = indVertex * 3;
            coords[vertexPos] = x;
            coords[vertexPos + 1] = y;
            coords[vertexPos + 2] = z;

            // white
            colors[vertexPos] = r;
            colors[vertexPos + 1] = g;
            colors[vertexPos + 2] = b;

            // normalized vertex normal (nx, ny, nz)
            nx = x * lengthInv;
            ny = y * lengthInv;
            nz = z * lengthInv;
            normals[vertexPos] = nx;
            normals[vertexPos + 1] = ny;
            normals[vertexPos + 2] = nz;

            // vertex tex coord (s, t) range between [0, 1]
            s = 1.0 - j / sectors;
            t = 1.0 - i / stacks;
            st[indVertex + indVertex] = s;
            st[indVertex + indVertex + 1] = t;
            ++j;
            ++indVertex;
        }
    }

    var ind = 0;
    const indices = new Array(stacks * sectors * 6 - 2 * sectors * 3);

    // generate CCW index list of sphere triangles
    // k1--k1+1
    // |  / |
    // | /  |
    // k2--k2+1
    var k1;
    var k2;
    for (var i = 0; i < stacks; ++i) {
        k1 = i * (sectors + 1); // beginning of current stack
        k2 = k1 + sectors + 1; // beginning of next stack
        var j = 0;

        while (j < sectors) {

            // 2 triangles per sector excluding first and last stacks
            // k1 => k2 => k1+1
            if (i != 0) {
                indices[ind] = k1;
                indices[ind + 1] = k2;
                indices[ind + 2] = k1 + 1;
                ind += 3;
            }

            // k1+1 => k2 => k2+1
            if (i != stacks - 1) {
                indices[ind] = k1 + 1;
                indices[ind + 1] = k2;
                indices[ind + 2] = k2 + 1;
                ind += 3;
            }
            ++j;
            ++k1;
            ++k2;
        }
    }

    builder.coordinates(coords)
        .colorsRGB(colors)
        .normals(normals)
        .textureCoordinates(st)
        .indices(indices)
        .name(name);

    return builder.build();
}

Models.cube = function(gl, r, g, b, name) {
    const pos = [-1.0, -1.0, 1.0,
        1.0, -1.0, 1.0,
        1.0, 1.0, 1.0, -1.0, 1.0, 1.0,
        // Back face
        -1.0, -1.0, -1.0, -1.0, 1.0, -1.0,
        1.0, 1.0, -1.0,
        1.0, -1.0, -1.0,
        // Top face
        -1.0, 1.0, -1.0, -1.0, 1.0, 1.0,
        1.0, 1.0, 1.0,
        1.0, 1.0, -1.0,
        // Bottom face
        -1.0, -1.0, -1.0,
        1.0, -1.0, -1.0,
        1.0, -1.0, 1.0, -1.0, -1.0, 1.0,
        // Right face
        1.0, -1.0, -1.0,
        1.0, 1.0, -1.0,
        1.0, 1.0, 1.0,
        1.0, -1.0, 1.0,
        // Left face
        -1.0, -1.0, -1.0, -1.0, -1.0, 1.0, -1.0, 1.0, 1.0, -1.0, 1.0, -1.0
    ];


    const texPos = [
        // Front
        0.0, 0.0, //
        1.0, 0.0, //
        1.0, 1.0, //
        0.0, 1.0, //
        // Back
        0.0, 0.0, //
        1.0, 0.0, //
        1.0, 1.0, //
        0.0, 1.0, //
        // Right
        0.0, 0.0, //
        1.0, 0.0, //
        1.0, 1.0, //
        0.0, 1.0, //
        // Left
        0.0, 0.0, //
        1.0, 0.0, //
        1.0, 1.0, //
        0.0, 1.0, //
        // Top
        0.0, 0.0, //
        1.0, 0.0, //
        1.0, 1.0, //
        0.0, 1.0, //
        // Bottom
        0.0, 0.0, //
        1.0, 0.0, //
        1.0, 1.0, //
        0.0, 1.0
    ];

    const indices = [
        0, 1, 2, /*        */ 0, 2, 3, // front
        4, 5, 6, /*        */ 4, 6, 7, // back
        8, 9, 10, /*       */ 8, 10, 11, // top
        12, 13, 14, /*     */ 12, 14, 15, // bottom
        16, 17, 18, /*     */ 16, 18, 19, // right
        20, 21, 22, /*     */ 20, 22, 23
    ];

    const color = new Array(pos.length);
    var i = 0;

    while (i < color.length) {
        color[i] = r;
        color[i + 1] = g;
        color[i + 2] = b;
        i += 3;
    }

    const normals = [
        0.0, 0.0, 1.0, //
        0.0, 0.0, 1.0, //
        0.0, 0.0, 1.0, //
        0.0, 0.0, 1.0, //
        // Back
        0.0, 0.0, -1.0, //
        0.0, 0.0, -1.0, //
        0.0, 0.0, -1.0, //
        0.0, 0.0, -1.0, //
        // Top
        0.0, 1.0, 0.0, //
        0.0, 1.0, 0.0, //
        0.0, 1.0, 0.0, //
        0.0, 1.0, 0.0, //
        // Bottom
        0.0, -1.0, 0.0, //
        0.0, -1.0, 0.0, //
        0.0, -1.0, 0.0, //
        0.0, -1.0, 0.0, //
        // Right
        1.0, 0.0, 0.0, //
        1.0, 0.0, 0.0, //
        1.0, 0.0, 0.0, //
        1.0, 0.0, 0.0, //
        // Left
        -1.0, 0.0, 0.0, //
        -1.0, 0.0, 0.0, //
        -1.0, 0.0, 0.0, //
        -1.0, 0.0, 0.0 //
    ];

    return new ModelBuilder()
        .coordinates(pos)
        .textureCoordinates(texPos)
        .indices(indices)
        .colorsRGB(color)
        .normals(normals)
        .name(name)
        .build();

}