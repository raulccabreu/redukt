package com.github.raulccabreu.redukt.ui

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent

interface LifecycleLayoutStateListener<T> : LayoutStateListener<T>, LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    override fun registerStateListener() {
        super.registerStateListener()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    override fun unregisterStateListener() {
        super.unregisterStateListener()
    }
}