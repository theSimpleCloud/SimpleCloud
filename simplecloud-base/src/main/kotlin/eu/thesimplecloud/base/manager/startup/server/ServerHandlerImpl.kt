package eu.thesimplecloud.base.manager.startup.server

import eu.thesimplecloud.api.screen.ICommandExecutable
import eu.thesimplecloud.clientserverapi.lib.handler.IServerHandler
import eu.thesimplecloud.clientserverapi.server.NettyServer
import eu.thesimplecloud.launcher.extension.sendMessage
import eu.thesimplecloud.launcher.startup.Launcher

class ServerHandlerImpl : IServerHandler<ICommandExecutable> {
    override fun onServerShutdown(nettyServer: NettyServer<ICommandExecutable>) {
        Launcher.instance.consoleSender.sendMessage("manager.server.stopped", "A server is now stopped.")
    }

    override fun onServerStartException(nettyServer: NettyServer<ICommandExecutable>, ex: Throwable) {
        Launcher.instance.logger.severe("Server start failed.")
        Launcher.instance.logger.exception(ex)
    }

    override fun onServerStarted(nettyServer: NettyServer<ICommandExecutable>) {
        Launcher.instance.consoleSender.sendMessage("manager.server.listening", "The cloud is now listening on port %PORT%", nettyServer.port.toString(), "...")
    }
}