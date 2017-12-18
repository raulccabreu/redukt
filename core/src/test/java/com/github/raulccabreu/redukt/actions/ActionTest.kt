package com.github.raulccabreu.redukt.actions

import org.junit.Assert
import org.junit.Test

class ActionTest {

    @Test
    fun whenCreateActionWithoutPayload() {
        val action = Action<Any?>("TEST")

        Assert.assertNull(action.payload)
        Assert.assertEquals("TEST", action.name)
    }

    @Test
    fun whenCreateActionWithStringPayload() {
        val action = Action("TEST", "payload")

        Assert.assertEquals("payload", action.payload)
        Assert.assertEquals("TEST", action.name)
    }
}