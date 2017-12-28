package com.github.raulccabreu.redukt.middlewares

import com.github.raulccabreu.redukt.actions.Action
import java.lang.reflect.Method
import java.security.InvalidParameterException
import java.util.concurrent.ConcurrentHashMap

abstract class BaseAnnotatedMiddleware<T> : Middleware<T> {

    private val beforeAction = BeforeAction::class.java
    private val afterAction = AfterAction::class.java
    private val beforeActions = BeforeActions::class.java
    private val afterActions = AfterActions::class.java

    private val befores: ConcurrentHashMap<String, Method> = ConcurrentHashMap()
    private val afters: ConcurrentHashMap<String, Method> = ConcurrentHashMap()
    private val interceptBefores = mutableSetOf<Method>()
    private val interceptAfters = mutableSetOf<Method>()

    init {
        javaClass.methods.filter {
                    it.isAnnotationPresent(beforeAction) ||
                    it.isAnnotationPresent(afterAction) ||
                    it.isAnnotationPresent(beforeActions) ||
                    it.isAnnotationPresent(afterActions)
                }.forEach {
                    add(it)
                }
    }

    private fun add(method: Method) {
        verifyNumberOfArguments(method)

        if (method.isAnnotationPresent(beforeAction))
            addAction(method.getAnnotation(beforeAction).action, method, befores)
        if (method.isAnnotationPresent(afterAction))
            addAction(method.getAnnotation(afterAction).action, method, afters)
        if (method.isAnnotationPresent(beforeActions))
            filtering(method.getAnnotation(beforeActions).filter, method, interceptBefores, befores)
        if (method.isAnnotationPresent(afterActions))
            filtering(method.getAnnotation(afterActions).filter, method, interceptAfters, afters)
    }

    private fun addAction(action: String, method: Method,
                                methodsMap: ConcurrentHashMap<String, Method>) {
        verifyActionIsBlank (action)
        methodsMap.put(action, method)
    }

    private fun filtering(filters: Array<String>, method: Method,
                          methods: MutableSet<Method>,
                          mapToMethod: ConcurrentHashMap<String, Method>) {
        if (filters.isEmpty())
            methods.add(method)
        else
            filters.forEach { addAction(it, method, mapToMethod) }
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