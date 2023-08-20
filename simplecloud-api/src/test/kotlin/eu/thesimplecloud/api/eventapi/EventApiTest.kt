/*
 * MIT License
 *
 * Copyright (C) 2020-2022 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.api.eventapi

import eu.thesimplecloud.api.external.ICloudModule
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito.*

class EventApiTest {

    val eventManager = BasicEventManager()
    val cloudModule = object : ICloudModule {
        override fun onEnable() {
        }

        override fun onDisable() {
        }

    }


    @Test
    fun testListenerCall() {
        val testListener = TestListener()
        eventManager.registerListener(cloudModule, testListener)
        val testEvent = TestEvent("test123")
        eventManager.call(testEvent)
        Assert.assertEquals("test123", testListener.testString)
    }

    @Test
    fun testUnregister() {
        val testListener = TestListener()
        eventManager.registerListener(cloudModule, testListener)
        eventManager.unregisterListener(testListener)
        val testEvent = TestEvent("12test")
        eventManager.call(testEvent)
        Assert.assertEquals(null, testListener.testString)
    }

    @Test
    fun testUnregisterByModule() {
        val testListener = TestListener()
        eventManager.registerListener(cloudModule, testListener)
        eventManager.unregisterAllListenersByCloudModule(cloudModule)
        val testEvent = TestEvent("12test")
        eventManager.call(testEvent)
        Assert.assertEquals(null, testListener.testString)
    }

    @Test
    fun testUnregisterAll() {
        val testListener = TestListener()
        val testEvent = TestEvent("12test")
        eventManager.registerListener(cloudModule, testListener)
        eventManager.unregisterAll()
        eventManager.call(testEvent)
        Assert.assertEquals(null, testListener.testString)
    }

    @Test
    fun testRegisterSingleEvent() {
        val listenerObj = object : IListener {}
        val eventExecutor = spy(IEventExecutor::class.java)
        eventManager.registerEvent(cloudModule, TestEvent::class.java, listenerObj, eventExecutor)
        val testEvent = TestEvent("")
        eventManager.call(testEvent)
        verify(eventExecutor, times(1)).execute(testEvent)
    }

    class TestEvent(val testString: String) : IEvent

    open class TestListener : IListener {

        var testString: String? = null

        @CloudEventHandler
        fun on(event: TestEvent) {
            testString = event.testString
        }

    }

}