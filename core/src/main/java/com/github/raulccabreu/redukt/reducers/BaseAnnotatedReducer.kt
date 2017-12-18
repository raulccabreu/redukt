package com.github.raulccabreu.redukt.reducers

import com.github.raulccabreu.redukt.actions.Action
import com.github.raulccabreu.redukt.actions.Reduce
import java.lang.reflect.Method
import java.security.InvalidParameterException
import java.util.concurrent.ConcurrentHashMap


abstract class BaseAnnotatedReducer<T> : Reducer<T> {

    private val actions: ConcurrentHashMap<String, Method> = ConcurrentHashMap()

    init {
        javaClass.methods
                .filter { it.isAnnotationPresent(Reduce::class.java) }
                .forEach {
                    val annotation = it.getAnnotation(Reduce::class.java) as Reduce

                    if (annotation.action.isBlank())
                        throw IllegalArgumentException("ReducerAction action cannot be empty")

                    if (it.parameterTypes.size != 2)
                        throw InvalidParameterException(
                                "The method ${it.name} must accept: State and Object arguments")

                    if(it.returnType == Void.TYPE || it.returnType == Unit::class.java)
                        throw InvalidParameterException(
                                "The method ${it.name} must return an instance of the State")

                    actions.put(annotation.action, it)
                }
    }

    override fun reduce(state: T, action: Action<*>): T {
        val method = actions[action.name]
        method?.let { return it.invoke(this, state, action.payload) as? T ?: state }
        return state
    }

}