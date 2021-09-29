package com.jwkuang.websocket_json_rpc

import android.app.Application

/**
 * Created on 9/17/21
 * @author jwkuang
 */
class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        System.setProperty("kotlinx.coroutines.debug", "on")
    }
}