package com.github.raulccabreu.redukt

import com.github.raulccabreu.redukt.actions.Action
import com.github.raulccabreu.redukt.middlewares.AfterAction
import com.github.raulccabreu.redukt.middlewares.AfterActions
import com.github.raulccabreu.redukt.middlewares.BaseAnnotatedMiddleware
import com.github.raulccabreu.redukt.middlewares.BeforeAction
import com.github.raulccabreu.redukt.middlewares.BeforeActions
import com.github.raulccabreu.redukt.reducers.Reducer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.security.InvalidParameterException
import java.util.concurrent.CountDownLatch

class BaseAnnotatedMiddlewareTest {

    private lateinit var redukt: Redukt<String>

    @Before
    fun setup() {
        redukt = Redukt("initial")
    }

    @After
    fun close() {
        redukt.stop()
    }

    @Test
    fun whenUseBeforeAction() {
        var result: Pair<String, String>? = null
        val signal = CountDownLatch(1)

        val middleware = ValidMiddleware { state, actionName ->
            result = Pair(state, actionName)
            signal.countDown()
        }

        redukt.middlewares["middleware"] = middleware

        redukt.dispatch(Action("valid", "new state"), false)

        signal.await()

        Assert.assertEquals("initial", result?.first)
        Assert.assertEquals("valid", result?.second)
    }

    @Test
    fun whenUseAfterAction() {
        var result: Pair<String, String>? = null
        val signal = CountDownLatch(1)

        val middleware = ValidMiddleware ( afterCallback = { state, actionName ->
            result = Pair(state, actionName)
            signal.countDown()
        })

        redukt.middlewares["middleware"] = middleware

        redukt.dispatch(Action("valid", "new state"), false)

        signal.await()

        Assert.assertEquals("initial", result?.first)
        Assert.assertEquals("valid", result?.second)
    }

    @Test
    fun whenUseBeforeAfterAction() {
        var resultBefore: Pair<String, String>? = null
        var resultAfter: Pair<String, String>? = null
        val signal = CountDownLatch(2)

        val middleware = ValidMiddleware ({ state, actionName ->
            resultBefore = Pair(state, actionName)
            signal.countDown()
        }, { state, actionName ->
            resultAfter = Pair(state, actionName)
            signal.countDown()
        })

        redukt.middlewares["middleware"] = middleware

        redukt.dispatch(Action("valid", "new state"), false)

        signal.await()

        Assert.assertEquals("initial", resultBefore?.first)
        Assert.assertEquals("valid", resultBefore?.second)
        Assert.assertEquals("initial", resultAfter?.first)
        Assert.assertEquals("valid", resultAfter?.second)
    }

    @Test
    fun whenChangeContent() {
        var resultBefore: Pair<String, String>? = null
        var resultAfter: Pair<String, String>? = null
        val signal = CountDownLatch(2)

        val changerReducer = object: Reducer<String> {
            override fun reduce(state: String, action: Action<*>) = action.payload.toString()
        }

        val middleware = ValidMiddleware ({ state, actionName ->
            resultBefore = Pair(state, actionName)
            signal.countDown()
        }, { state, actionName ->
            resultAfter = Pair(state, actionName)
            signal.countDown()
        })

        redukt.middlewares["middleware"] = middleware
        redukt.reducers["changerReducer"] = changerReducer

        redukt.dispatch(Action("valid", "new state"), false)

        signal.await()

        Assert.assertEquals("initial", resultBefore?.first)
        Assert.assertEquals("valid", resultBefore?.second)
        Assert.assertEquals("new state", resultAfter?.first)
        Assert.assertEquals("valid", resultAfter?.second)
    }

    @Test
    fun invalidBeforeActionException() {
        try {
            redukt.middlewares["invalidBeforeAction"] = InvalidBeforeAction()
            junit.framework.Assert.assertTrue(false)
        } catch (ex: Exception) {
            System.out.println("${ex.message}")
            junit.framework.Assert.assertTrue(ex is IllegalArgumentException)
        }
    }

    @Test
    fun invalidBeforeActionMethodsException() {
        try {
            redukt.middlewares["invalidBeforeActionMethods"] = InvalidBeforeActionMethods()
            junit.framework.Assert.assertTrue(false)
        } catch (ex: Exception) {
            System.out.println("${ex.message}")
            junit.framework.Assert.assertTrue(ex is InvalidParameterException)
        }
    }

