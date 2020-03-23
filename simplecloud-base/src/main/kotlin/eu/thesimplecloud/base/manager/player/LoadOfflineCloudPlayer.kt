package eu.thesimplecloud.base.manager.player

import com.fasterxml.jackson.annotation.JsonIgnore
import eu.thesimplecloud.api.player.connection.DefaultPlayerConnection
import eu.thesimplecloud.api.property.Property
import org.bson.Document
import java.util.*
import kotlin.collections.HashMap

open class LoadOfflineCloudPlayer(
        val name: String,
        val uniqueId: UUID,
        val firstLogin: Long,
        val lastLogin: Long,
        val onlineTime: Long,
        val lastPlayerConnection: DefaultPlayerConnection,
        var propertyMap: Document
)