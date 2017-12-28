package com.github.raulccabreu.redukt.ui

import android.content.Context
import android.view.View
import com.github.raulccabreu.redukt.Redukt

abstract class ReactiveView<T>(context: Context) : View(context), StateListenerLayout<T> {

    protected abstract fun getRedukt(): Redukt<T>

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        start(getRedukt())
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stop(getRedukt())
    }
}