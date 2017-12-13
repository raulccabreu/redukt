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
        middlewares.parallelFor { it.before(state, action) }
    }

    private fun reduce(action: Action<*>) {
        if (action is MiddlewareAction) {
            reduce(action)
            return
        }
        val oldState = state
        var tempState = state
        middlewares.parallelFor { it.before(tempState, action) }
        reducers.forEach { tempState = it.reduce(tempState, action) }
        state = tempState
        listeners.parallelFor { notifyReducer(it, oldState) }
        middlewares.parallelFor { it.after(tempState, action) }
    }

    private fun notifyReducer(it: StateListener<T>, oldState: T) {
        if (it.hasChanged(state, oldState)) it.onChanged(state)
    }
}
