package com.github.raulccabreu.redukt.ui

import android.support.v7.app.AppCompatActivity

abstract class ReactiveActivity<T> : AppCompatActivity(), LifecycleLayoutStateListener<T>