package eu.thesimplecloud.base.manager.startup.server

import eu.thesimplecloud.clientserverapi.lib.handler.IServerHandler
import eu.thesimplecloud.clientserverapi.server.NettyServer
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.lib.screen.ICommandExecutable

class ServerHandlerImpl : IServerHandler<ICommandExecutable> {
    override fun onServerShutdown(nettyServer: NettyServer<ICommandExecutable>) {
        Launcher.instance.consoleSender.sendMessage("manager.server.stopped", "A server is now stopped.")
    }

    override fun onServerStartException(nettyServer: NettyServer<ICommandExecutable>, ex: Throwable) {
        Launcher.instance.logger.severe("Server start failed.")
        Launcher.instance.logger.exception(ex)
    }

    override fun onServerStarted(nettyServer: NettyServer<ICommandExecutable>) {
        Launcher.instance.consoleSender.sendMessage("manager.server.listening", "A server is now listening on port %PORT%", nettyServer.port.toString(), "...")
    }
}