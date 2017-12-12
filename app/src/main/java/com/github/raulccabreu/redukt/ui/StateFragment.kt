package com.github.raulccabreu.redukt.ui

import android.support.v4.app.Fragment
import com.github.raulccabreu.redukt.Redukt
import com.github.raulccabreu.redukt.states.StateListener

abstract class StateFragment<T> : Fragment(), StateListener<T> {

    protected abstract fun getRedukt(): Redukt<T>

    override fun onStart() {
        super.onStart()

        getRedukt().listeners.add(this)
        onChanged(getRedukt().state)
    }

    override fun onStop() {
        getRedukt().listeners.remove(this)
        super.onStop()
    }

    override fun hasChanged(newState: T, oldState: T): Boolean = newState != oldState
}