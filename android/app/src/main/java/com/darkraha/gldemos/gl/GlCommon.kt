package com.darkraha.gldemos.gl

import android.content.Context

object GlCommon {
   lateinit var appContext: Context

   val idArray = intArrayOf(0)

    @JvmStatic
    val PI_F = Math.PI.toFloat()

    @JvmStatic
    val TO_RAD = PI_F / 180.0f

    @JvmStatic
    val ALNGLE45 = Math.toRadians(45.0).toFloat()
}