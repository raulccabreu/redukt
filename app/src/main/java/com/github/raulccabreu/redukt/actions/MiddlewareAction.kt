package com.github.raulccabreu.redukt.actions

class MiddlewareAction<out T>(name: String, payload: T? = null) : Action<T>(name, payload)