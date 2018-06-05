package com.github.raulccabreu.redukt.utils

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ConcurrentLinkedHashMapUtilsTest {

    @Test
    fun createLinkedMapWorks() {
        val map = createLinkedMap<String>()

        assertEquals(1000, map.capacity())
        assertTrue(map is ConcurrentLinkedHashMap<String, String>)
    }
}