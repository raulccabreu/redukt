package com.github.raulccabreu.redukt.reducers

import com.github.raulccabreu.redukt.actions.Action

interface Reducer<T: Any> {
    fun reduce(state: T, action: Action<*>): T
}
