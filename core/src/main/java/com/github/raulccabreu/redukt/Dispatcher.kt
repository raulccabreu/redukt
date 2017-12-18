package com.github.raulccabreu.redukt

import com.github.raulccabreu.redukt.actions.Action
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue

class Dispatcher(val processNewAction: (Action<*>) -> Unit) {
    companion object {
        val MAX_ACTIONS = 1000 // How many actions we should allow? It could be custom?
    }

    private val actionQueue: BlockingQueue<Action<*>> = ArrayBlockingQueue(MAX_ACTIONS)
    private var running = false

    // Event loop using a thread implementation is enough?
    // Do we need another approach? With Executor, Coroutines or ThreadPools.
    private val eventLoop = Thread {
        try {
            while(running) {
                val action = actionQueue.take()
                processNewAction(action)
            }
        } catch (e: InterruptedException) { }
    }

    fun dispatch(action: Action<*>) {
        if (!actionQueue.offer(action))
            throw IllegalStateException("Dispatcher receive more than $MAX_ACTIONS")
    }

    fun start() {
        if (running) return
        running = true
        eventLoop.start()
    }

    fun stop() {
        running = false
        eventLoop.interrupt()
    }
}

