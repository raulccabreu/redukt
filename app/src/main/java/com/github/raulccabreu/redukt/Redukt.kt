package com.github.raulccabreu.redukt

import com.github.raulccabreu.redukt.actions.Action
import com.github.raulccabreu.redukt.middlewares.Middleware
import com.github.raulccabreu.redukt.reducers.Reducer
import com.github.raulccabreu.redukt.states.StateListener
import kotlin.system.measureTimeMillis

class Redukt<T>(state: T) {
    var state = state
        private set
    val reducers = mutableSetOf<Reducer<T>>()
    val middlewares = mutableSetOf<Middleware<T>>()
    val listeners = mutableSetOf<StateListener<T>>()
    var traceActionProfile = false
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
        val elapsed = measureTimeMillis {
            val oldState = state
            var tempState = state
            middlewares.parallelFor { it.before(tempState, action) }
            reducers.forEach { tempState = it.reduce(tempState, action) }
            state = tempState
            listeners.parallelFor { notifyListeners(it, oldState) }
            middlewares.parallelFor { it.after(tempState, action) }
        }
        if (traceActionProfile) println("[Redukt] [$elapsed ms] Action ${action.name}")
    }

    private fun notifyListeners(it: StateListener<T>, oldState: T) {
        if (it.hasChanged(state, oldState)) it.onChanged(state)
    }
}
