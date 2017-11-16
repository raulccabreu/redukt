package com.github.raulccabreu.redukt.actions

data class Action<out T>(val name: String, val payload: T? = null)