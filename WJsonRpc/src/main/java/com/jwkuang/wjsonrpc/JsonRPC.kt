package com.jwkuang.wjsonrpc

import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.net.URI
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created on 9/14/21
 * @author jwkuang
 */
class JsonRPC(private val url: URI): WebSocketClient(url) {
    private var requestId = AtomicInteger(1)
    fun start() {
        connect()
    }

    suspend fun callServerMethod(method: String) {

    }

    suspend fun callServerMethod(method: String, params: JSONObject) {

    }

    suspend fun callServerMethod(method: String, params: JSONArray) {

    }

    override fun onOpen(handshakedata: ServerHandshake?) {
        TODO("Not yet implemented")
    }

    override fun onMessage(message: String?) {
        TODO("Not yet implemented")
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onError(ex: Exception?) {
        TODO("Not yet implemented")
    }
}