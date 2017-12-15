package com.github.raulccabreu.redukt.middlewares

import com.github.raulccabreu.redukt.actions.Action
import com.github.raulccabreu.redukt.states.StateListener

/**
 * Created by raulcca on 12/15/17.
 */

class DebugMiddleware<T> : Middleware<T>, StateListener<T> {
    override fun before(state: T, action: Action<*>) {
        println("------------------------------------------------------------")
        println("<Redukt> has start [${action.name}] with [${action.payload}]")
    }

    override fun after(state: T, action: Action<*>) {
        //Do nothing
    }

    override fun onChanged(state: T) {
        println("<Redukt> new state is [$state]")
    }

    override fun hasChanged(newState: T, oldState: T): Boolean {
        return if (newState == oldState) {
            println("<Redukt> state wasn't changed!")
            false
        } else {
            println("<Redukt> state was changed ...")
            true
        }
    }
}