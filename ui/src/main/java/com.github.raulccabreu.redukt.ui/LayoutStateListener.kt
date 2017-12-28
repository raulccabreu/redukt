package com.github.raulccabreu.redukt.ui

import com.github.raulccabreu.redukt.Redukt
import com.github.raulccabreu.redukt.states.StateListener

interface LayoutStateListener<T> : StateListener<T> {

    fun getRedukt(): Redukt<T>

    fun registerStateListener() {
        getRedukt().listeners.add(this)
        onChanged(getRedukt().state)
    }

    fun unregisterStateListener() {
        getRedukt().listeners.remove(this)
    }

    override fun hasChanged(newState: T, oldState: T): Boolean = newState != oldState
}