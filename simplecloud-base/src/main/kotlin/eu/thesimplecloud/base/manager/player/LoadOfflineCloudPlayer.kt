package eu.thesimplecloud.base.manager.player

import eu.thesimplecloud.api.player.connection.DefaultPlayerConnection
import org.bson.Document
import java.util.*

open class LoadOfflineCloudPlayer(
        val name: String,
        val uniqueId: UUID,
        val firstLogin: Long,
        val lastLogin: Long,
        val onlineTime: Long,
        val lastPlayerConnection: DefaultPlayerConnection,
        var propertyMap: Document
)