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
                    it.isAnnotationPresent(Intercept::class.java)
                }.forEach {
                    if (it.isAnnotationPresent(BeforeAction::class.java))
                        addBeforeMiddleware(it)
                    if (it.isAnnotationPresent(AfterAction::class.java))
                        addAfterMiddleware(it)
                    if (it.isAnnotationPresent(Intercept::class.java))
                        addIntercept(it)
                }
    }

    private fun addBeforeMiddleware(method: Method) {
        val annotation = method.getAnnotation(BeforeAction::class.java) as BeforeAction

        if (annotation.action.isBlank())
            throw IllegalArgumentException("BeforeReduce action cannot be empty")

        if (method.parameterTypes.size != 2)
            throw InvalidParameterException(
                    "The method ${method.name} must accept: State and Action")

        befores.put(annotation.action, method)
    }

    private fun addAfterMiddleware(method: Method) {
        val annotation = method.getAnnotation(AfterAction::class.java) as AfterAction

        if (annotation.action.isBlank())
            throw IllegalArgumentException("BeforeReduce action cannot be empty")

        if (method.parameterTypes.size != 2)
            throw InvalidParameterException(
                    "The method ${method.name} must accept: State and Action")

        afters.put(annotation.action, method)
    }

    private fun addIntercept(method: Method) {
        val annotation = method.getAnnotation(AfterAction::class.java) as Intercept

        if (method.parameterTypes.size != 2)
            throw InvalidParameterException(
                    "The method ${method.name} must accept: State and Action")

        if (annotation.isBefore) interceptBefores.add(method) else interceptAfters
    }

    override fun before(state: T, action: Action<*>) {
        if (!canExecute(state)) return

        befores[action.name]?.invoke(this, state, action)
        interceptBefores.forEach { it.invoke(this, state, action) }
    }

    override fun after(state: T, action: Action<*>) {
        if (!canExecute(state)) return

        afters[action.name]?.invoke(this, state, action)
        interceptAfters.forEach { it.invoke(this, state, action) }
    }

    open fun canExecute(state: T) : Boolean = true
}