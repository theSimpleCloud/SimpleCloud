package eu.thesimplecloud.api.player

import com.fasterxml.jackson.annotation.JsonIgnore
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.utils.Nameable
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.util.*

open class SimpleCloudPlayer(
        private val name: String,
        private val uniqueId: UUID
): Nameable {

    override fun getName(): String {
        return name
    }

    /**
     * Returns the uniqueId of this player.
     */
    fun getUniqueId(): UUID {
        return uniqueId
    }

    /**
     * Returns a promise of the cloud player of this player.
     */
    @JsonIgnore
    fun getCloudPlayer(): ICommunicationPromise<ICloudPlayer> {
        return CloudAPI.instance.getCloudPlayerManager().getCloudPlayer(uniqueId)
    }

}