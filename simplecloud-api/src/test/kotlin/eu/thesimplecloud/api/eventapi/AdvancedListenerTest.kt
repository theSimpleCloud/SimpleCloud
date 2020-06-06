/*
 * MIT License
 *
 * Copyright (C) 2020 The SimpleCloud authors
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

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.event.player.CloudPlayerLoginEvent
import eu.thesimplecloud.api.external.ICloudModule
import eu.thesimplecloud.api.listenerextension.cloudListener
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import java.util.*

class AdvancedListenerTest {

    @Before
    fun before() {
        val cloudAPI = spy(EmptyMockCloudAPIImpl::class.java)
        val cloudModule = object : ICloudModule {
            override fun onEnable() {
            }

            override fun onDisable() {
            }
        }
        `when`(cloudAPI.getThisSidesCloudModule()).thenReturn(cloudModule)
        val eventManager = BasicEventManager()
        `when`(cloudAPI.getEventManager()).thenReturn(eventManager)
    }

    @Test
    fun listener_call_test() {
        val runnable = mock(Runnable::class.java)
        val advancedListener = cloudListener<CloudPlayerLoginEvent>()
        advancedListener.unregisterAfterCall()
        advancedListener.addAction { runnable.run() }
        CloudAPI.instance.getEventManager().call(CloudPlayerLoginEvent(UUID.randomUUID(), ""))
        CloudAPI.instance.getEventManager().call(CloudPlayerLoginEvent(UUID.randomUUID(), ""))
        verify(runnable, times(1)).run()
    }

    @Test
    fun listener_unregister_test() {
        val runnable = mock(Runnable::class.java)
        val advancedListener = cloudListener<CloudPlayerLoginEvent>()
        advancedListener.addAction { runnable.run() }
        CloudAPI.instance.getEventManager().call(CloudPlayerLoginEvent(UUID.randomUUID(), ""))
        CloudAPI.instance.getEventManager().call(CloudPlayerLoginEvent(UUID.randomUUID(), ""))
        advancedListener.unregister()
        CloudAPI.instance.getEventManager().call(CloudPlayerLoginEvent(UUID.randomUUID(), ""))
        verify(runnable, times(2)).run()
    }

    @Test
    fun listener_promise_test() {
        val runnable = mock(Runnable::class.java)
        val advancedListener = cloudListener<CloudPlayerLoginEvent>()
        advancedListener.toPromise().addResultListener { runnable.run() }
        CloudAPI.instance.getEventManager().call(CloudPlayerLoginEvent(UUID.randomUUID(), ""))
        CloudAPI.instance.getEventManager().call(CloudPlayerLoginEvent(UUID.randomUUID(), ""))
        Thread.sleep(100)
        verify(runnable, times(1)).run()
    }

    @Test
    fun listener_auto_unregister_test() {
        val runnable = mock(Runnable::class.java)
        val advancedListener = cloudListener<CloudPlayerLoginEvent>(true, 1)
        advancedListener.addAction { runnable.run() }
        CloudAPI.instance.getEventManager().call(CloudPlayerLoginEvent(UUID.randomUUID(), ""))
        Thread.sleep(1024)
        CloudAPI.instance.getEventManager().call(CloudPlayerLoginEvent(UUID.randomUUID(), ""))
        verify(runnable, times(1)).run()
    }

}