package com.github.raulccabreu.redukt

import com.github.raulccabreu.redukt.actions.Action
import com.github.raulccabreu.redukt.reducers.Reducer

class Redukt<T: Any>(state: T) {
    var state = state
        private set
    val reducers = mutableSetOf<Reducer<T>>()

    fun dispatch(action: Action<*>) {
        // Naive non async approach to reduce the state
        var tempState = state
        reducers.forEach { tempState = it.reduce(tempState, action) }
        state = tempState
    }
}
