package com.darkraha.opengldemoj.gl.modelling;

import com.darkraha.opengldemoj.gl.shader.ShaderProgramBuilder;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * Contains the vertex data of model in different arrays.
 */
public class Model {
    public String name;
    public float[] vPos;
    public float[] vColor;
    public float[] vTexPos;
    public float[] vNormal;
    public float[] vTangent;
    public float[] vBitangent;
    public int[] indices;
    public float[] color;
    public int posComponents;
    public int colorComponents;
    public int texPosComponents;
    public int normalComponents;
    public int drawType = GL33.GL_TRIANGLES;

    private Model() {

    }


    public GlModel toGlModel() {
        int[] ids = new int[6];

        int idVao = glGenVertexArrays();
        glBindVertexArray(idVao);
        ids[0] = createVBO(vPos, ShaderProgramBuilder.A_LOCATION_VERTEX_POS, posComponents);
        ids[1] = vColor != null ? createVBO(vColor, ShaderProgramBuilder.A_LOCATION_VERTEX_COLOR, colorComponents) : 0;
        ids[2] = vNormal != null ? createVBO(vNormal, ShaderProgramBuilder.A_LOCATION_VERTEX_NORMAL, normalComponents) : 0;
        ids[3] = vTexPos != null ? createVBO(vTexPos, ShaderProgramBuilder.A_LOCATION_VERTEX_TEXPOS, texPosComponents) : 0;
        ids[4] = vTangent != null ? createVBO(vTangent, ShaderProgramBuilder.A_LOCATION_VERTEX_TANGENT,
                3) : 0;
        ids[5] = vBitangent != null ? createVBO(vBitangent, ShaderProgramBuilder.A_LOCATION_VERTEX_BITANGENT,
                3) : 0;

        int idIbo = indices != null ? createIBO(indices) : 0;

        int count = vPos.length / posComponents;

        if (indices != null) {
            count = indices.length;
        }

        glBindVertexArray(0);
        return new GlModel(idVao, ids, idIbo, drawType, count, name);
    }

    public Model calcTangent(){
        vTangent = new float[vPos.length];
        calcTangent(vPos,vTexPos,indices, vTangent,null);
        return this;
    }

    public Model calcTangentBitangent(){
        vTangent = new float[vPos.length];
        vBitangent = new float[vPos.length];
        calcTangent(vPos,vTexPos,indices, vTangent,vBitangent);
        return this;
    }



    /**
     * Calculates tangents/bitangents for bumping.
     *
     * @param vPos       array of the vertex positions (x,y,z)
     * @param vTexPos    array of the vertex texture positions (s,t)
     * @param indices    indices of vertices to draw model with triangles
     * @param vTangent   array to store calculated tangent vectors (x,y,z)
     * @param vBitangent array to store calculated tangent vectors (x,y,z)
     */
    public static void calcTangent(float[] vPos, float[] vTexPos,
                                   int[] indices,
                                   float[] vTangent, float[] vBitangent) {

        int posComponents = 3;
        int texPosComponets = 2;
        int offset;

        if (indices == null) {
            indices = new int[vPos.length / posComponents];
            for (int i = 0; i < indices.length; i++) {
                indices[i] = i;
            }
        }

        // coordinates of points of triangle
        Vector3f p0 = new Vector3f();
        Vector3f p1 = new Vector3f();
        Vector3f p2 = new Vector3f();

        // texture coordinates of points of triangle
        Vector2f pTex0 = new Vector2f();
        Vector2f pTex1 = new Vector2f();
        Vector2f pTex2 = new Vector2f();

        // for storing calculated edges (p1,p0) and (p2,p0)
        Vector3f deltaP10 = new Vector3f();
        Vector3f deltaP20 = new Vector3f();

        // for storing calculated edges
        // in texture coordinates (pTex1, pTex0) and (pTex2,pTex0)
        Vector2f deltaTex10 = new Vector2f();
        Vector2f deltaTex20 = new Vector2f();

        //
        Vector3f tmp1 = new Vector3f();
        Vector3f tmp2 = new Vector3f();

        final Vector3f tangent = new Vector3f();
        final Vector3f bitangent = new Vector3f();


        for (int i = 0; i < indices.length; i += 3) {

            offset = indices[i] * posComponents;
            p0.set(vPos[offset], vPos[offset + 1], vPos[offset + 2]);
            offset = indices[i + 1] * posComponents;
            p1.set(vPos[offset], vPos[offset + 1], vPos[offset + 2]);
            offset = indices[i + 2] * posComponents;
            p2.set(vPos[offset], vPos[offset + 1], vPos[offset + 2]);

            offset = indices[i] * texPosComponets;
            pTex0.set(vTexPos[offset], vTexPos[offset + 1]);
            offset = indices[i + 1] * texPosComponets;
            pTex1.set(vTexPos[offset], vTexPos[offset + 1]);
            offset = indices[i + 2] * texPosComponets;
            pTex2.set(vTexPos[offset], vTexPos[offset + 1]);


            // calc edges
            p1.sub(p0, deltaP10);
            p2.sub(p0, deltaP20);
            pTex1.sub(pTex0, deltaTex10);
            pTex2.sub(pTex0, deltaTex20);

            float r = 1.0f / (deltaTex10.x * deltaTex20.y - deltaTex10.y * deltaTex20.x);

            // tangent = (deltaP10 * deltaTex20.y   - deltaP20 * deltaTex10.y)*r;
            tmp1.set(deltaP10).mul(deltaTex20.y)
                    .sub(tmp2.set(deltaP20).mul(deltaTex10.y), tangent);
            tangent.mul(r);

            // add same tangent for each points of triangle
            // if point already had tangent we will
            // average tangent from old value and new value
            offset = indices[i] * posComponents;
            vTangent[offset] += tangent.x;
            vTangent[offset + 1] += tangent.y;
            vTangent[offset + 2] += tangent.z;

            offset = indices[i + 1] * posComponents;
            vTangent[offset] += tangent.x;
            vTangent[offset + 1] += tangent.y;
            vTangent[offset + 2] += tangent.z;

            offset = indices[i + 2] * posComponents;
            vTangent[offset] += tangent.x;
            vTangent[offset + 1] += tangent.y;
            vTangent[offset + 2] += tangent.z;


            if (vBitangent != null) {
                // bitangent = (deltaP20 * deltaTex10.x   - deltaP10 * deltaTex20.x)*r;
                tmp1.set(deltaP20).mul(deltaTex10.x)
                        .sub(tmp2.set(deltaP10).mul(deltaTex20.x), bitangent);
                bitangent.mul(r);

                // add same bitangent for each points of triangle
                // if point already had bitangent we will
                // average bitangent from old value and new value
                offset = indices[i] * posComponents;
                vBitangent[offset] += bitangent.x;
                vBitangent[offset + 1] += bitangent.y;
                vBitangent[offset + 2] += bitangent.z;

                offset = indices[i + 1] * posComponents;
                vBitangent[offset] += bitangent.x;
                vBitangent[offset + 1] += bitangent.y;
                vBitangent[offset + 2] += bitangent.z;

                offset = indices[i + 2] * posComponents;
                vBitangent[offset] += bitangent.x;
                vBitangent[offset + 1] += bitangent.y;
                vBitangent[offset + 2] += bitangent.z;
            }
        }

        normalize3(vTangent);
        if (vBitangent != null) {
            normalize3(vBitangent);
        }
    }

