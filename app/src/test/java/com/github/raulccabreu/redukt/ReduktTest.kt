package com.github.raulccabreu.redukt

import com.github.raulccabreu.redukt.actions.Action
import com.github.raulccabreu.redukt.reducers.Reducer
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Created by niltonvasques on 12/11/17.
 */
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
}
