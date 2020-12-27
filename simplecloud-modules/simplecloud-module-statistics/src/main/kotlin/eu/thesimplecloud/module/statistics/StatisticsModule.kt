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

package eu.thesimplecloud.module.statistics

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.external.ICloudModule
import eu.thesimplecloud.base.manager.database.MongoOfflineCloudPlayerHandler
import eu.thesimplecloud.base.manager.database.SQLOfflineCloudPlayerHandler
import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.module.rest.javalin.RestServer
import eu.thesimplecloud.module.statistics.rest.timed.TimedValueController
import eu.thesimplecloud.module.statistics.timed.TimedValueCollectorManager
import eu.thesimplecloud.module.statistics.timed.collector.CPUUsageTimedCollector
import eu.thesimplecloud.module.statistics.timed.collector.MemoryTimedCollector
import eu.thesimplecloud.module.statistics.timed.collector.PlayerCountTimedCollector
import eu.thesimplecloud.module.statistics.timed.listener.CloudServiceStartListener
import eu.thesimplecloud.module.statistics.timed.listener.PlayerConnectListener
import eu.thesimplecloud.module.statistics.timed.store.ITimedValueStore
import eu.thesimplecloud.module.statistics.timed.store.MongoTimedValueStore
import eu.thesimplecloud.module.statistics.timed.store.SQLTimedValueStore

/**
 * Created by IntelliJ IDEA.
 * Date: 26.12.2020
 * Time: 10:38
 * @author Frederick Baier
 */
class StatisticsModule : ICloudModule {

    val timedValueCollectorManager = TimedValueCollectorManager()

    override fun onEnable() {
        instance = this
        CloudAPI.instance.getEventManager().registerListener(this, CloudServiceStartListener())
        CloudAPI.instance.getEventManager().registerListener(this, PlayerConnectListener())

        timedValueCollectorManager.registerValueCollector(CPUUsageTimedCollector(), Double::class.java)
        timedValueCollectorManager.registerValueCollector(MemoryTimedCollector(), Int::class.java)
        timedValueCollectorManager.registerValueCollector(PlayerCountTimedCollector(), Int::class.java)
        timedValueCollectorManager.start()

        RestServer.instance.controllerHandler.registerController(TimedValueController())
    }

    override fun onDisable() {
    }

    companion object {
        lateinit var instance: StatisticsModule
            private set
    }

    fun <T : Any> createTimedValueStore(collectionName: String, clazz: Class<T>): ITimedValueStore<T> {
        val offlineCloudPlayerHandler = Manager.instance.offlineCloudPlayerHandler
        if (offlineCloudPlayerHandler is SQLOfflineCloudPlayerHandler) {
            return SQLTimedValueStore<T>(clazz, collectionName)
        } else if (offlineCloudPlayerHandler is MongoOfflineCloudPlayerHandler) {
            return MongoTimedValueStore<T>(clazz, collectionName, offlineCloudPlayerHandler.database)
        }

        throw IllegalStateException("OfflineCloudPlayerManager was not SQL or MongoDB")
    }

}