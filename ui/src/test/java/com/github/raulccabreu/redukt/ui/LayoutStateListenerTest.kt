package com.github.raulccabreu.redukt.ui

import com.github.raulccabreu.redukt.Redukt
import junit.framework.Assert
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch

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
        val signal = CountDownLatch(1)
        var result: String? = null
        listener = object : LayoutStateListener<String> {
            override fun onChanged(state: String) {
                result = state
                signal.countDown()
            }

            override fun getRedukt() = redukt
        }

        listener.registerStateListener()

        signal.await()
        Assert.assertEquals(result, redukt.state)
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