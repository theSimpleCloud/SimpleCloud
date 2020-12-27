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

package eu.thesimplecloud.module.statistics.timed.collector

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.module.statistics.timed.TimedValue
import java.util.concurrent.TimeUnit

/**
 * Created by IntelliJ IDEA.
 * Date: 25.12.2020
 * Time: 12:15
 * @author Frederick Baier
 */
class MemoryTimedCollector : ITimedValueCollector<Int> {

    override fun collectValue(): TimedValue<Int> {
        val usedMemory = CloudAPI.instance.getWrapperManager().getAllCachedObjects().sumBy { it.getUsedMemory() }
        return TimedValue(usedMemory)
    }

    override fun collectInterval(): Long {
        return TimeUnit.MINUTES.toMillis(1)
    }

    override fun collectionName(): String {
        return "cloud_stats_memory_usage"
    }

}