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

package eu.thesimplecloud.api.wrapper.impl

import eu.thesimplecloud.api.wrapper.IWrapperInfo
import eu.thesimplecloud.api.wrapper.IWrapperInfoUpdater
import eu.thesimplecloud.clientserverapi.lib.json.PacketExclude
import eu.thesimplecloud.jsonlib.GsonCreator
import eu.thesimplecloud.jsonlib.JsonLib
import eu.thesimplecloud.jsonlib.JsonLibExclude

data class DefaultWrapperInfo(
    private val name: String,
    private val host: String,
    @Volatile private var maxSimultaneouslyStartingServices: Int,
    @Volatile private var maxMemory: Int
) : IWrapperInfo {

    @JsonLibExclude
    @PacketExclude
    @Volatile
    private var wrapperUpdater: DefaultWrapperInfoUpdater? = DefaultWrapperInfoUpdater(this)

    @JsonLibExclude
    @Volatile
    private var authenticated = false

    @JsonLibExclude
    @Volatile
    private var usedMemory: Int = 0

    @JsonLibExclude
    @Volatile
    private var templatesReceived = false

    @JsonLibExclude
    @Volatile
    private var currentlyStartingServices = 0

    @JsonLibExclude
    @Volatile
    private var cpuUsage = 0.0F

    override fun setAuthenticated(authenticated: Boolean) {
        getUpdater().setAuthenticated(authenticated)
    }

    override fun getName(): String = this.name

    override fun getHost(): String = this.host

    override fun getMaxSimultaneouslyStartingServices(): Int = this.maxSimultaneouslyStartingServices

    override fun getUsedMemory(): Int = this.usedMemory

    override fun getMaxMemory(): Int = this.maxMemory

    override fun getCpuUsage(): Float {
        return this.cpuUsage
    }

    override fun isAuthenticated(): Boolean = this.authenticated

    override fun hasTemplatesReceived(): Boolean = this.templatesReceived

    override fun getCurrentlyStartingServices(): Int = this.currentlyStartingServices

    override fun getUpdater(): IWrapperInfoUpdater {
        if (this.wrapperUpdater == null) {
            this.wrapperUpdater = DefaultWrapperInfoUpdater(this)
        }
        return this.wrapperUpdater ?: throw NullPointerException("WrapperUpdater was null")
    }

    override fun applyValuesFromUpdater(updater: IWrapperInfoUpdater) {
        this.cpuUsage = updater.getCpuUsage()
        this.maxMemory = updater.getMaxMemory()
        this.usedMemory = updater.getUsedMemory()

        this.currentlyStartingServices = updater.getCurrentlyStartingServices()
        this.maxSimultaneouslyStartingServices = updater.getMaxSimultaneouslyStartingServices()

        this.templatesReceived = updater.hasTemplatesReceived()
        this.authenticated = updater.isAuthenticated()
    }

    override fun toString(): String {
        val gson = GsonCreator().excludeAnnotations(PacketExclude::class.java).create()
        return JsonLib.fromObject(this, gson).getAsJsonString()
    }

}