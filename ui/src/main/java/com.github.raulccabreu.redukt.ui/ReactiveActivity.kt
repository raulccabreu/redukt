package com.github.raulccabreu.redukt.ui

import android.support.v7.app.AppCompatActivity
import com.github.raulccabreu.redukt.Redukt

abstract class ReactiveActivity<T> : AppCompatActivity(), StateListenerLayout<T> {

    protected abstract fun getRedukt(): Redukt<T>

    override fun onStart() {
        super.onStart()
        start(getRedukt())
    }

    override fun onStop() {
        super.onStop()
        stop(getRedukt())
    }
}