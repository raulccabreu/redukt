package com.github.raulccabreu.redukt.ui

import com.github.raulccabreu.redukt.Redukt
import com.github.raulccabreu.redukt.states.StateListener

interface StateListenerLayout<T> : StateListener<T> {

    fun start(redukt: Redukt<T>) {
        redukt.listeners.add(this)
        onChanged(redukt.state)
    }

    fun stop(redukt: Redukt<T>) {
        redukt.listeners.remove(this)
    }

    override fun hasChanged(newState: T, oldState: T): Boolean = newState != oldState
}