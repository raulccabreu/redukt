package com.github.raulccabreu.redukt.ui

import android.content.Context
import android.view.View

abstract class ReactiveView<T>(context: Context) : View(context), LayoutStateListener<T> {

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        registerStateListener()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        unregisterStateListener()
    }
}