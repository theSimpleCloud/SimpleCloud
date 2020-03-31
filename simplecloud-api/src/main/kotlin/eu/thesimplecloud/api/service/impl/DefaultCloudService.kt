package eu.thesimplecloud.api.service.impl

import eu.thesimplecloud.api.property.Property
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.service.ServiceState
import eu.thesimplecloud.clientserverapi.lib.json.GsonExclude
import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.util.*
import kotlin.collections.HashMap

data class DefaultCloudService(
        private val groupName: String,
        private val serviceNumber: Int,
        private val uniqueId: UUID,
        private val templateName: String,
        private var wrapperName: String,
        private var port: Int,
        private val maxMemory: Int,
        private var motd: String
) : ICloudService {

    @GsonExclude
    private val startingPromise = CommunicationPromise<Unit>(enableTimeout = false)
    @GsonExclude
    private val connectedPromise = CommunicationPromise<Unit>(enableTimeout = false)
    @GsonExclude
    private val joinablePromise = CommunicationPromise<Unit>(enableTimeout = false)
    @GsonExclude
    private val closedPromise = CommunicationPromise<Unit>(enableTimeout = false)

    private var serviceState = ServiceState.PREPARED
    private var onlinePlayers = 0
    private var authenticated = false
    @GsonExclude
    private var lastUpdate = System.currentTimeMillis()

    var propertyMap = HashMap<String, Property<*>>()

    override fun getGroupName(): String = this.groupName

    override fun getServiceNumber(): Int = this.serviceNumber

    override fun getUniqueId(): UUID = this.uniqueId

    override fun getTemplateName(): String = this.templateName

    override fun getPort(): Int = this.port

    fun setPort(port: Int) {
        this.port = port
    }

    override fun getWrapperName(): String = this.wrapperName

    fun setWrapperName(wrapperName: String) {
        this.wrapperName = wrapperName
    }

    override fun getState(): ServiceState = this.serviceState

    override fun setState(serviceState: ServiceState) {
        this.serviceState = serviceState
    }

    override fun getOnlinePlayers(): Int = this.onlinePlayers

    override fun setOnlinePlayers(amount: Int) {
        this.onlinePlayers = amount
    }

    override fun getMOTD(): String = this.motd

    override fun setMOTD(motd: String) {
        this.motd = motd
    }

    override fun isAuthenticated(): Boolean = this.authenticated

    override fun setAuthenticated(authenticated: Boolean) {
        this.authenticated = authenticated
    }

    override fun getMaxMemory(): Int = this.maxMemory

    override fun getLastUpdate(): Long = this.lastUpdate

    override fun setLastUpdate(timeStamp: Long) {
        this.lastUpdate = timeStamp
    }

    override fun startingPromise(): ICommunicationPromise<Unit> = this.startingPromise

    override fun connectedPromise(): ICommunicationPromise<Unit> = this.connectedPromise

    override fun joinablePromise(): ICommunicationPromise<Unit> = this.joinablePromise

    override fun closedPromise(): ICommunicationPromise<Unit> = this.closedPromise


    override fun toString(): String {
        return JsonData.fromObjectWithGsonExclude(this).getAsJsonString()
    }

    override fun getProperties(): Map<String, Property<*>> = this.propertyMap

    override fun <T : Any> setProperty(name: String, property: Property<T>) {
        this.propertyMap[name] = property
    }


}