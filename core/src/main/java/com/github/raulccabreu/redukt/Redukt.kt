package com.github.raulccabreu.redukt

import com.github.raulccabreu.redukt.actions.Action
import com.github.raulccabreu.redukt.middlewares.DebugMiddleware
import com.github.raulccabreu.redukt.middlewares.Middleware
import com.github.raulccabreu.redukt.reducers.Reducer
import com.github.raulccabreu.redukt.states.StateListener
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.system.measureTimeMillis

class Redukt<T>(state: T, debug: Boolean = false) {
    var state = state
        private set
    val reducers = ConcurrentLinkedQueue<Reducer<T>>()
    val middlewares = ConcurrentLinkedQueue<Middleware<T>>()
    val listeners = ConcurrentLinkedQueue<StateListener<T>>()
    val debug = debug
    private val dispatcher = Dispatcher { reduce(it) }

    init {
        if (debug) {
            val debugMiddleware = DebugMiddleware<T>()
            middlewares.add(debugMiddleware)
            listeners.add(debugMiddleware)
        }
        start()
    }

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
        log("<Redukt> has spent [$elapsed ms] with [${action.name}]")
    }

    private fun log(message: String) {
        if (debug) debug(message)
    }

    private fun notifyListeners(it: StateListener<T>, oldState: T) {
        if (it.hasChanged(state, oldState)) it.onChanged(state)
    }
}
