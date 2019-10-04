package eu.thesimplecloud.launcher

import eu.thesimplecloud.launcher.application.ICloudApplication
import eu.thesimplecloud.launcher.console.ConsoleManager
import eu.thesimplecloud.launcher.console.command.CommandManager
import eu.thesimplecloud.launcher.console.setup.SetupManager
import eu.thesimplecloud.launcher.logger.LoggerProvider
import com.google.common.collect.ComparisonChain.start
import com.mongodb.BasicDBObject
import com.mongodb.MongoClient
import de.flapdoodle.embed.mongo.MongodExecutable
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.runtime.Network
import org.reflections.util.ConfigurationBuilder.build
import java.util.*



/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 06.09.2019
 * Time: 21:31
 */
class Launcher(val args: Array<String>): ICloudApplication {

    companion object {
        lateinit var instance: Launcher
    }

    private var running = true
    val logger = LoggerProvider(this)
    val commandManager = CommandManager(this)
    val setupManager = SetupManager(this)
    val consoleManager = ConsoleManager(this, commandManager)

    override fun start() {
        instance = this
        System.setProperty("user.language", "en")

        commandManager.registerAllCommands("eu.thesimplecloud.launcher.commands")
        consoleManager.start()

        if (args.size == 0) {
            //setupManager.startSetup(eu.thesimplecloud.launcher.setups.StartSetup())
        }

        logger.updatePromt(false)

        /*val starter = MongodStarter.getDefaultInstance()

        val bindIp = "localhost"
        val port = 12345
        val mongodConfig = MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(Net(bindIp, port, Network.localhostIsIPv6()))
                .build()



        var mongodExecutable: MongodExecutable? = null
        try {
            mongodExecutable = starter.prepare(mongodConfig)
            val mongod = mongodExecutable!!.start()

            val mongo = MongoClient(bindIp, port)
            val db = mongo.getDB("tsradiobots")
            val col = db.createCollection("testCol", BasicDBObject())
            col.save(BasicDBObject("testDoc", Date()))

        } finally {
            if (mongodExecutable != null)
                mongodExecutable!!.stop()
        }*/
    }

    override fun shutdown() {
        running = false
    }

    override fun getApplicationName(): String = "Launcher"


    override fun isRunning(): Boolean = running


}