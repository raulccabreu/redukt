package com.github.raulccabreu.redukt.middlewares

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class BeforeActions(val filter: Array<String> = [])