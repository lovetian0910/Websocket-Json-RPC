package com.jwkuang.wjsonrpc

import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.lang.Exception
import java.net.URI
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created on 9/14/21
 * @author jwkuang
 */
class JsonRPC(private val url: URI) : WebSocketClient(url) {
    companion object {
        var CALL_SERVER_TIME_OUT: Long = 10 * 1000
        var MAX_RECONNECT_TIME = 5
    }
    private var requestId = AtomicInteger(1)
    private val requestMap = ConcurrentHashMap<Int, CancellableContinuation<Response>>()
    private val requestDisconnectCache = ConcurrentHashMap<Int, String>()
    private val notificationDisconnectCache = ConcurrentLinkedQueue<String>()
    private var needReconnect = true
    private val isReconnecting = AtomicBoolean(false)
    private val reconnectCount = AtomicInteger(0)
    fun start() {
        connect()
    }

    fun end() {
        needReconnect = false
        close()

    }

    suspend fun callServerMethod(method: String, params: Any?): Response {
        val response: Response
        val id = requestId.getAndAdd(1)
        try {
            withTimeout(CALL_SERVER_TIME_OUT) {
                response = doCallServerMethod(id, method, params)
            }
        } catch (e: Exception) {
            requestMap.remove(id)
            requestDisconnectCache.remove(id)
            return timeout()
        }
        return response
    }

    fun notification(method: String, params: Any?) {
        val notificationMsg = generateMsg(method, null, params)
        if(isOpen) {
            send(notificationMsg)
        }else {
            notificationDisconnectCache.add(notificationMsg)
            tryReconnect()
        }
    }

    override fun onOpen(handshakedata: ServerHandshake?) {
        reconnectCount.set(0)
    }

    override fun onMessage(message: String?) {

    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        if (needReconnect && reconnectCount.get() < MAX_RECONNECT_TIME) {

        } else {

        }
    }

    override fun onError(ex: Exception?) {

    }

    private fun tryReconnect() {
        if(!isReconnecting.get() && reconnectCount.get() < MAX_RECONNECT_TIME) {

        }
    }

    private suspend fun doCallServerMethod(id: Int, method: String, params: Any?) =
            suspendCancellableCoroutine<Response> {
                requestMap[id] = it
                val requestMsg = generateMsg(method, id, params)
                requestMsg?.let {msg ->
                    if (isOpen) {
                        send(msg)
                    }else {
                        requestDisconnectCache[id] = msg
                        tryReconnect()
                    }
                } ?: run {
                    it.resume(parseError(), null)
                }

            }

    private fun generateMsg(method: String, id: Int?, params: Any?): String? {
        val msgJson = JSONObject()
        msgJson.put("jsonrpc", "2.0")
        msgJson.put("method", method)
        msgJson.putOpt("id", id)
        msgJson.putOpt("params", params)
        return msgJson.toString()
    }

    private fun timeout(): Response {
        return Response(success = false,
                error = Error(ErrorCode.TIME_OUT, "Call Server timeout", null),
                result = null)
    }

    private fun parseError(): Response {
        return Response(success = false,
                error = Error(ErrorCode.PARSE_ERROR, "Parse error", null), result = null)
    }

    private fun closeResponse(): Response {
        return Response(success = false, error = Error(ErrorCode.CLOSE, "Websocket close", null), null)
    }

    data class Response(val success: Boolean, val error: Error?, val result: Any?)

    data class Error(val code: Int, val message: String, val data: Any?)

    object ErrorCode {
        const val TIME_OUT = -30000
        const val CLOSE = -30001
        const val INVALID_REQUEST = -32600
        const val PARSE_ERROR = -32700
    }
}