package com.github.raulccabreu.redukt.reducers

import com.github.raulccabreu.redukt.actions.Action

interface Reducer<State> {
    fun reduce(state: State, action: Action<*>): State
}
