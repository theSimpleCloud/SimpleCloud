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

package eu.thesimplecloud.module.statistics.timed.store

import eu.thesimplecloud.module.statistics.timed.TimedValue

/**
 * Created by IntelliJ IDEA.
 * Date: 25.12.2020
 * Time: 12:34
 * @author Frederick Baier
 */
interface ITimedValueStore<T : Any> {

    fun getCollectionName(): String

    fun store(timedValue: TimedValue<T>)

    fun getAll(): List<TimedValue<T>>

    fun get(fromTimeStamp: Long, toTimeStamp: Long): List<TimedValue<T>>

    fun get(fromTimeStamp: Long, toTimeStamp: Long, resolution: Long): List<TimedValue<T>> {
        val list = get(fromTimeStamp, toTimeStamp)
        if (list.isEmpty()) return list
        var lastTimeStamp = list.first().getTimeStamp()
        val returnList = mutableListOf(list.first())

        list.drop(1).forEach {
            if (lastTimeStamp + resolution <= it.getTimeStamp()) {
                lastTimeStamp = it.getTimeStamp()
                returnList.add(it)
            }
        }
        return returnList
    }

}