    @Test
    fun invalidAfterActionException() {
        try {
            redukt.middlewares["invalidAfterAction"] = InvalidAfterAction()
            junit.framework.Assert.assertTrue(false)
        } catch (ex: Exception) {
            System.out.println("${ex.message}")
            junit.framework.Assert.assertTrue(ex is IllegalArgumentException)
        }
    }

    @Test
    fun invalidAfterActionMethodsException() {
        try {
            redukt.middlewares["invalidAfterActionMethods"] = InvalidAfterActionMethods()
            junit.framework.Assert.assertTrue(false)
        } catch (ex: Exception) {
            System.out.println("${ex.message}")
            junit.framework.Assert.assertTrue(ex is InvalidParameterException)
        }
    }

    @Test
    fun whenUseBeforeActions() {
        var result: Pair<String, String>? = null
        val signal = CountDownLatch(1)

        val middleware = ValidMiddlewareActions { state, actionName ->
            result = Pair(state, actionName)
            signal.countDown()
        }

        redukt.middlewares["middleware"] = middleware

        redukt.dispatch(Action("valid", "new state"), false)

        signal.await()

        Assert.assertEquals("initial", result?.first)
        Assert.assertEquals("valid", result?.second)
    }

    @Test
    fun whenUseAfterActions() {
        var result: Pair<String, String>? = null
        val signal = CountDownLatch(1)

        val middleware = ValidMiddlewareActions ( afterCallback = { state, actionName ->
            result = Pair(state, actionName)
            signal.countDown()
        })

        redukt.middlewares["middleware"] = middleware

        redukt.dispatch(Action("valid", "new state"), false)

        signal.await()

        Assert.assertEquals("initial", result?.first)
        Assert.assertEquals("valid", result?.second)
    }

    @Test
    fun whenUseBeforeAfterActions() {
        var resultBefore: Pair<String, String>? = null
        var resultAfter: Pair<String, String>? = null
        val signal = CountDownLatch(2)

        val middleware = ValidMiddlewareActions ({ state, actionName ->
            resultBefore = Pair(state, actionName)
            signal.countDown()
        }, { state, actionName ->
            resultAfter = Pair(state, actionName)
            signal.countDown()
        })

        redukt.middlewares["middleware"] = middleware

        redukt.dispatch(Action("valid", "new state"), false)

        signal.await()

        Assert.assertEquals("initial", resultBefore?.first)
        Assert.assertEquals("valid", resultBefore?.second)
        Assert.assertEquals("initial", resultAfter?.first)
        Assert.assertEquals("valid", resultAfter?.second)
    }

    @Test
    fun whenUseBeforeAfterActionsWithTwoFilters() {
        var resultBefore: Pair<String, String>? = null
        var resultAfter: Pair<String, String>? = null
        var signal = CountDownLatch(2)

        val middleware = ValidMiddlewareActionsWithFilter ({ state, actionName ->
            resultBefore = Pair(state, actionName)
            signal.countDown()
        }, { state, actionName ->
            resultAfter = Pair(state, actionName)
            signal.countDown()
        })

        redukt.middlewares["middleware"] = middleware

        redukt.dispatch(Action("action_a", "new state"), false)

        signal.await()

        Assert.assertEquals("initial", resultBefore?.first)
        Assert.assertEquals("action_a", resultBefore?.second)
        Assert.assertEquals("initial", resultAfter?.first)
        Assert.assertEquals("action_a", resultAfter?.second)

        resultBefore = null
        resultAfter = null
        signal = CountDownLatch(2)

        redukt.dispatch(Action("action_b", "new state"), false)

        signal.await()

        Assert.assertEquals("initial", resultBefore?.first)
        Assert.assertEquals("action_b", resultBefore?.second)
        Assert.assertEquals("initial", resultAfter?.first)
        Assert.assertEquals("action_b", resultAfter?.second)
    }

