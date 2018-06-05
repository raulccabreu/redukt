package com.github.raulccabreu.redukt

import com.github.raulccabreu.redukt.actions.Action
import com.github.raulccabreu.redukt.reducers.Reducer
import com.github.raulccabreu.redukt.states.StateListener
import junit.framework.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.CountDownLatch

class DispatcherTest {
    @Test
    fun afterDispatchTooManyActionsAsync() {
        val actionsCount = Dispatcher.MAX_ACTIONS - 1
        val redukt = Redukt<String>("initial")
        val changerReducer = object: Reducer<String> {
            override fun reduce(state: String, action: Action<*>) = action.payload.toString()
        }
        val signal = CountDownLatch(actionsCount)
        val listener = object: StateListener<String> {
            override fun hasChanged(newState: String, oldState: String) = true
            override fun onChanged(state: String) { signal.countDown() }
        }
        redukt.reducers["changerReducer"] = changerReducer
        redukt.listeners.add(listener)
        for(pos in 0 until actionsCount) {
            redukt.dispatch(Action("action", "new state $pos"))
        }
        signal.await()
        assertEquals("new state ${actionsCount - 1}", redukt.state)
        redukt.stop()
    }

    @Test(expected = IllegalStateException::class)
    fun afterDispatchMoreActionsThanIsAllowed() {
        val actionsCount = Dispatcher.MAX_ACTIONS * 10
        val redukt = Redukt("initial")
        val changerReducer = object: Reducer<String> {
            override fun reduce(state: String, action: Action<*>) = action.payload.toString()
        }
        redukt.reducers["changerReducer"] = changerReducer
        for(pos in 0 until actionsCount) redukt.dispatch(Action("action", ""))
        redukt.stop()
    }

    @Test
    fun dispatchWithTooManyListeners() {
        val listenersCount = 1001
        val signal = CountDownLatch(listenersCount)
        val redukt = Redukt("initial")
        val changerReducer = object: Reducer<String> {
            override fun reduce(state: String, action: Action<*>) = action.payload.toString()
        }
        for(pos in 0 until listenersCount) {
            val listener = object: StateListener<String> {
                override fun hasChanged(newState: String, oldState: String) = true
                override fun onChanged(state: String) { signal.countDown() }
            }
            redukt.listeners.add(listener)
        }
        redukt.reducers["changerReducer"] = changerReducer
        redukt.dispatch(Action("action", "new state"))
        signal.await()
        assertEquals("new state", redukt.state)
        redukt.stop()
    }
}
