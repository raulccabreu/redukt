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

    private fun reduce(action: MiddlewareAction<*>) {
        System.out.println("${action.name} reduce middleware")
        middlewares.parallelFor { it.before(state, action) }
    }

    private fun reduce(action: Action<*>) {
        if (action is MiddlewareAction) {
            reduce(action)
            return
        }
        System.out.println("${action.name} reduce")

        val oldState = state
        var tempState = state
        middlewares.parallelFor {
            it.before(tempState, action)
            System.out.println("${action.name} before ${it::class.java.simpleName}")
        }

        reducers.forEach {
            tempState = it.reduce(tempState, action)
            System.out.println("${action.name} reducer ${it::class.java.simpleName}")
        }
        state = tempState
        listeners.parallelFor {
            notifyReducer(it, oldState)
            System.out.println("${action.name} listener ${it::class.java.simpleName}")
        }
        middlewares.parallelFor {
            it.after(tempState, action)
            System.out.println("${action.name} after ${it::class.java.simpleName}")
        }
    }

    private fun notifyReducer(it: StateListener<T>, oldState: T) {
        if (it.hasChanged(state, oldState)) it.onChanged(state)
    }
}
