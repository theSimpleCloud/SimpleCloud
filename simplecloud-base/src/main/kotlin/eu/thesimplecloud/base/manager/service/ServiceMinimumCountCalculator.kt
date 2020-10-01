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

package eu.thesimplecloud.base.manager.service

import com.google.common.collect.Maps
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.event.service.CloudServiceInvisibleEvent
import eu.thesimplecloud.api.eventapi.CloudEventHandler
import eu.thesimplecloud.api.eventapi.IListener
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.api.utils.Timestamp
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit

/**
 * Created by IntelliJ IDEA.
 * Date: 27.09.2020
 * Time: 13:32
 * @author Frederick Baier
 */
class ServiceMinimumCountCalculator : IListener {

    private val groupToTimeStamps = Maps.newConcurrentMap<ICloudServiceGroup, MutableList<Timestamp>>()


    init {
        CloudAPI.instance.getEventManager().registerListener(CloudAPI.instance.getThisSidesCloudModule(), this)
    }

    @CloudEventHandler
    fun onServiceUpdate(event: CloudServiceInvisibleEvent) {
        val cloudService = event.cloudService
        val timestamps = groupToTimeStamps.getOrPut(cloudService.getServiceGroup()) { CopyOnWriteArrayList() }
        timestamps.add(Timestamp())

        timestamps.removeIf { it.hasTimePassed(TimeUnit.MINUTES.toMillis(1)) }
    }

    fun getCalculatedMinimumServiceCount(group: ICloudServiceGroup): Int {
        val timestamps = this.groupToTimeStamps[group] ?: return 0
        return timestamps.filter { !it.hasTimePassed(TimeUnit.MINUTES.toMillis(1)) }.size + 1
    }


}