    public static void normalize3(float[] src) {
        Vector3f v = new Vector3f();
        for (int i = 0; i < src.length; i += 3) {
            v.set(src[i], src[i + 1], src[i + 2]);
            v.normalize();
            src[i] = v.x;
            src[i + 1] = v.y;
            src[i + 2] = v.z;
        }
    }

    //----------------------------------------------------------------------------
    //
    public static int createVBO(float[] data, int locations, int size) {

        try (MemoryStack stack = MemoryStack.stackPush()) {
            int idVbo = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, idVbo);

            FloatBuffer fb = stack.mallocFloat(data.length);
            fb.put(data).flip();
            glBufferData(GL_ARRAY_BUFFER, fb, GL_STATIC_DRAW);

            glEnableVertexAttribArray(locations);
            glVertexAttribPointer(locations,
                    size, GL_FLOAT, false,
                    0, 0);

            return idVbo;
        }
    }


    public static int createIBO(byte[] data) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer byteBuffer = stack.malloc(data.length);
            byteBuffer.put(data).flip();
            int idIbo = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idIbo);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, byteBuffer, GL_STATIC_DRAW);
            return idIbo;
        }
    }

    public static int createIBO(int[] data) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer byteBuffer = stack.malloc(data.length * 4).asIntBuffer();
            byteBuffer.put(data).flip();
            int idIbo = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idIbo);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, byteBuffer, GL_STATIC_DRAW);
            return idIbo;
        }
    }

    //----------------------------------------------------------------------------
    //
    public static class Builder {
        private final Model model = new Model();

        public Builder coordinates(float[] coords) {
            model.vPos = coords;
            model.posComponents = 3;
            return this;
        }

        public Builder coordinates2d(float[] coords) {
            model.vPos = coords;
            model.posComponents = 2;
            return this;
        }

        public Builder colorsRGB(float[] colorsRGB) {
            model.vColor = colorsRGB;
            model.colorComponents = 3;
            return this;
        }

        public Builder textureCoordinates(float[] st) {
            model.vTexPos = st;
            model.texPosComponents = 2;
            return this;
        }

        public Builder textureCoordinates3(float[] st) {
            model.vTexPos = st;
            model.texPosComponents = 3;
            return this;
        }

        public Builder textureCoordinates4(float[] st) {
            model.vTexPos = st;
            model.texPosComponents = 4;
            return this;
        }

        public Builder normals(float[] n) {
            model.vNormal = n;
            model.normalComponents = 3;
            return this;
        }

        public Builder indices(int[] i) {
            model.indices = i;
            return this;
        }


        public Builder solidColor(float r, float g, float b, float a) {
            model.color = new float[]{r, g, b, a};
            return this;
        }

        public Builder name(String n) {
            model.name = n;
            return this;
        }


        /**
         * @param t default GL_TRIANGLES
         * @return
         */
        public Builder drawType(int t) {
            model.drawType = t;
            return this;
        }


        public Model build() {
            return model;
        }
    }

}
