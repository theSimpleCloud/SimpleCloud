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

package eu.thesimplecloud.plugin.startup

import eu.thesimplecloud.api.service.impl.DefaultCloudService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by IntelliJ IDEA.
 * Date: 26.09.2020
 * Time: 13:33
 * @author Frederick Baier
 */
class UsedMemoryUpdater {

    fun startUpdater() {
        GlobalScope.launch {
            val thisService = CloudPlugin.instance.thisService() as DefaultCloudService
            while (true) {
                delay(2000)
                val usedMemory = getUsedMemoryInMB(Runtime.getRuntime())
                if (thisService.getUsedMemory() != usedMemory) {
                    thisService.setUsedMemory(usedMemory)
                    thisService.update()
                }
            }
        }
    }

    private fun getUsedMemoryInMB(runtime: Runtime): Int {
        val totalMemory = runtime.totalMemory()
        val freeMemory = runtime.freeMemory()
        val usedMemoryInMB = (totalMemory - freeMemory).toDouble() / (1024 * 1024).toDouble()
        return usedMemoryInMB.toInt()
    }


}