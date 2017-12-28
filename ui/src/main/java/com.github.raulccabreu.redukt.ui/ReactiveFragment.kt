package com.github.raulccabreu.redukt.ui

import android.support.v4.app.Fragment

abstract class ReactiveFragment<T> : Fragment(), LifecycleLayoutStateListener<T>