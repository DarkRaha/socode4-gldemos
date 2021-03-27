package com.darkraha.opengldemokt.gl


import com.darkraha.opengldemokt.renders.Render
import org.lwjgl.glfw.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil.NULL
import org.lwjgl.system.Platform

open class AppOGL {

    companion object {
        @JvmStatic
        var isV120 = false
    }

    protected var window = 0L
    protected var render: Render
    var width = 0
        protected set
    var height = 0
        protected set


    constructor(w: Int, h: Int) {
        render = Render()
        init(w, h)
    }

    constructor(r: Render, w: Int, h: Int) {
        render = r
        init(w, h)
    }

    fun setRenderer(r: Render?) {
        val old = render

        (r ?: Render()).also {
            it.onSetup(this)
            render = it
            old.onDispose(this)
        }
    }

    protected fun init(w: Int, h: Int) {
        check(window == 0L) { "GLFW may be already initialized" }
        GLFWErrorCallback.createPrint(System.err).set()

        check(glfwInit()) { "Unable to initialize GLFW" }

        createWindow(w, h)
        setupCallbacks()
        setupGL()
    }

    fun setupGL() {
        MemoryStack.stackPush().use { stack ->
            val pWidth = stack.mallocInt(1)
            val pHeight = stack.mallocInt(1)
            glfwGetWindowSize(window, pWidth, pHeight)
            width = pWidth[0]
            height = pHeight[0]

            // Get the resolution of the primary monitor
            val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor())

            // Center the window
            glfwSetWindowPos(
                window, (vidmode!!.width() - pWidth[0]) / 2,
                (vidmode.height() - pHeight[0]) / 2
            )
            glfwMakeContextCurrent(window)
            // Enable v-sync
            glfwSwapInterval(1)

            // Make the window visible
            glfwShowWindow(window)

            println("Version: " + glfwGetVersionString())
            loop()
        }
    }


    open fun createWindow(w: Int, h: Int) {
        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)

        if (!isV120) {
            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3)
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)

            if (Platform.get() === Platform.MACOSX) {
                glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE)
            }
        }

        window = glfwCreateWindow(w, h, "Hello OpenGL!", NULL, NULL)
        if (window == NULL) {
            throw RuntimeException("Failed to create the GLFW window")
        }
    }

    open fun setupCallbacks() {

        glfwSetKeyCallback(
            window
        ) { window: Long, key: Int, scancode: Int, action: Int, keyMods: Int ->

            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true)
            }
        }

        glfwSetWindowSizeCallback(window) { win, w, h ->
            width = w
            height = h
            render.onSurfaceChanged(this@AppOGL, w, h)
        }

        glfwSetCursorPosCallback(window) { win, xpos, ypos ->

        }

        glfwSetMouseButtonCallback(window) { win, button, action, keyMods ->

        }

        glfwSetScrollCallback(window) { win, xoffset, yoffset ->

        }
    }

    open fun dispose() {
        render.onDispose(this)
        if (window != 0L) {
            Callbacks.glfwFreeCallbacks(window)
            glfwDestroyWindow(window)
            glfwTerminate()
            glfwSetErrorCallback(null)!!.free()
            window = 0
        }
    }

    protected fun loop() {
        GL.createCapabilities(false)
        render.onSetup(this)
        while (!glfwWindowShouldClose(window)) {
            render.onDrawFrame(this)
            glfwSwapBuffers(window) // swap the color buffers
            glfwPollEvents()
        }
        dispose()
    }
}
