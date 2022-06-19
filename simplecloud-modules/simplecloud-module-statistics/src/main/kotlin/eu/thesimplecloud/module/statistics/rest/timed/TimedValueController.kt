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

package eu.thesimplecloud.module.statistics.rest.timed

import eu.thesimplecloud.module.rest.annotation.*
import eu.thesimplecloud.module.rest.controller.IController
import eu.thesimplecloud.module.statistics.StatisticsModule
import eu.thesimplecloud.module.statistics.timed.TimedValue

/**
 * Created by IntelliJ IDEA.
 * Date: 26.12.2020
 * Time: 10:56
 * @author Frederick Baier
 */
@RestController("statistics/timedvalue/")
class TimedValueController : IController {

    /**
     * The parameters [from] and [to] are from negative infinity to 0. 0 is now.
     */
    @RequestMapping(RequestType.GET, "data/:name", "web.statistics.get.data")
    fun handleGet(
        @RequestPathParam("name") name: String,
        @RequestParam("from", true) from: Long,
        @RequestParam("to", true) to: Long,
        @RequestParam("resolution", false) resolution: Long?
    ): List<TimedValue<out Any>> {


        val valueStore = StatisticsModule.instance.getValueStoreByName(name)
            ?: throwNoSuchElement()

        val recalculatedFrom = from + System.currentTimeMillis()
        val recalculatedTo = to + System.currentTimeMillis()

        return if (resolution == null) {
            valueStore.get(recalculatedFrom, recalculatedTo)
        } else {
            valueStore.get(recalculatedFrom, recalculatedTo, resolution)
        }
    }

    @RequestMapping(RequestType.GET, "count/:name", "web.statistics.get.count")
    fun handleGetCount(
        @RequestPathParam("name") name: String
    ): Int {

        val valueStore = StatisticsModule.instance.getValueStoreByName(name)
            ?: throwNoSuchElement()

        return valueStore.count()
    }

}