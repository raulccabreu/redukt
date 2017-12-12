package com.github.raulccabreu.redukt.middlewares

import com.github.raulccabreu.redukt.actions.Action
import java.lang.reflect.Method
import java.security.InvalidParameterException
import java.util.concurrent.ConcurrentHashMap

abstract class BaseAnnotatedMiddleware<T> : Middleware<T> {

    private val befores: ConcurrentHashMap<String, Method> = ConcurrentHashMap()
    private val afters: ConcurrentHashMap<String, Method> = ConcurrentHashMap()

    init {
        javaClass.methods
                .filter {
                    it.isAnnotationPresent(BeforeAction::class.java) ||
                            it.isAnnotationPresent(AfterAction::class.java)
                }.forEach {
                    when {
                        it.isAnnotationPresent(BeforeAction::class.java) -> addBeforeReduce(it)
                        it.isAnnotationPresent(AfterAction::class.java) -> addAfterReduce(it)
                    }
                }
    }

    private fun addBeforeReduce(method: Method) {
        val annotation = method.getAnnotation(BeforeAction::class.java) as BeforeAction

        if (annotation.action.isBlank())
            throw IllegalArgumentException("BeforeReduce action cannot be empty")

        if (method.parameterTypes.size != 2)
            throw InvalidParameterException(
                    "The method ${method.name} must accept: State and Action")

        befores.put(annotation.action, method)
    }

    private fun addAfterReduce(method: Method) {
        val annotation = method.getAnnotation(BeforeAction::class.java) as BeforeAction

        if (annotation.action.isBlank())
            throw IllegalArgumentException("BeforeReduce action cannot be empty")

        if (method.parameterTypes.size != 2)
            throw InvalidParameterException(
                    "The method ${method.name} must accept: State and Action")

        afters.put(annotation.action, method)
    }

    override fun before(state: T, action: Action<*>) {
        befores[action.name]?.invoke(this, state, action)
    }

    override fun after(state: T, action: Action<*>) {
        afters[action.name]?.invoke(this, state, action)
    }
}