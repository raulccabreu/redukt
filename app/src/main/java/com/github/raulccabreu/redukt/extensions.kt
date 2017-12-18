package com.github.raulccabreu.redukt

import android.os.Build
import android.util.Log
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


fun <T> Collection<T>.parallelFor(call: (T) -> Unit) {
    if (this.isEmpty()) return

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        this.parallelStream().forEach { call(it) }
    } else {
        val numThreads = Runtime.getRuntime().availableProcessors()
        val exec = Executors.newFixedThreadPool(numThreads)
        for (item in this) {
            exec.submit { call(item) }
        }
        exec.shutdown()
        exec.awaitTermination(1, TimeUnit.DAYS)
    }
}

fun debug(msg: String) {
    Log.d("Redukt", msg)
}
