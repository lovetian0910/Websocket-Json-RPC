package com.jwkuang.wjsonrpc

import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.lang.Exception
import java.net.URI

/**
 * Created on 9/14/21
 * @author jwkuang
 */
class JsonRPC(private val url: URI): WebSocketClient(url) {

    fun start() {
        connect()
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