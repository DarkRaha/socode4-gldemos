package com.darkraha.gldemos

import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ConfigurationInfo
import android.widget.Toast
import com.darkraha.gldemos.renders.*


class MainActivity : AppCompatActivity() {

    private lateinit var glSurfaceView: GLSurfaceView
    private var render: Render? = null
    private val GL_VERSION_MAJOR = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!isOpenGlSupported(GL_VERSION_MAJOR)) {
            Toast.makeText(
                this, "OpenGl ES $GL_VERSION_MAJOR is not supported",
                Toast.LENGTH_LONG
            ).show()
            finish()
            return
        }

        // set view programmatically
        glSurfaceView = GLSurfaceView(this)
        glSurfaceView.setEGLContextClientVersion(3)
        render = TexturedCubeRender(this)
        //render = TexturedQuadRender(this)
        // render = QuadRender()
        // render = ColoredQuadRender()
        //render = ColoredCubeRender()
        glSurfaceView.setRenderer(render)
        setContentView(glSurfaceView)
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView.onPause()
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView.onResume()
    }

    private fun isOpenGlSupported(major: Int): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val configInfo: ConfigurationInfo = activityManager.deviceConfigurationInfo

        // The GLES version used by an application.
        // The upper order 16 bits of reqGlEsVersion represent the major version
        // and the lower order 16 bits the minor version.
        return if (configInfo.reqGlEsVersion != ConfigurationInfo.GL_ES_VERSION_UNDEFINED) {
            Toast.makeText(
                this, "Supported version OpenGl ${configInfo.reqGlEsVersion shr 16}",
                Toast.LENGTH_LONG
            ).show()
            (configInfo.reqGlEsVersion shr 16) >= major
        } else {
            1 >= major
        }
    }
}