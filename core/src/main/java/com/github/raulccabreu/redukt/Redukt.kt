package com.github.raulccabreu.redukt

import com.github.raulccabreu.redukt.actions.Action
import com.github.raulccabreu.redukt.middlewares.DebugMiddleware
import com.github.raulccabreu.redukt.middlewares.Middleware
import com.github.raulccabreu.redukt.reducers.Reducer
import com.github.raulccabreu.redukt.states.StateListener
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.system.measureTimeMillis

class Redukt<T>(state: T, private val debug: Boolean = false) {
    var state = state
        private set
    val reducers = ConcurrentHashMap<String, Reducer<T>>()
    val middlewares = ConcurrentHashMap<String, Middleware<T>>()
    val listeners = ConcurrentLinkedQueue<StateListener<T>>()
    private val dispatcher = Dispatcher { reduce(it) }

    init {
        addDebugMiddleware()
        start()
    }

    fun add(reducer: Reducer<T>) {
        reducers[reducer::class.java.canonicalName] = reducer
    }

    fun add(tag: String, reducer: Reducer<T>) {
        reducers[tag] = reducer
    }

    fun add(middleware: Middleware<T>) {
        middlewares[middleware::class.java.canonicalName] = middleware
    }

    fun add(tag: String, middleware: Middleware<T>) {
        middlewares[tag] = middleware
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
            val listeners = listeners.toSet() //to avoid concurrent modification exception
            val middlewares = middlewares.values.toSet()
            val oldState = state
            var tempState = state
            middlewares.parallelFor { it.before(tempState, action) }
            reducers.values.forEach { tempState = it.reduce(tempState, action) }
            state = tempState
            listeners.parallelFor { notifyListeners(it, oldState) }
            middlewares.parallelFor { it.after(tempState, action) }
        }
        if (debug) {
            debug("<Redukt> has spent [$elapsed ms] with [${action.name}]")
        }
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
