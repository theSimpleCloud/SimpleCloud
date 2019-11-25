package eu.thesimplecloud.base.manager.mongo

import com.mongodb.BasicDBObject
import com.mongodb.MongoClient
import de.flapdoodle.embed.mongo.Command
import de.flapdoodle.embed.mongo.MongodExecutable
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.*
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.config.io.ProcessOutput
import de.flapdoodle.embed.process.io.progress.IProgressListener
import de.flapdoodle.embed.process.runtime.Network
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger


class MongoTest {

    var runtimeConfig = RuntimeConfigBuilder()
            .defaults(Command.MongoD)
            .processOutput(ProcessOutput.getDefaultInstanceSilent())
            .artifactStore(ArtifactStoreBuilder()
                    .defaults(Command.MongoD)
                    .download(DownloadConfigBuilder()
                            .defaultsForCommand(Command.MongoD)
                            .progressListener(object : IProgressListener {

                                override fun start(label: String) {}

                                override fun progress(label: String, percent: Int) {}

                                override fun info(label: String, message: String) {}

                                override fun done(label: String) {}


                            })))
            .build()

    fun start() {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1\$tT] [%4$-7s] %5\$s %n")
        val mongoLogger = Logger.getLogger("org.mongodb.driver")
        mongoLogger.level = Level.OFF

        val starter = MongodStarter.getInstance(runtimeConfig)

        val bindIp = "localhost"
        val port = 14415
        val mongodConfig = MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(Net(bindIp, port, Network.localhostIsIPv6()))
                .build()

        var mongodExecutable: MongodExecutable? = null
        try {
            mongodExecutable = starter.prepare(mongodConfig)
            val mongod = mongodExecutable!!.start()

            val mongo = MongoClient(bindIp, port)
            val db = mongo.getDB("test")
            val col = db.createCollection("testCol", BasicDBObject())
            col.save(BasicDBObject("testDoc", Date()))

            while (true) {

            }
        } finally {
            //mongodExecutable?.stop()
        }
    }

}