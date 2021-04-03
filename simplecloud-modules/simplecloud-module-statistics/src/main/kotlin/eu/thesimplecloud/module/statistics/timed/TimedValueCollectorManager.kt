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

package eu.thesimplecloud.module.statistics.timed

import com.google.common.collect.Maps
import eu.thesimplecloud.api.utils.time.Timestamp
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.module.statistics.StatisticsModule
import eu.thesimplecloud.module.statistics.timed.collector.ITimedValueCollector
import eu.thesimplecloud.module.statistics.timed.store.ITimedValueStore
import java.util.concurrent.TimeUnit

/**
 * Created by IntelliJ IDEA.
 * Date: 25.12.2020
 * Time: 12:24
 * @author Frederick Baier
 */
class TimedValueCollectorManager {

    private val storeAndCollectorToLastExecutedTime =
            Maps.newConcurrentMap<StoreAndValueCollector<out Any>, Timestamp>()

    fun start() {
        Launcher.instance.scheduler.scheduleAtFixedRate({
            val collectors =
                    storeAndCollectorToLastExecutedTime.filter { it.value.hasTimePassed(it.key.collector.collectInterval()) }.keys
            collectors.forEach {
                it.storeNextValue()
                this.storeAndCollectorToLastExecutedTime[it] = Timestamp()
            }
        }, 0, 1, TimeUnit.SECONDS)
    }

    fun <T : Any> registerValueCollector(collector: ITimedValueCollector<T>, clazz: Class<T>) {
        val store = StatisticsModule.instance.createTimedValueStore<T>(collector.collectionName(), clazz)
        val storeAndValueCollector = StoreAndValueCollector(collector, store)
        this.storeAndCollectorToLastExecutedTime[storeAndValueCollector] = Timestamp(0)
    }


    fun getStoreByCollectionNameUnsafe(name: String): ITimedValueStore<out Any>? {
        return this.storeAndCollectorToLastExecutedTime.keys.firstOrNull { it.collector.collectionName() == name }?.store
    }

    class StoreAndValueCollector<T : Any>(
            val collector: ITimedValueCollector<T>,
            val store: ITimedValueStore<T>
    ) {

        fun storeNextValue() {
            store.store(collector.collectValue())
        }

    }

}