package com.github.raulccabreu.redukt.middlewares

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class AfterActions(val filter: Array<String> = [])