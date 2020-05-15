package eu.thesimplecloud.api.player

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.player.text.CloudText
import eu.thesimplecloud.clientserverapi.lib.json.GsonExclude
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.util.concurrent.ConcurrentLinkedQueue

class PlayerMessageQueue(@GsonExclude private val player: ICloudPlayer) {

    private val queue = ConcurrentLinkedQueue<QueuedText>()

    @Volatile
    private var lastMessagePromise: ICommunicationPromise<Unit> = CommunicationPromise.of(Unit)

    @Synchronized
    fun queueMessage(cloudText: CloudText): ICommunicationPromise<Unit> {
        val promise = CommunicationPromise<Unit>(500)
        this.queue.add(QueuedText(cloudText, promise))
        if (this.lastMessagePromise.isDone) {
            sendNextMessage()
        }
        return promise
    }

    private fun sendNextMessage() {
        val queuedText = queue.poll()
        queuedText?.let {
            val promise = CloudAPI.instance.getCloudPlayerManager().sendMessageToPlayer(player, queuedText.cloudText)
            this.lastMessagePromise = promise
            promise.addCompleteListener { sendNextMessage() }
            promise.addCompleteListener { queuedText.promise.trySuccess(Unit) }
        }
    }


    private class QueuedText(val cloudText: CloudText, val promise: ICommunicationPromise<Unit>)

}