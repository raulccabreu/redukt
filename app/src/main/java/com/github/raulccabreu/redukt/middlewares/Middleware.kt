package com.github.raulccabreu.redukt.middlewares

import com.github.raulccabreu.redukt.actions.Action

interface Middleware<in State> {

    fun before(state: State, action: Action<*>)

    fun after(state: State, action: Action<*>)
}