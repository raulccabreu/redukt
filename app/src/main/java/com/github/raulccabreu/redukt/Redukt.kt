package com.github.raulccabreu.redukt

import com.github.raulccabreu.redukt.actions.Action
import com.github.raulccabreu.redukt.actions.MiddlewareAction
import com.github.raulccabreu.redukt.middlewares.Middleware
import com.github.raulccabreu.redukt.reducers.Reducer
import com.github.raulccabreu.redukt.states.StateListener

class Redukt<T>(state: T) {
    var state = state
        private set
    val reducers = mutableSetOf<Reducer<T>>()
    val middlewares = mutableSetOf<Middleware<T>>()
    val listeners = mutableSetOf<StateListener<T>>()
    private val dispatcher = Dispatcher { reduce(it) }

    init { start() }

    fun dispatch(action: Action<*>, async: Boolean = true) {
        if (async) dispatcher.dispatch(action)
        else reduce(action)
    }

    private fun start() {
        dispatcher.start()
    }

    fun stop() {
        dispatcher.stop()
    }

    private fun reduce(action: Action<*>) {

        System.out.println("${action.name} reduce")

        reduceBefore(action)

        if (action is MiddlewareAction) return

        val oldState = state
        var tempState = state

        reducers.forEach {
            tempState = it.reduce(tempState, action)
            System.out.println("${action.name} reducer ${it::class.java.simpleName}")
        }

        state = tempState

        reduceListener(action, oldState)
        reduceAfter(action)
    }

    private fun reduceBefore(action: Action<*>) {
        middlewares.parallelFor {
            it.before(state, action)
            System.out.println("${action.name} before ${it::class.java.simpleName}")
        }
    }

    private fun reduceListener(action: Action<*>, oldState: T) {
        listeners.parallelFor {
            notifyReducer(it, oldState)
            System.out.println("${action.name} listener ${it::class.java.simpleName}")
        }
    }

    private fun reduceAfter(action: Action<*>) {
        middlewares.parallelFor {
            it.after(state, action)
            System.out.println("${action.name} after ${it::class.java.simpleName}")
        }
    }

    private fun notifyReducer(it: StateListener<T>, oldState: T) {
        if (it.hasChanged(state, oldState)) it.onChanged(state)
    }
}
