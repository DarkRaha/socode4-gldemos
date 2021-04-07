package com.darkraha.opengldemoj.gl.modelling;

import org.lwjgl.opengl.GL33;

public class Models {

    public static Model quad(float width, float height, String name) {
        Model.Builder builder = new Model.Builder();
        float coordx = width / 2.0f;
        float coordy = height / 2.0f;


        builder.drawType(GL33.GL_TRIANGLE_STRIP)
                .coordinates2d(new float[]{
                        -coordx, coordy,
                        coordx, coordy,
                        -coordx, -coordy,
                        coordx, -coordy,
                })
                .colorsRGB(new float[]{
                        1.0f, 0.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 0.0f, 1.0f,
                        1.0f, 1.0f, 1.0f
                })
                .textureCoordinates(new float[]{
                        0.0f, 0.0f,
                        1.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f
                })
                .normals(new float[]{
                        0f, 0f, 1f, //
                        0f, 0f, 1f, //
                        0f, 0f, 1f, //
                        0f, 0f, 1f //
                })

                .name(name);


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
    public static Model sphere(float radius, int sectors, int stacks, float r, float g, float b, String name) {
        Model.Builder builder = new Model.Builder();
        float pi = (float) Math.PI;
        float x, y, z, xy;                              // vertex position
        float nx, ny, nz, lengthInv = 1.0f / radius;    // vertex normal
        float s, t;                                     // vertex texture coordinates

        float sectorStep = 2 * pi / sectors;
        float stackStep = pi / stacks;
        float sectorAngle, stackAngle;

        int indVertex = 0;

        int vertexCount = (stacks + 1) * (sectors + 1);
        float[] coords = new float[vertexCount * 3];
        float[] colors = new float[vertexCount * 3];
        float[] normals = new float[vertexCount * 3];
        float[] st = new float[vertexCount * 2];

        for (int i = 0; i <= stacks; ++i) {
            stackAngle = pi / 2f - i * stackStep;        // starting from pi/2 to -pi/2
            xy = radius * (float) Math.cos(stackAngle);   // r * cos(u)
            z = radius * (float) Math.sin(stackAngle);    // r * sin(u)

            // add (sectorCount+1) vertices per stack
            // the first and last vertices have same position and normal, but different tex coords
            for (int j = 0; j <= sectors; ++j, ++indVertex) {
                sectorAngle = j * sectorStep;           // starting from 0 to 2pi

                // vertex position (x, y, z)
                x = xy * (float) Math.cos(sectorAngle);  // r * cos(u) * cos(v)
                y = xy * (float) Math.sin(sectorAngle);  // r * cos(u) * sin(v)

                int vertexPos = indVertex * 3;
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
                s = 1f - (float) j / sectors;
                t = 1f - (float) i / stacks;
                st[indVertex + indVertex] = s;
                st[indVertex + indVertex + 1] = t;
            }
        }


        int ind = 0;
        int[] indices = new int[stacks * sectors * 6 - 2 * sectors * 3];

        // generate CCW index list of sphere triangles
        // k1--k1+1
        // |  / |
        // | /  |
        // k2--k2+1
        int k1, k2;
        for (int i = 0; i < stacks; ++i) {
            k1 = i * (sectors + 1);     // beginning of current stack
            k2 = k1 + sectors + 1;      // beginning of next stack

            for (int j = 0; j < sectors; ++j, ++k1, ++k2) {

                // 2 triangles per sector excluding first and last stacks
                // k1 => k2 => k1+1
                if (i != 0) {
                    indices[ind] = k1;
                    indices[ind + 1] = k2;
                    indices[ind + 2] = k1 + 1;
                    ind += 3;
                }

                // k1+1 => k2 => k2+1
                if (i != (stacks - 1)) {
                    indices[ind] = k1 + 1;
                    indices[ind + 1] = k2;
                    indices[ind + 2] = k2 + 1;
                    ind += 3;
                }
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


    public static Model cube(float r, float g, float b, String name) {

        float[] pos = new float[]{
                // Front face
                -1.0f, -1.0f, 1.0f,
                1.0f, -1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                -1.0f, 1.0f, 1.0f,

                // Back face
                -1.0f, -1.0f, -1.0f,
                -1.0f, 1.0f, -1.0f,
                1.0f, 1.0f, -1.0f,
                1.0f, -1.0f, -1.0f,

                // Top face
                -1.0f, 1.0f, -1.0f,
                -1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, -1.0f,

                // Bottom face
                -1.0f, -1.0f, -1.0f,
                1.0f, -1.0f, -1.0f,
                1.0f, -1.0f, 1.0f,
                -1.0f, -1.0f, 1.0f,

                // Right face
                1.0f, -1.0f, -1.0f,
                1.0f, 1.0f, -1.0f,
                1.0f, 1.0f, 1.0f,
                1.0f, -1.0f, 1.0f,

                // Left face
                -1.0f, -1.0f, -1.0f,
                -1.0f, -1.0f, 1.0f,
                -1.0f, 1.0f, 1.0f,
                -1.0f, 1.0f, -1.0f};


        float[] texPos = new float[]{
                // Front
                0.0f, 0.0f, //
                1.0f, 0.0f, //
                1.0f, 1.0f, //
                0.0f, 1.0f, //
                // Back
                0.0f, 0.0f, //
                1.0f, 0.0f, //
                1.0f, 1.0f, //
                0.0f, 1.0f, //
                // Right
                0.0f, 0.0f, //
                1.0f, 0.0f, //
                1.0f, 1.0f, //
                0.0f, 1.0f, //
                // Left
                0.0f, 0.0f, //
                1.0f, 0.0f, //
                1.0f, 1.0f, //
                0.0f, 1.0f, //
                // Top
                0.0f, 0.0f, //
                1.0f, 0.0f, //
                1.0f, 1.0f, //
                0.0f, 1.0f, //
                // Bottom
                0.0f, 0.0f, //
                1.0f, 0.0f, //
                1.0f, 1.0f, //
                0.0f, 1.0f, //
        };


        int[] indices = new int[]{
                0, 1, 2,/*        */ 0, 2, 3,    // front
                4, 5, 6,/*        */ 4, 6, 7,    // back
                8, 9, 10,/*       */8, 10, 11,   // top
                12, 13, 14,/*     */12, 14, 15,   // bottom
                16, 17, 18,/*     */16, 18, 19,   // right
                20, 21, 22,/*     */20, 22, 23,   // left
        };

        float[] color = new float[pos.length];
        for (int i = 0; i < color.length; i += 3) {
            color[i] = r;
            color[i + 1] = g;
            color[i + 2] = b;
        }


        float []normals = new float[] {
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
        };

        return new Model.Builder()
                .coordinates(pos)
                .textureCoordinates(texPos)
                .indices(indices)
                .colorsRGB(color)
                .normals(normals)
                .name(name)
                .build();
    }


}
