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
        javaClass.methods.filter {
                    it.isAnnotationPresent(BeforeAction::class.java) ||
                    it.isAnnotationPresent(AfterAction::class.java) ||
                    it.isAnnotationPresent(BeforeActions::class.java) ||
                    it.isAnnotationPresent(AfterActions::class.java)
                }.forEach {
                    add(it)
                }
    }

    private fun add(method: Method) {
        if (method.isAnnotationPresent(BeforeAction::class.java))
            addBeforeAction(method)
        if (method.isAnnotationPresent(AfterAction::class.java))
            addAfterAction(method)
        if (method.isAnnotationPresent(BeforeActions::class.java))
            addBeforeActions(method)
        if (method.isAnnotationPresent(AfterActions::class.java))
            addAfterActions(method)
    }

    private fun addBeforeAction(method: Method) {
        val annotation = method.getAnnotation(BeforeAction::class.java) as BeforeAction

        verifyActionIsBlank (annotation.action)
        verifyNumberOfArguments(method)

        befores.put(annotation.action, method)
    }

    private fun addAfterAction(method: Method) {
        val annotation = method.getAnnotation(AfterAction::class.java) as AfterAction

        verifyActionIsBlank (annotation.action)
        verifyNumberOfArguments(method)

        afters.put(annotation.action, method)
    }

    private fun addBeforeActions(method: Method) {
        val annotation = method.getAnnotation(BeforeActions::class.java) as BeforeActions

        verifyNumberOfArguments(method)

        if (annotation.filter.isEmpty())
            interceptBefores.add(method)
        else
            annotation.filter.forEach {
                befores.put(it, method)
            }
    }

    private fun addAfterActions(method: Method) {
        val annotation = method.getAnnotation(AfterActions::class.java) as AfterActions

        verifyNumberOfArguments(method)

        if (annotation.filter.isEmpty())
            interceptAfters.add(method)
        else
            annotation.filter.forEach {
                afters.put(it, method)
            }
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