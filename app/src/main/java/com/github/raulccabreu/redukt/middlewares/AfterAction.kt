package com.github.raulccabreu.redukt.middlewares

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class AfterAction(val action: String)