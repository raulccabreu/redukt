package com.github.raulccabreu.redukt.ui

import com.github.raulccabreu.redukt.Redukt
import junit.framework.Assert
import org.junit.After
import org.junit.Before
import org.junit.Test

class LayoutStateListenerTest {

    lateinit var redukt: Redukt<String>
    lateinit var listener: LayoutStateListener<String>

    @Before
    fun setup() {
        redukt = Redukt("initial")
        listener = object : LayoutStateListener<String> {
            override fun onChanged(state: String) {
                //Nothing
            }

            override fun getRedukt() = redukt
        }
    }

    @After
    fun reset() {
        listener.unregisterStateListener()
        redukt.stop()
    }

    @Test
    fun wheRegisterStateListener() {
        listener.registerStateListener()
        Assert.assertEquals(1, redukt.listeners.size)
        Assert.assertTrue(redukt.listeners.any { it == listener })
    }

    @Test
    fun verifyIfCallOnChangedWhenRegisterListener() {
        listener = object : LayoutStateListener<String> {
            override fun onChanged(state: String) {
                Assert.assertEquals(state, redukt.state)
            }

            override fun getRedukt() = redukt
        }

        listener.registerStateListener()
    }

    @Test
    fun whenUnregisterStateListener() {
        listener.registerStateListener()
        Assert.assertEquals(1, redukt.listeners.size)
        Assert.assertTrue(redukt.listeners.any { it == listener })

        listener.unregisterStateListener()
        Assert.assertEquals(0, redukt.listeners.size)
    }

}