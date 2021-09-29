package com.jwkuang.websocket_json_rpc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    companion object{
        const val TAG = "WJsonRPC"
    }
    private var continuation: ContinuationWrapper? = null
    private val scope = CoroutineScope(Dispatchers.IO)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    @InternalCoroutinesApi
    fun clickContinue(view: View) {
        if(continuation?.continuation?.isActive == true) {
            continuation?.continuation?.resume(true, null)
        }
    }

    fun clickStart(view: View) {
        val continuation = ContinuationWrapper()
        this.continuation = continuation
        scope.launch {
            val ret = startWithTimeOut(continuation)
            log("clickStart ret = $ret")
        }

    }

    fun clickStartNoContinue(view: View) {
        scope.launch {
            startCoroutine(ContinuationWrapper())
        }
    }


    private suspend fun startWithTimeOut(continuation: ContinuationWrapper): Boolean {
        var result: Boolean
        try{
            withTimeout(3000) {
                log("start timeout")
                result = startCoroutine(continuation)
                log("coroutine result: $result")
            }
        }catch (e: Exception) {
            log("$e")
            return false
        }
        log("startWithTimeOut return")
        return result

    }
    private suspend fun startCoroutine(continuation: ContinuationWrapper) = suspendCancellableCoroutine<Boolean> {
        log("startCoroutine")
        continuation.continuation = it
    }

    private fun log(msg: String) {
        println("[${Thread.currentThread().name}] $msg")
    }

    data class ContinuationWrapper(var continuation: CancellableContinuation<Boolean>? = null)
}

