package eu.thesimplecloud.api.eventapi

import eu.thesimplecloud.api.external.ICloudModule
import org.junit.Assert
import org.junit.Test

class EventApiTest {

    val eventManager = EventManager()
    val cloudModule = object: ICloudModule {
        override fun onEnable() {
        }

        override fun onDisable() {
        }

    }



    @Test
    fun testListenerCall(){
        val testListener = TestListener()
        eventManager.registerListener(cloudModule, testListener)
        val testEvent = TestEvent("test123")
        eventManager.call(testEvent)
        Assert.assertEquals("test123", testListener.testString)
    }

    @Test
    fun testUnregister(){
        val testListener = TestListener()
        eventManager.registerListener(cloudModule, testListener)
        eventManager.unregisterListener(testListener)
        val testEvent = TestEvent("12test")
        eventManager.call(testEvent)
        Assert.assertEquals(null, testListener.testString)
    }

    @Test
    fun testUnregisterAll(){
        val testListener = TestListener()
        val testEvent = TestEvent("12test")
        eventManager.registerListener(cloudModule, testListener)
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