    @Test
    fun whenUseBeforeAfterActionsWithTwoDifferentActions() {
        var resultBefore: Pair<String, String>? = null
        var resultAfter: Pair<String, String>? = null
        var signal = CountDownLatch(2)

        val middleware = ValidMiddlewareActions ({ state, actionName ->
            resultBefore = Pair(state, actionName)
            signal.countDown()
        }, { state, actionName ->
            resultAfter = Pair(state, actionName)
            signal.countDown()
        })

        redukt.middlewares["middleware"] = middleware

        redukt.dispatch(Action("valid", "new state"), false)

        signal.await()

        Assert.assertEquals("initial", resultBefore?.first)
        Assert.assertEquals("valid", resultBefore?.second)
        Assert.assertEquals("initial", resultAfter?.first)
        Assert.assertEquals("valid", resultAfter?.second)

        signal = CountDownLatch(2)

        redukt.dispatch(Action("otherAction", "new state"), false)

        signal.await()

        Assert.assertEquals("initial", resultBefore?.first)
        Assert.assertEquals("otherAction", resultBefore?.second)
        Assert.assertEquals("initial", resultAfter?.first)
        Assert.assertEquals("otherAction", resultAfter?.second)
    }

    @Test
    fun whenUseBeforeAfterActionsAndChangeContent() {
        var resultBefore: Pair<String, String>? = null
        var resultAfter: Pair<String, String>? = null
        val signal = CountDownLatch(2)

        val reducer = object: Reducer<String> {
            override fun reduce(state: String, action: Action<*>) = action.payload.toString()
        }

        val middleware = ValidMiddlewareActions ({ state, actionName ->
            resultBefore = Pair(state, actionName)
            signal.countDown()
        }, { state, actionName ->
            resultAfter = Pair(state, actionName)
            signal.countDown()
        })

        redukt.middlewares["middleware"] = middleware
        redukt.reducers["reducer"] = reducer

        redukt.dispatch(Action("valid", "new state"), false)

        signal.await()

        Assert.assertEquals("initial", resultBefore?.first)
        Assert.assertEquals("valid", resultBefore?.second)
        Assert.assertEquals("new state", resultAfter?.first)
        Assert.assertEquals("valid", resultAfter?.second)
    }

    @Test
    fun whenUseBeforeAfterActionsAndChangeContentWithTwoFilters() {
        var resultBefore: Pair<String, String>? = null
        var resultAfter: Pair<String, String>? = null
        var signal = CountDownLatch(2)

        val reducer = object: Reducer<String> {
            override fun reduce(state: String, action: Action<*>) = action.payload.toString()
        }

        val middleware = ValidMiddlewareActionsWithFilter ({ state, actionName ->
            resultBefore = Pair(state, actionName)
            signal.countDown()
        }, { state, actionName ->
            resultAfter = Pair(state, actionName)
            signal.countDown()
        })

        redukt.middlewares["middleware"] = middleware
        redukt.reducers["reducer"] = reducer

        redukt.dispatch(Action("action_a", "new state"), false)

        signal.await()

        Assert.assertEquals("initial", resultBefore?.first)
        Assert.assertEquals("action_a", resultBefore?.second)
        Assert.assertEquals("new state", resultAfter?.first)
        Assert.assertEquals("action_a", resultAfter?.second)

        resultBefore = null
        resultAfter = null
        signal = CountDownLatch(2)

        redukt.dispatch(Action("action_b", "new state second"), false)

        signal.await()

        Assert.assertEquals("new state", resultBefore?.first)
        Assert.assertEquals("action_b", resultBefore?.second)
        Assert.assertEquals("new state second", resultAfter?.first)
        Assert.assertEquals("action_b", resultAfter?.second)
    }

    @Test
    fun invalidAfterActionsMethodsException() {
        try {
            redukt.middlewares["invalidAfterActionsMethods"] = InvalidAfterActionsMethods()
            junit.framework.Assert.assertTrue(false)
        } catch (ex: Exception) {
            System.out.println("${ex.message}")
            junit.framework.Assert.assertTrue(ex is InvalidParameterException)
        }
    }

    @Test
    fun invalidBeforeActionsMethodsException() {
        try {
            redukt.middlewares["invalidBeforeActionsMethods"] = InvalidBeforeActionsMethods()
            junit.framework.Assert.assertTrue(false)
        } catch (ex: Exception) {
            System.out.println("${ex.message}")
            junit.framework.Assert.assertTrue(ex is InvalidParameterException)
        }
    }

