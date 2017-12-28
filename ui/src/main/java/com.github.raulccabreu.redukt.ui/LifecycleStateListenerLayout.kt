package com.github.raulccabreu.redukt.ui

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent

interface LifecycleStateListenerLayout<T> : StateListenerLayout<T>, LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    override fun registerListener() {
        super.registerListener()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    override fun unregisterListener() {
        super.unregisterListener()
    }
}