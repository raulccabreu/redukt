package com.github.raulccabreu.redukt

import com.github.raulccabreu.redukt.actions.Action
import com.github.raulccabreu.redukt.reducers.Reducer
import org.junit.Assert.assertEquals
import org.junit.Test

class ReduktTest {
    @Test
    fun initialState() {
        val redukt = Redukt("initial")
        assertEquals("initial", redukt.state)
        assertEquals(0, redukt.reducers.size)
        redukt.stop()
    }

    @Test
    fun afterAddReducer() {
        val redukt  = Redukt<String>("")
        val reducer = object: Reducer<String> {
            override fun reduce(state: String, action: Action<*>) = state
        }
        redukt.reducers["reducer"] = reducer
        assertEquals(1, redukt.reducers.size)
        assertEquals(reducer, redukt.reducers.values.first())
        redukt.stop()
    }

    @Test
    fun afterDispatch() {
        val redukt = Redukt("initial")
        val reducer = object: Reducer<String> {
            override fun reduce(state: String, action: Action<*>) = action.payload.toString()
        }
        redukt.reducers["reducer"] = reducer
        assertEquals("initial", redukt.state)
        redukt.dispatch(Action("action", "new state"), false)
        assertEquals("new state", redukt.state)
        redukt.dispatch(Action("action", "another state"), false)
        assertEquals("another state", redukt.state)
        redukt.stop()
    }

    @Test
    fun afterDispatchWithTwoReducers() {
        val redukt = Redukt("initial")
        val changerReducer = object: Reducer<String> {
            override fun reduce(state: String, action: Action<*>) = action.payload.toString()
        }
        val upperCaseReducer = object: Reducer<String> {
            override fun reduce(state: String, action: Action<*>) = state.toUpperCase()
        }
        redukt.reducers["changerReducer"] = changerReducer
        redukt.reducers["upperCaseReducer"] = upperCaseReducer
        assertEquals("initial", redukt.state)
        redukt.dispatch(Action("action", "new state"), false)
        assertEquals("NEW STATE", redukt.state)
        redukt.dispatch(Action("action", "another state"), false)
        assertEquals("ANOTHER STATE", redukt.state)
        redukt.stop()
    }

    @Test
    fun afterDispatchTooManyActions() {
        val redukt = Redukt("initial")
        val changerReducer = object: Reducer<String> {
            override fun reduce(state: String, action: Action<*>) = action.payload.toString()
        }
        redukt.reducers["changerReducer"] = changerReducer
        for(pos in 1 until 101) {
            redukt.dispatch(Action("action", "new state $pos"), false)
        }
        assertEquals("new state 100", redukt.state)
        redukt.stop()
    }
}
