package com.github.raulccabreu.redukt.states

interface  StateListener<in T : Any> {
    fun hasChanged(newState: T, oldState: T): Boolean

    fun onChanged(state: T)
}