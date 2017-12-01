package com.github.raulccabreu.redukt.reducers

import com.github.raulccabreu.redukt.actions.Action

interface Reducer<in T: Any> {
    fun reduce(state: T, action: Action<*>)
}