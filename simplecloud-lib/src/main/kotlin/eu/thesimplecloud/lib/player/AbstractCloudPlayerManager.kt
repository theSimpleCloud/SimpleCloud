package eu.thesimplecloud.lib.player


import kotlin.collections.ArrayList

abstract class AbstractCloudPlayerManager : ICloudPlayerManager {

    private val cachedPlayers = ArrayList<ICloudPlayer>()

    override fun updateCloudPlayer(cloudPlayer: ICloudPlayer) {
        val cachedCloudPlayer = getCachedCloudPlayer(cloudPlayer.getUniqueId())
        if (cachedCloudPlayer == null) {
            cachedPlayers.add(cloudPlayer)
            return
        }
        cachedCloudPlayer as CloudPlayer
        cachedCloudPlayer.setConnectedProxyName(cloudPlayer.getConnectedProxyName())
        cachedCloudPlayer.setConnectedServerName(cloudPlayer.getConnectedServerName())
    }

    override fun removeCloudPlayer(cloudPlayer: ICloudPlayer) {
        this.cachedPlayers.removeIf { it.getUniqueId() == cloudPlayer.getUniqueId() }
    }

    override fun getAllCachedCloudPlayers(): List<ICloudPlayer> = this.cachedPlayers



}