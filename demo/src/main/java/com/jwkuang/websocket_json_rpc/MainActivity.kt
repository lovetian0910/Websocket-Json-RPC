package com.jwkuang.websocket_json_rpc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.jwkuang.wjsonrpc.JsonRPC
import kotlinx.coroutines.*
import java.net.URI

class MainActivity : AppCompatActivity() {
    companion object{
        const val TAG = "WJsonRPC"
    }
    private val scope = CoroutineScope(Dispatchers.IO)
    private val rpc = JsonRPC(URI("ws://192.168.31.181:8080/echo"))
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun clickWebsocket(view: View) {
        if(!rpc.isOpen) {
            rpc.start()
        }else {
            scope.launch {
                val response = rpc.callServerMethod("test", "test_params")
                log("Response success: ${response.success}, Error: ${response.error}, result: ${response.result}")
            }
        }

    }

    fun clickNotification(view: View) {
        if(rpc.isOpen) {
            rpc.notification("notification", "no_params")
        }
    }

    private fun log(msg: String) {
        println("[${Thread.currentThread().name}] $msg")
    }

}

