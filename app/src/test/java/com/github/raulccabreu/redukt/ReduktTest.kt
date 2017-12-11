package com.github.raulccabreu.redukt

import com.github.raulccabreu.redukt.actions.Action
import com.github.raulccabreu.redukt.reducers.Reducer
import org.junit.Assert.assertEquals
import org.junit.Test

class ReduktTest {
    @Test
    fun initialState() {
        val redukt = Redukt<String>("initial")
        assertEquals("initial", redukt.state)
        assertEquals(0, redukt.reducers.size)
    }

    @Test
    fun afterAddReducer() {
        val redukt  = Redukt<String>("")
        val reducer = object: Reducer<String> {
            override fun reduce(state: String, action: Action<*>) = state
        }
        redukt.reducers.add(reducer)
        assertEquals(1, redukt.reducers.size)
        assertEquals(reducer, redukt.reducers.first())
    }

    @Test
    fun afterDispatch() {
        val redukt = Redukt<String>("initial")
        val reducer = object: Reducer<String> {
            override fun reduce(state: String, action: Action<*>) = action.payload.toString()
        }
        redukt.reducers.add(reducer)
        assertEquals("initial", redukt.state)
        redukt.dispatch(Action("action", "new state"))
        assertEquals("new state", redukt.state)
        redukt.dispatch(Action("action", "another state"))
        assertEquals("another state", redukt.state)
    }

    @Test
    fun afterDispatchWithTwoReducers() {
        val redukt = Redukt<String>("initial")
        val changerReducer = object: Reducer<String> {
            override fun reduce(state: String, action: Action<*>) = action.payload.toString()
        }
        val upcaseReducer = object: Reducer<String> {
            override fun reduce(state: String, action: Action<*>) = state.toUpperCase()
        }
        redukt.reducers.add(changerReducer)
        redukt.reducers.add(upcaseReducer)
        assertEquals("initial", redukt.state)
        redukt.dispatch(Action("action", "new state"))
        assertEquals("NEW STATE", redukt.state)
        redukt.dispatch(Action("action", "another state"))
        assertEquals("ANOTHER STATE", redukt.state)
    }
}
