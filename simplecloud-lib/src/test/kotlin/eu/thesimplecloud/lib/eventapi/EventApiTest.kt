package eu.thesimplecloud.lib.eventapi

import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito

class EventApiTest {

    val eventManager = EventManager()



    @Test
    fun testListenerCall(){
        val testListener = TestListener()
        eventManager.registerListener(testListener)
        val testEvent = TestEvent("test123")
        eventManager.call(testEvent)
        Assert.assertEquals("test123", testListener.testString)
    }

    @Test
    fun testUnregister(){
        val testListener = TestListener()
        eventManager.registerListener(testListener)
        eventManager.unregisterListener(testListener)
        val testEvent = TestEvent("12test")
        eventManager.call(testEvent)
        Assert.assertEquals(null, testListener.testString)
    }

    @Test
    fun testUnregisterAll(){
        val testListener = TestListener()
        val testEvent = TestEvent("12test")
        eventManager.registerListener(testListener)
        eventManager.unregisterAll()
        eventManager.call(testEvent)
        Assert.assertEquals(null, testListener.testString)
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