    @Test
    fun whenInterceptAllAnnotationsInOneClass() {
        var resultBefore: Pair<String, String>? = null
        var resultAfter: Pair<String, String>? = null
        var resultBefores: Pair<String, String>? = null
        var resultAfters: Pair<String, String>? = null
        val signal = CountDownLatch(2)

        val changerReducer = object: Reducer<String> {
            override fun reduce(state: String, action: Action<*>) = action.payload.toString()
        }

        val middleware = Middleware ({ state, actionName ->
            resultBefore = Pair(state, actionName)
            signal.countDown()
        }, { state, actionName ->
            resultAfter = Pair(state, actionName)
            signal.countDown()
        },{ state, actionName ->
            resultBefores = Pair(state, actionName)
            signal.countDown()
        }, { state, actionName ->
            resultAfters = Pair(state, actionName)
            signal.countDown()
        })

        redukt.middlewares["middleware"] = middleware
        redukt.reducers["changerReducer"] = changerReducer

        redukt.dispatch(Action("valid", "new state"), false)

        signal.await()

        Assert.assertEquals("initial", resultBefore?.first)
        Assert.assertEquals("valid", resultBefore?.second)
        Assert.assertEquals("new state", resultAfter?.first)
        Assert.assertEquals("valid", resultAfter?.second)

        Assert.assertEquals("initial", resultBefores?.first)
        Assert.assertEquals("valid", resultBefores?.second)
        Assert.assertEquals("new state", resultAfters?.first)
        Assert.assertEquals("valid", resultAfters?.second)
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

    inner class InvalidBeforeAction : BaseAnnotatedMiddleware<String>() {
        @BeforeAction("")
        fun beforeAction(state: String, action: Action<*>) { }
    }

    inner class InvalidBeforeActionMethods : BaseAnnotatedMiddleware<String>() {
        @BeforeAction("Invalid")
        fun beforeAction() { }
    }

    inner class InvalidAfterAction : BaseAnnotatedMiddleware<String>() {
        @AfterAction("")
        fun afterAction(state: String, action: Action<*>) { }
    }

    inner class InvalidAfterActionMethods : BaseAnnotatedMiddleware<String>() {
        @AfterAction("Invalid")
        fun afterAction() { }
    }

    inner class ValidMiddlewareActions(val beforeCallback: ((String, String) -> Unit)? = null,
                                       val afterCallback: ((String, String) -> Unit)? = null) :
            BaseAnnotatedMiddleware<String>() {

        @BeforeActions
        fun beforeActions(state: String, action: Action<*>) {
            beforeCallback?.invoke(state, action.name)
        }

        @AfterActions
        fun afterActions(state: String, action: Action<*>) {
            afterCallback?.invoke(state, action.name)
        }

    }

    inner class ValidMiddlewareActionsWithFilter(val beforeCallback: ((String, String) -> Unit)? = null,
                                       val afterCallback: ((String, String) -> Unit)? = null) :
            BaseAnnotatedMiddleware<String>() {

        @BeforeActions(["action_a", "action_b"])
        fun beforeActions(state: String, action: Action<*>) {
            beforeCallback?.invoke(state, action.name)
        }

        @AfterActions(["action_a", "action_b"])
        fun afterActions(state: String, action: Action<*>) {
            afterCallback?.invoke(state, action.name)
        }

    }

    inner class InvalidBeforeActionsMethods : BaseAnnotatedMiddleware<String>() {
        @BeforeActions
        fun beforeAction() { }
    }

    inner class InvalidAfterActionsMethods : BaseAnnotatedMiddleware<String>() {
        @AfterActions
        fun afterAction() { }
    }

    inner class Middleware(val before: ((String, String) -> Unit)? = null,
                           val aftert: ((String, String) -> Unit)? = null,
                           val beforeActions: ((String, String) -> Unit)? = null,
                           val aftertActions: ((String, String) -> Unit)? = null) :
            BaseAnnotatedMiddleware<String>() {

        @BeforeAction("valid")
        fun beforeAction(state: String, action: Action<*>) {
            before?.invoke(state, action.name)
        }

        @AfterAction("valid")
        fun afterAction(state: String, action: Action<*>) {
            aftert?.invoke(state, action.name)
        }

        @BeforeActions
        fun beforeActions(state: String, action: Action<*>) {
            beforeActions?.invoke(state, action.name)
        }

        @AfterActions
        fun afterActions(state: String, action: Action<*>) {
            aftertActions?.invoke(state, action.name)
        }

    }
}