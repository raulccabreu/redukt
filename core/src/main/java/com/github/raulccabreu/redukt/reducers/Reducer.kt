package com.github.raulccabreu.redukt.reducers

import com.github.raulccabreu.redukt.actions.Action

interface Reducer<T> {
    fun reduce(state: T, action: Action<*>): T
}
