package com.github.raulccabreu.redukt.actions

open class Action<out T>(val name: String, val payload: T? = null)