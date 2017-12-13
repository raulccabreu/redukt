package com.github.raulccabreu.redukt.reducers

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Reduce(val action: String)