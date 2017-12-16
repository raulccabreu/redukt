package com.github.raulccabreu.redukt.middlewares

import com.github.raulccabreu.redukt.actions.Action
import com.github.raulccabreu.redukt.debug
import com.github.raulccabreu.redukt.states.StateListener

class DebugMiddleware<T> : Middleware<T>, StateListener<T> {
    override fun before(state: T, action: Action<*>) {
        debug("<Redukt> has start [${action.name}] with [${action.payload}]")
    }

    override fun after(state: T, action: Action<*>) {
        //Do nothing
    }

    override fun onChanged(state: T) {
        debug("<Redukt> new state is [$state]")
    }

    override fun hasChanged(newState: T, oldState: T): Boolean {
        return if (newState == oldState) {
            debug("<Redukt> state wasn't changed!")
            false
        } else {
            debug("<Redukt> state was changed ...")
            true
        }
    }
}