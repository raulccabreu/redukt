package com.github.raulccabreu.redukt.ui

import android.content.Context
import android.view.View
import com.github.raulccabreu.redukt.Redukt
import com.github.raulccabreu.redukt.states.StateListener

abstract class StateView<T>(context: Context) : View(context), StateListener<T> {

    private var isRegistered = false

    protected abstract fun changeStateWhenHidden(): Boolean
    protected abstract fun getRedukt(): Redukt<T>

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        registerReducer()
    }

    override fun onDetachedFromWindow() {
        unregisterReducer()
        super.onDetachedFromWindow()
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        if (visibility == View.VISIBLE && !isRegistered)
            registerReducer()
        else if (visibility != View.VISIBLE && !changeStateWhenHidden())
            unregisterReducer()
    }

    private fun registerReducer() {
        if (isRegistered) return

        isRegistered = true
        getRedukt().listeners.add(this)
        onChanged(getRedukt().state)
    }

    private fun unregisterReducer() {
        getRedukt().listeners.remove(this)
        isRegistered = false
    }

    override fun hasChanged(newState: T, oldState: T): Boolean = newState != oldState

}