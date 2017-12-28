package com.github.raulccabreu.redukt.ui

import android.content.Context
import android.view.View

abstract class ReactiveView<T>(context: Context) : View(context), StateListenerLayout<T> {

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        registerListener()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        unregisterListener()
    }
}