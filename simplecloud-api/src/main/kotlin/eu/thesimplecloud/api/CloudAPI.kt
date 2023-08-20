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

package eu.thesimplecloud.api

import eu.thesimplecloud.api.cachelist.manager.CacheListManager
import eu.thesimplecloud.api.cachelist.manager.ICacheListManager
import eu.thesimplecloud.api.message.IMessageChannelManager
import eu.thesimplecloud.api.message.MessageChannelManager
import eu.thesimplecloud.api.sync.`object`.GlobalPropertyHolder
import eu.thesimplecloud.api.sync.`object`.IGlobalPropertyHolder
import eu.thesimplecloud.api.template.ITemplateManager
import eu.thesimplecloud.api.template.impl.DefaultTemplateManager
import eu.thesimplecloud.api.wrapper.IWrapperManager
import eu.thesimplecloud.api.wrapper.impl.DefaultWrapperManager

abstract class CloudAPI : ICloudAPI {

    private val wrapperManager: IWrapperManager = DefaultWrapperManager()
    private val templateManager: ITemplateManager = DefaultTemplateManager()
    private val messageChannelManager: IMessageChannelManager = MessageChannelManager()
    private val cacheListManager: ICacheListManager = CacheListManager()
    private val globalPropertyHolder: IGlobalPropertyHolder = GlobalPropertyHolder()

    init {
        instance = this
    }


    override fun getWrapperManager(): IWrapperManager = this.wrapperManager

    override fun getTemplateManager(): ITemplateManager = this.templateManager

    override fun getMessageChannelManager(): IMessageChannelManager = this.messageChannelManager

    override fun getCacheListManager(): ICacheListManager = this.cacheListManager

    override fun getGlobalPropertyHolder(): IGlobalPropertyHolder = this.globalPropertyHolder

    companion object {
        @JvmStatic
        lateinit var instance: ICloudAPI
            private set

        fun isAvailable(): Boolean {
            return runCatching { CloudAPI.instance.getEventManager() }.isSuccess
        }
    }
}