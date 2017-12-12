package com.github.raulccabreu.redukt.reducers

import com.github.raulccabreu.redukt.actions.Action
import com.github.raulccabreu.redukt.actions.ReducerAction
import java.security.InvalidParameterException
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap


abstract class BaseAnnotatedReducer<T> : Reducer<T> {

    private val actions: ConcurrentHashMap<String, Method> = ConcurrentHashMap()

    init {
        javaClass.methods
                .filter { it.isAnnotationPresent(ReducerAction::class.java) }
                .forEach {
                    val annotation = it.getAnnotation(ReducerAction::class.java) as ReducerAction

                    if (annotation.action.isBlank())
                        throw IllegalArgumentException("ReducerAction action cannot be empty")

                    if (it.parameterTypes.size != 2)
                        throw InvalidParameterException(
                                "Bound method ${it.name} must accept: State and Object arguments")

                    //TODO implement return type verification
//                    if (it.returnType is T)
//                        throw InvalidParameterException("Bound method ${it.name} must return an instance of the State")

                    actions.put(annotation.action, it)
                }
    }

    override fun reduce(state: T, action: Action<*>): T {
        val method = actions[action.name]
        method?.let { return it.invoke(this, state, action.payload) as T }
        return state
    }

}