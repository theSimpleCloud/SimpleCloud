package eu.thesimplecloud.base.manager.setup.mongo

import eu.thesimplecloud.base.manager.config.MongoConfig
import eu.thesimplecloud.base.manager.config.MongoConfigLoader
import eu.thesimplecloud.launcher.console.setup.ISetup
import eu.thesimplecloud.launcher.console.setup.annotations.SetupCancelled
import eu.thesimplecloud.launcher.console.setup.annotations.SetupQuestion
import eu.thesimplecloud.launcher.startup.Launcher

class MongoDBUseEmbedSetup : ISetup {

    @SetupQuestion(0, "manager.setup.mongodb-embed.question.use", "Do you want to use embed mongodb? (Otherwise a mongodb installation is required) yes/no")
    fun setup(use: Boolean) {
        if (!use) {
            Launcher.instance.setupManager.queueSetup(MongoDBConnectionSetup())
        } else {
            val mongoConfig = MongoConfigLoader().loadConfig()
            MongoConfigLoader().saveConfig(MongoConfig(true, mongoConfig.mongoServerInformation))
        }
    }

    @SetupCancelled
    fun cancel() {
        Launcher.instance.shutdown()
    }

}