package com.github.raulccabreu.redukt.actions

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class ReducerAction(val action: String)