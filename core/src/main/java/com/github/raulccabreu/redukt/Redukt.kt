package com.github.raulccabreu.redukt

import com.github.raulccabreu.redukt.actions.Action
import com.github.raulccabreu.redukt.middlewares.DebugMiddleware
import com.github.raulccabreu.redukt.middlewares.Middleware
import com.github.raulccabreu.redukt.reducers.Reducer
import com.github.raulccabreu.redukt.states.StateListener
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap
import com.googlecode.concurrentlinkedhashmap.EvictionListener
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.system.measureTimeMillis

class Redukt<T>(state: T, val debug: Boolean = false) {
    private val reducerListener = EvictionListener<String, Reducer<T>> { key, _ ->
        log("<Redukt> added reducer: $key")
    }

    private val middlewareListener = EvictionListener<String, Middleware<T>> { key, _ ->
        log("<Redukt> added middleware: $key")
    }

    var state = state
        private set
    val reducers = ConcurrentLinkedHashMap
            .Builder<String, Reducer<T>>()
            .maximumWeightedCapacity(1000)
            .listener(reducerListener)
            .build()
    val middlewares = ConcurrentLinkedHashMap
            .Builder<String, Middleware<T>>()
            .maximumWeightedCapacity(1000)
            .listener(middlewareListener)
            .build()
    val listeners = ConcurrentLinkedQueue<StateListener<T>>()
    private val dispatcher = Dispatcher { reduce(it) }

    init {
        addDebugMiddleware()
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
            middlewares.ascendingMap().values.parallelFor { it.before(tempState, action) }
            reducers.ascendingMap().values.forEach { tempState = it.reduce(tempState, action) }
            state = tempState
            listeners.parallelFor { notifyListeners(it, oldState) }
            middlewares.ascendingMap().values.parallelFor { it.after(tempState, action) }
        }
        log("<Redukt> has spent [$elapsed ms] with [${action.name}]")
    }

    private fun log(message: String) {
        if (debug) debug(message)
    }

    private fun notifyListeners(it: StateListener<T>, oldState: T) {
        if (it.hasChanged(state, oldState)) it.onChanged(state)
    }

    private fun addDebugMiddleware() {
        if (!debug) return

        val debugMiddleware = DebugMiddleware<T>()
        middlewares["com.github.raulccabreu.redukt.debugMiddleware"] = debugMiddleware
        listeners.add(debugMiddleware)
    }
}
