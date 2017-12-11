package com.github.raulccabreu.redukt

import com.github.raulccabreu.redukt.reducers.Reducer

class Redukt<T: Any>(state: T) {
    var state = state
        private set
    val reducers = mutableSetOf<Reducer<T>>()
}
