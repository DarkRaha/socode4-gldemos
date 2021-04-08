package com.darkraha.gldemos

import android.app.Application
import com.darkraha.gldemos.gl.GlCommon

class GlDemosApp :Application() {

    override fun onCreate() {
        GlCommon.appContext = this;
        super.onCreate()
    }
}