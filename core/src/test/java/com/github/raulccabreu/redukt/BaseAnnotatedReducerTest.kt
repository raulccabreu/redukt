package com.github.raulccabreu.redukt

import com.github.raulccabreu.redukt.actions.Action
import com.github.raulccabreu.redukt.actions.Reduce
import com.github.raulccabreu.redukt.reducers.BaseAnnotatedReducer
import junit.framework.Assert
import org.junit.Test

class BaseAnnotatedReducerTest {

    @Test
    fun whenUseValidBaseReducer() {
        val redukt = Redukt("initial")
        redukt.reducers["validReducer"] = ValidReducer()

        redukt.dispatch(Action("valid", "new state"), false)

        Assert.assertEquals("new state", redukt.state)
        redukt.stop()
    }

    @Test
    fun whenUseValidBaseReducerExecuteTwice() {
        val redukt = Redukt("initial")
        redukt.reducers["validReducer"] = ValidReducer()

        redukt.dispatch(Action("valid", "new state"), false)
        redukt.dispatch(Action("valid", "new state2"), false)

        Assert.assertEquals("new state2", redukt.state)
        redukt.stop()
    }

    @Test
    fun whenUseInvalidBaseReducer() {
        val redukt = Redukt("initial")
        redukt.reducers["validReducer"] = ValidReducer()

        redukt.dispatch(Action("invalid", "new state"), false)

        Assert.assertEquals("initial", redukt.state)
        redukt.stop()
    }

    @Test
    fun afterDispatchWithTwoReducers() {
        val redukt = Redukt("initial")
        redukt.reducers["validReducer"] = ValidReducer()
        redukt.reducers["upperReducer"] = UpperCaseReducer()
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
        redukt.reducers["validReducer"] = ValidReducer()
        for(pos in 1 until 1001) {
            redukt.dispatch(Action("valid", "new state $pos"), false)
        }
        Assert.assertEquals("new state 1000", redukt.state)
        redukt.stop()
    }

    @Test
    fun invalidArgumentException() {
        val redukt = Redukt("initial")
        try {
            redukt.reducers["invalidArgumentReducer"] = InvalidArgumentReducer()
            Assert.assertTrue(false)
        } catch (ex: Exception) {
            System.out.println("${ex.message}")
            assert(ex is IllegalArgumentException)
        }
    }

    @Test
    fun invalidParameterException() {
        val redukt = Redukt("initial")
        try {
            redukt.reducers["invalidParameterReducer"] = InvalidParameterReducer()
            Assert.assertTrue(false)
        } catch (ex: Exception) {
            System.out.println("${ex.message}")
            assert(ex is IllegalArgumentException)
        } finally {
            redukt.stop()
        }
    }

    @Test
    fun invalidReturnParameterException() {
        val redukt = Redukt("initial")
        try {
            redukt.reducers["voidReturnReducer"] = VoidReturnReducer()
            Assert.assertTrue(false)
        } catch (ex: Exception) {
            System.out.println("${ex.message}")
            Assert.assertTrue(ex is IllegalArgumentException)
        } finally {
            redukt.stop()
        }
    }

    @Test
    fun invalidReturnParameter() {
        val redukt = Redukt("initial")
        redukt.reducers["invalidReturnReducer"] = InvalidReturnReducer()

        redukt.dispatch(Action("valid", "new state"), false)

        Assert.assertEquals("initial", redukt.state)
        redukt.stop()
    }


    inner class ValidReducer : BaseAnnotatedReducer<String>() {
        @Reduce("valid")
        fun testBaseReducer(state: String, payload: String): String {
            return payload
        }
    }

    inner class UpperCaseReducer : BaseAnnotatedReducer<String>() {
        @Reduce("valid")
        fun testBaseReducer(state: String, payload: String): String {
            return payload.toUpperCase()
        }
    }

    inner class InvalidArgumentReducer : BaseAnnotatedReducer<String>() {
        @Reduce("")
        fun testBaseReducer(state: String, payload: String): String {
            return payload
        }
    }

    inner class InvalidParameterReducer : BaseAnnotatedReducer<String>() {
        @Reduce("invalid")
        fun testBaseReducer(): String {
            return ""
        }
    }

    inner class VoidReturnReducer : BaseAnnotatedReducer<String>() {
        @Reduce("invalid")
        fun testBaseReducer(state: String, payload: String) { }
    }

    inner class InvalidReturnReducer : BaseAnnotatedReducer<String>() {
        @Reduce("invalid")
        fun testBaseReducer(state: String, payload: String): Int {
            return 1
        }
    }
}