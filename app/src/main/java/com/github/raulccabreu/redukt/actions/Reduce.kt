package com.github.raulccabreu.redukt.actions

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Reduce(val action: String)