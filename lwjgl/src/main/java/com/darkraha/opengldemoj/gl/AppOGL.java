package com.darkraha.opengldemoj.gl;

import com.darkraha.opengldemoj.renders.Render;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.Platform;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class AppOGL {
    public static boolean isV120 = false;

    protected long window = 0L;
    protected Render render;
    protected int width = 0;
    protected int height = 0;


    public AppOGL(int w, int h) {
        render = new Render();
        init(w, h);
    }

    public AppOGL(Render r, int w, int h) {
        render = r;
        init(w, h);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setRenderer(Render r) {
        Render old = render;

        if (r == null) {
            r = new Render();
        }

        r.onSetup(this);

        render = r;
        old.onDispose(this);
    }


    protected void init(int w, int h) {
        if (window != 0L) {
            throw new IllegalStateException("GLFW may be already initialized");
        }

        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        createWindow(w, h);
        setupCallbacks();
        setupGL();
    }

    protected void setupGL() {

        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(window, pWidth, pHeight);

            width = pWidth.get(0);
            height = pHeight.get(0);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(window, (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );

            glfwMakeContextCurrent(window);
            // Enable v-sync
            glfwSwapInterval(1);
            // Make the window visible
            glfwShowWindow(window);

            System.out.println("Version: " + glfwGetVersionString());

            loop();
        }
    }


    protected void createWindow(int w, int h) {
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        if(isV120){
//            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2);
//            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
            glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_FALSE);


        }else {
            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

            if (Platform.get() == Platform.MACOSX) {
                glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
            }
        }

        window = glfwCreateWindow(w, h, "Hello OpenGL!", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }
    }

    protected void setupCallbacks() {

        glfwSetKeyCallback(
                window, (window, key, scancode, action, keyMods) -> {
                    if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                        glfwSetWindowShouldClose(window, true);
                    }
                }
        );

        glfwSetWindowSizeCallback(window, (win, w, h) -> {
            width = w;
            height = h;
            render.onSurfaceChanged(AppOGL.this, w, h);
        });

        glfwSetCursorPosCallback(window, (win, xpos, ypos) -> {
        });

        glfwSetMouseButtonCallback(window, (win, button, action, keyMods) -> {
        });

        glfwSetScrollCallback(window, (win, xoffset, yoffset) -> {
        });
    }

    protected void dispose() {
        render.onDispose(this);

        if (window != 0L) {
            Callbacks.glfwFreeCallbacks(window);
            glfwDestroyWindow(window);
            glfwTerminate();
            glfwSetErrorCallback(null).free();
            window = 0;
        }
    }

    protected void loop() {
        createCapabilities(false);
        render.onSetup(this);

        while (!glfwWindowShouldClose(window)) {
            render.onDrawFrame(this);
            glfwSwapBuffers(window); // swap the color buffers
            glfwPollEvents();
        }
        dispose();
    }
}
