package eu.thesimplecloud.lib.directorypaths

class DirectoryPaths(
        val groupsPath: String = "groups/",
        val proxyGroupsPath: String = groupsPath + "proxies/",
        val serverGroupsPath: String = groupsPath + "servers/",
        val lobbyGroupsPath: String = groupsPath + "lobbies/",
        val storagePath: String = "storage/",
        val templatesPath: String = "templates/",
        val tempPath: String = "tmp/",
        val staticPath: String = "static/",
        val minecraftJarsPath: String = storagePath + "minecraftJars/",
        val languagesPath: String = storagePath + "languages/",
        val modulesPath: String = "modules/",
        val wrappersPath: String = storagePath + "wrappers/"
        ) {
    companion object {
        lateinit var paths: DirectoryPaths
    }
}