package com.github.raulccabreu.redukt.middlewares

import com.github.raulccabreu.redukt.actions.Action
import java.lang.reflect.Method
import java.security.InvalidParameterException
import java.util.concurrent.ConcurrentHashMap

abstract class BaseAnnotatedMiddleware<T> : Middleware<T> {

    private val befores: ConcurrentHashMap<String, Method> = ConcurrentHashMap()
    private val afters: ConcurrentHashMap<String, Method> = ConcurrentHashMap()
    private val interceptBefores = mutableSetOf<Method>()
    private val interceptAfters = mutableSetOf<Method>()

    init {
        javaClass.methods
                .filter {
                    it.isAnnotationPresent(BeforeAction::class.java) ||
                    it.isAnnotationPresent(AfterAction::class.java) ||
                    it.isAnnotationPresent(BeforeActions::class.java) ||
                    it.isAnnotationPresent(AfterActions::class.java)
                }.forEach {
                    if (it.isAnnotationPresent(BeforeAction::class.java))
                        addBeforeMiddleware(it)
                    if (it.isAnnotationPresent(AfterAction::class.java))
                        addAfterMiddleware(it)
                    if (it.isAnnotationPresent(BeforeActions::class.java))
                        addBeforeActions(it)
                    if (it.isAnnotationPresent(AfterActions::class.java))
                        addAfterActions(it)
                }
    }

    private fun addBeforeMiddleware(method: Method) {
        val annotation = method.getAnnotation(BeforeAction::class.java) as BeforeAction

        verifyActionIsBlank (annotation.action)
        verifyNumberOfArguments(method)

        befores.put(annotation.action, method)
    }

    private fun addAfterMiddleware(method: Method) {
        val annotation = method.getAnnotation(AfterAction::class.java) as AfterAction

        verifyActionIsBlank (annotation.action)
        verifyNumberOfArguments(method)

        afters.put(annotation.action, method)
    }

    private fun addBeforeActions(method: Method) {
        verifyNumberOfArguments(method)

        interceptBefores.add(method)
    }

    private fun addAfterActions(method: Method) {
        verifyNumberOfArguments(method)

        interceptAfters.add(method)
    }

    private fun verifyActionIsBlank(action: String) {
        if (action.isBlank())
            throw IllegalArgumentException("Action cannot be empty")
    }

    private fun verifyNumberOfArguments(method: Method) {
        if (method.parameterTypes.size != 2)
            throw InvalidParameterException(
                    "The method ${method.name} must accept: State and Action")
    }

    final override fun before(state: T, action: Action<*>) {
        if (!canExecute(state)) return

        befores[action.name]?.invoke(this, state, action)
        interceptBefores.forEach { it.invoke(this, state, action) }
    }

    final override fun after(state: T, action: Action<*>) {
        if (!canExecute(state)) return

        afters[action.name]?.invoke(this, state, action)
        interceptAfters.forEach { it.invoke(this, state, action) }
    }

    open fun canExecute(state: T) : Boolean = true
}