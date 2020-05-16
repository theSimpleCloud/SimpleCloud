package eu.thesimplecloud.api.message

import eu.thesimplecloud.api.client.NetworkComponentReference

data class Message(
        val channel: String,
        val className: String,
        val messageString: String,
        val senderReference: NetworkComponentReference,
        val receivers: List<NetworkComponentReference>
)