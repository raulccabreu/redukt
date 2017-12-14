package com.github.raulccabreu.redukt

import com.github.raulccabreu.redukt.actions.Action
import com.github.raulccabreu.redukt.middlewares.AfterAction
import com.github.raulccabreu.redukt.middlewares.BaseAnnotatedMiddleware
import com.github.raulccabreu.redukt.middlewares.BeforeAction
import com.github.raulccabreu.redukt.reducers.Reducer
import org.junit.Assert
import org.junit.Test
import java.security.InvalidParameterException
import java.util.concurrent.CountDownLatch

class BaseAnnotatedMiddlewareTest {

    @Test
    fun whenUseBeforeAction() {
        var result: Pair<String, String>? = null
        val signal = CountDownLatch(1)

        val beforeMiddleware = ValidMiddleware { state, actionName ->
            result = Pair(state, actionName)
            signal.countDown()
        }

        val redukt = Redukt("initial")
        redukt.middlewares.add(beforeMiddleware)

        redukt.dispatch(Action("valid", "new state"), false)

        signal.await()

        Assert.assertEquals("initial", result?.first)
        Assert.assertEquals("valid", result?.second)

        redukt.stop()
    }

    @Test
    fun whenUseAfterAction() {
        var result: Pair<String, String>? = null
        val signal = CountDownLatch(1)

        val beforeMiddleware = ValidMiddleware ( afterCallback = { state, actionName ->
            result = Pair(state, actionName)
            signal.countDown()
        })

        val redukt = Redukt("initial")
        redukt.middlewares.add(beforeMiddleware)

        redukt.dispatch(Action("valid", "new state"), false)

        signal.await()

        Assert.assertEquals("initial", result?.first)
        Assert.assertEquals("valid", result?.second)

        redukt.stop()
    }

    @Test
    fun whenUseBeforeAfterAction() {
        var resultBefore: Pair<String, String>? = null
        var resultAfter: Pair<String, String>? = null
        val signal = CountDownLatch(2)

        val beforeMiddleware = ValidMiddleware ({ state, actionName ->
            resultBefore = Pair(state, actionName)
            signal.countDown()
        }, { state, actionName ->
            resultAfter = Pair(state, actionName)
            signal.countDown()
        })

        val redukt = Redukt("initial")
        redukt.middlewares.add(beforeMiddleware)

        redukt.dispatch(Action("valid", "new state"), false)

        signal.await()

        Assert.assertEquals("initial", resultBefore?.first)
        Assert.assertEquals("valid", resultBefore?.second)
        Assert.assertEquals("initial", resultAfter?.first)
        Assert.assertEquals("valid", resultAfter?.second)

        redukt.stop()
    }

    @Test
    fun whenChangeContent() {
        var resultBefore: Pair<String, String>? = null
        var resultAfter: Pair<String, String>? = null
        val signal = CountDownLatch(2)

        val changerReducer = object: Reducer<String> {
            override fun reduce(state: String, action: Action<*>) = action.payload.toString()
        }

        val beforeMiddleware = ValidMiddleware ({ state, actionName ->
            resultBefore = Pair(state, actionName)
            signal.countDown()
        }, { state, actionName ->
            resultAfter = Pair(state, actionName)
            signal.countDown()
        })

        val redukt = Redukt("initial")
        redukt.middlewares.add(beforeMiddleware)
        redukt.reducers.add(changerReducer)

        redukt.dispatch(Action("valid", "new state"), false)

        signal.await()

        Assert.assertEquals("initial", resultBefore?.first)
        Assert.assertEquals("valid", resultBefore?.second)
        Assert.assertEquals("new state", resultAfter?.first)
        Assert.assertEquals("valid", resultAfter?.second)

        redukt.stop()
    }

    inner class ValidMiddleware(val beforeCallback: ((String, String) -> Unit)? = null,
                                val afterCallback: ((String, String) -> Unit)? = null) :
            BaseAnnotatedMiddleware<String>() {

        @BeforeAction("valid")
        fun beforeAction(state: String, action: Action<*>) {
            beforeCallback?.invoke(state, action.name)
        }

        @AfterAction("valid")
        fun afterAction(state: String, action: Action<*>) {
            afterCallback?.invoke(state, action.name)
        }

    }

    @Test
    fun invalidBeforeActionException() {
        val redukt = Redukt("initial")
        try {
            redukt.middlewares.add(InvalidBeforeAction())
            junit.framework.Assert.assertTrue(false)
        } catch (ex: Exception) {
            System.out.println("${ex.message}")
            junit.framework.Assert.assertTrue(ex is IllegalArgumentException)
        } finally {
            redukt.stop()
        }
    }

    @Test
    fun invalidBeforeMethodsException() {
        val redukt = Redukt("initial")
        try {
            redukt.middlewares.add(InvalidBeforeMethods())
            junit.framework.Assert.assertTrue(false)
        } catch (ex: Exception) {
            System.out.println("${ex.message}")
            junit.framework.Assert.assertTrue(ex is InvalidParameterException)
        } finally {
            redukt.stop()
        }
    }

    @Test
    fun invalidAfterActionException() {
        val redukt = Redukt("initial")
        try {
            redukt.middlewares.add(InvalidAfterAction())
            junit.framework.Assert.assertTrue(false)
        } catch (ex: Exception) {
            System.out.println("${ex.message}")
            junit.framework.Assert.assertTrue(ex is IllegalArgumentException)
        } finally {
            redukt.stop()
        }
    }

    @Test
    fun invalidAfterMethodsException() {
        val redukt = Redukt("initial")
        try {
            redukt.middlewares.add(InvalidAfterMethods())
            junit.framework.Assert.assertTrue(false)
        } catch (ex: Exception) {
            System.out.println("${ex.message}")
            junit.framework.Assert.assertTrue(ex is InvalidParameterException)
        } finally {
            redukt.stop()
        }
    }

    inner class InvalidBeforeAction : BaseAnnotatedMiddleware<String>() {
        @BeforeAction("")
        fun beforeAction(state: String, action: Action<*>) { }
    }

    inner class InvalidBeforeMethods : BaseAnnotatedMiddleware<String>() {
        @BeforeAction("Invalid")
        fun beforeAction() { }
    }

    inner class InvalidAfterAction : BaseAnnotatedMiddleware<String>() {
        @AfterAction("")
        fun afterAction(state: String, action: Action<*>) { }
    }

    inner class InvalidAfterMethods : BaseAnnotatedMiddleware<String>() {
        @AfterAction("Invalid")
        fun afterAction() { }
    }
}