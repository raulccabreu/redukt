package com.github.raulccabreu.redukt

import com.github.raulccabreu.redukt.actions.Action
import com.github.raulccabreu.redukt.actions.ReducerAction
import com.github.raulccabreu.redukt.reducers.BaseAnnotatedReducer
import junit.framework.Assert
import org.junit.Test

class BaseAnnotatedReducerTest {

    @Test
    fun whenUseValidBaseReducer() {
        val redukt = Redukt("initial")
        redukt.reducers.add(ValidReducer())

        redukt.dispatch(Action("valid", "new state"), false)

        Assert.assertEquals("new state", redukt.state)
        redukt.stop()
    }

    @Test
    fun whenUseValidBaseReducerExecuteTwice() {
        val redukt = Redukt("initial")
        redukt.reducers.add(ValidReducer())

        redukt.dispatch(Action("valid", "new state"), false)
        redukt.dispatch(Action("valid", "new state2"), false)

        Assert.assertEquals("new state2", redukt.state)
        redukt.stop()
    }

    @Test
    fun whenUseInvalidBaseReducer() {
        val redukt = Redukt("initial")
        redukt.reducers.add(ValidReducer())

        redukt.dispatch(Action("invalid", "new state"), false)

        Assert.assertEquals("initial", redukt.state)
        redukt.stop()
    }

    @Test
    fun afterDispatchWithTwoReducers() {
        val redukt = Redukt("initial")
        redukt.reducers.add(ValidReducer())
        redukt.reducers.add(UpperCaseReducer())
        org.junit.Assert.assertEquals("initial", redukt.state)
        redukt.dispatch(Action("valid", "new state"), false)
        org.junit.Assert.assertEquals("NEW STATE", redukt.state)
        redukt.dispatch(Action("valid", "another state"), false)
        org.junit.Assert.assertEquals("ANOTHER STATE", redukt.state)
        redukt.stop()
    }

    @Test
    fun afterDispatchTooManyActions() {
        val redukt = Redukt("initial")
        redukt.reducers.add(ValidReducer())
        for(pos in 1 until 1001) {
            redukt.dispatch(Action("valid", "new state $pos"), false)
        }
        Assert.assertEquals("new state 1000", redukt.state)
        redukt.stop()
    }

    inner class ValidReducer : BaseAnnotatedReducer<String>() {
        @ReducerAction("valid")
        fun testBaseReducer(state: String, payload: String): String {
            return payload
        }
    }

    inner class UpperCaseReducer : BaseAnnotatedReducer<String>() {
        @ReducerAction("valid")
        fun testBaseReducer(state: String, payload: String): String {
            return payload.toUpperCase()
        }
    }
}