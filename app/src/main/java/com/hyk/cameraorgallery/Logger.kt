package com.hyk.cameraorgallery

import android.util.Log
import androidx.databinding.ktx.BuildConfig

private const val TAG = "test"

object Logger {
    /** Log Level Error  */
    fun e(vararg message: Any?) {
        if (BuildConfig.DEBUG) Log.e(TAG, buildLogMsg(*message))
    }

    /** Log Level Warning  */
    fun w(vararg message: Any?) {
        if (BuildConfig.DEBUG) Log.w(TAG, buildLogMsg(*message))
    }

    /** Log Level Information  */
    fun i(vararg message: Any?) {
        if (BuildConfig.DEBUG) Log.i(TAG, buildLogMsg(*message))
    }

    /** Log Level Debug  */
    fun d(vararg message: Any?) {
        if (BuildConfig.DEBUG) Log.d(TAG, buildLogMsg(*message))
    }

    /** Log Level Verbose  */
    fun v(vararg message: Any?) {
        if (BuildConfig.DEBUG) Log.v(TAG, buildLogMsg(*message))
    }

    private fun buildLogMsg(vararg message: Any?): String {
        val ste = Thread.currentThread().stackTrace[4]
        val sb = StringBuilder()
        if (ste.fileName != null) {
            sb.append(
                ste.fileName./*replace(".java", String.empty).*/replace(
                    Regex(".kt"),
                    ""
                )
            )
        }
        sb.append("_")
        sb.append(ste.methodName)
        sb.append("> ")
        for (obj in message) {
            if (ste.fileName == "MainActivity.kt")
                if (obj.toString().startsWith("isSignedIn")) {
                    d("* == start ============================================================= *")
                }
            sb.append(obj/*.toString()*/)
        }
        return sb.toString()
    }
}