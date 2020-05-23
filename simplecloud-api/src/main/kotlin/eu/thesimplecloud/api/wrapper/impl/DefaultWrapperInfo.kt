package eu.thesimplecloud.api.wrapper.impl

import eu.thesimplecloud.api.wrapper.IWritableWrapperInfo
import eu.thesimplecloud.clientserverapi.lib.json.GsonCreator
import eu.thesimplecloud.clientserverapi.lib.json.GsonExclude
import eu.thesimplecloud.clientserverapi.lib.json.JsonData

data class DefaultWrapperInfo(
        private val name: String,
        private val host: String,
        private var maxSimultaneouslyStartingServices: Int,
        private var maxMemory: Int
) : IWritableWrapperInfo {

    @GsonExclude
    private var authenticated = false

    @GsonExclude
    private var usedMemory: Int = 0

    @GsonExclude
    private var templatesReceived = false

    @GsonExclude
    private var currentlyStartingServices = 0

    override fun setUsedMemory(memory: Int) {
        this.usedMemory = memory
    }

    override fun setMaxSimultaneouslyStartingServices(amount: Int) {
        this.maxSimultaneouslyStartingServices = amount
    }

    override fun setMaxMemory(memory: Int) {
        this.maxMemory = memory
    }

    override fun setAuthenticated(boolean: Boolean) {
        this.authenticated = boolean
    }

    override fun getName(): String = this.name

    override fun getHost(): String = this.host

    override fun getMaxSimultaneouslyStartingServices(): Int = this.maxSimultaneouslyStartingServices

    override fun getUsedMemory(): Int = this.usedMemory

    override fun getMaxMemory(): Int = this.maxMemory

    override fun isAuthenticated(): Boolean = this.authenticated

    override fun hasTemplatesReceived(): Boolean = this.templatesReceived

    override fun getCurrentlyStartingServices(): Int = this.currentlyStartingServices

    override fun setTemplatesReceived(boolean: Boolean) {
        this.templatesReceived = boolean
    }

    override fun setCurrentlyStartingServices(startingServices: Int) {
        this.currentlyStartingServices = startingServices
    }

    override fun toString(): String {
        return JsonData.fromObject(this, GsonCreator().create()).getAsJsonString()
    }

}