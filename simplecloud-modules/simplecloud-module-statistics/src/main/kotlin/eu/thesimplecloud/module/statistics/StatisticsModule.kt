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

package eu.thesimplecloud.module.statistics

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.external.ICloudModule
import eu.thesimplecloud.base.manager.database.MongoOfflineCloudPlayerHandler
import eu.thesimplecloud.base.manager.database.SQLOfflineCloudPlayerHandler
import eu.thesimplecloud.base.manager.database.SQLiteOfflineCloudPlayerHandler
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
import eu.thesimplecloud.module.statistics.timed.store.SQLiteTimedValueStore
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by IntelliJ IDEA.
 * Date: 26.12.2020
 * Time: 10:38
 * @author Frederick Baier
 */
class StatisticsModule : ICloudModule {

    private val timedValueCollectorManager = TimedValueCollectorManager()

    private val valueStores = CopyOnWriteArrayList<ITimedValueStore<*>>()

    override fun onEnable() {
        instance = this
        CloudAPI.instance.getEventManager().registerListener(this, CloudServiceStartListener())
        CloudAPI.instance.getEventManager().registerListener(this, PlayerConnectListener())

        CloudAPI.instance.getWrapperManager().getAllCachedObjects().forEach {
            timedValueCollectorManager.registerValueCollector(CPUUsageTimedCollector(it), Float::class.java)
            timedValueCollectorManager.registerValueCollector(MemoryTimedCollector(it), Int::class.java)
        }
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

    fun getValueStoreByName(name: String): ITimedValueStore<*>? {
        return this.valueStores.firstOrNull { it.getCollectionName() == name }
    }

    fun <T : Any> createTimedValueStore(collectionName: String, clazz: Class<T>): ITimedValueStore<T> {
        val offlineCloudPlayerHandler = Manager.instance.offlineCloudPlayerHandler
        if (offlineCloudPlayerHandler is SQLOfflineCloudPlayerHandler) {
            val sqlTimedValueStore = SQLTimedValueStore<T>(clazz, collectionName, offlineCloudPlayerHandler.connection!!)
            valueStores.add(sqlTimedValueStore)
            return sqlTimedValueStore
        } else if (offlineCloudPlayerHandler is MongoOfflineCloudPlayerHandler) {
            val mongoTimedValueStore =
                MongoTimedValueStore<T>(clazz, collectionName, offlineCloudPlayerHandler.database)
            valueStores.add(mongoTimedValueStore)
            return mongoTimedValueStore
        } else if (offlineCloudPlayerHandler is SQLiteOfflineCloudPlayerHandler) {
            val sqlTimedValueStore = SQLiteTimedValueStore<T>(clazz, collectionName, offlineCloudPlayerHandler.connection!!)
            valueStores.add(sqlTimedValueStore)
            return sqlTimedValueStore
        }

        throw IllegalStateException("OfflineCloudPlayerManager was not SQL or MongoDB")
    }

}