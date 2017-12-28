package com.github.raulccabreu.redukt.ui

import android.support.v4.app.Fragment
import com.github.raulccabreu.redukt.Redukt

abstract class ReactiveFragment<T> : Fragment(), StateListenerLayout<T> {

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