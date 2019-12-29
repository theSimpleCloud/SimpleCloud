package eu.thesimplecloud.base

import de.flapdoodle.embed.mongo.Command
import de.flapdoodle.embed.mongo.MongoShellStarter
import de.flapdoodle.embed.mongo.MongodExecutable
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.*
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.config.io.ProcessOutput
import de.flapdoodle.embed.process.io.progress.IProgressListener
import de.flapdoodle.embed.process.runtime.Network
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.launcher.startup.Launcher
import org.apache.commons.lang3.StringUtils
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.concurrent.thread


class MongoController(val mongoBuilder: MongoBuilder) {

    private var mongodExecutable: MongodExecutable? = null
    private val roles = arrayOf("\"readWrite\"", "{\"db\":\"local\",\"role\":\"read\"}")

    val startedPromise = CommunicationPromise<Unit>(enableTimeout = false)
    private var lastPercent = -1

    private var mongodConfig: IMongodConfig? = null
    var runtimeConfig = RuntimeConfigBuilder()
            .defaults(Command.MongoD)
            .processOutput(ProcessOutput.getDefaultInstanceSilent())
            .artifactStore(ArtifactStoreBuilder()
                    .defaults(Command.MongoD)
                    .download(DownloadConfigBuilder()
                            .defaultsForCommand(Command.MongoD)
                            .progressListener(object : IProgressListener {

                                override fun start(label: String) {
                                    Launcher.instance.logger.console("Starting: $label")
                                }

                                override fun progress(label: String, percent: Int) {
                                    if (percent != lastPercent && percent % 10 == 0)
                                        Launcher.instance.logger.console("$label $percent%")
                                    lastPercent = percent
                                }

                                override fun info(label: String, message: String) {
                                    Launcher.instance.logger.info(label)
                                }

                                override fun done(label: String) {
                                    Launcher.instance.logger.success("Finished: $label")
                                }


                            })))
            .build()

    fun start() {
        thread { start0() }
    }

    private fun start0() {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1\$tT] [%4$-7s] %5\$s %n")
        val mongoLogger = Logger.getLogger("org.mongodb.driver")
        mongoLogger.level = Level.OFF

        val starter = MongodStarter.getInstance(runtimeConfig)
        val directory = File(mongoBuilder.directory)
        val directoryExistOnStart = directory.exists()
        val replication = Storage(mongoBuilder.directory, null, 0)

        val cmdOptions = MongoCmdOptionsBuilder().enableAuth(true).verbose(false).build()
        mongodConfig = MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(Net(mongoBuilder.host, mongoBuilder.port, Network.localhostIsIPv6()))
                .replication(replication)
                .cmdOptions(cmdOptions)
                .build()

        try {
            this.mongodExecutable = starter.prepare(mongodConfig)
            val mongod = mongodExecutable!!.start()
            if (!directoryExistOnStart) {
                addAdmin()
                addUser(mongoBuilder.userDatabase, mongoBuilder.userName, mongoBuilder.userPassword)
            }
            startedPromise.setSuccess(Unit)
        } finally {
            //mongodExecutable?.stop()
        }
    }

    fun stop(): ICommunicationPromise<Unit> {
        return this.startedPromise.then { this.mongodExecutable?.stop() ?: Unit }
    }

    private fun addAdmin() {
        val scriptText = StringUtils.join(
                String.format("db.createUser(" +
                        "{\"user\":\"%s\",\"pwd\":\"%s\"," +
                        "\"roles\":[" +
                        "\"root\"," +
                        "{\"role\":\"userAdmin\",\"db\":\"admin\"}," +
                        "{\"role\":\"dbAdmin\",\"db\":\"admin\"}," +
                        "{\"role\":\"userAdminAnyDatabase\",\"db\":\"admin\"}," +
                        "{\"role\":\"dbAdminAnyDatabase\",\"db\":\"admin\"}," +
                        "{\"role\":\"clusterAdmin\",\"db\":\"admin\"}," +
                        "{\"role\":\"dbOwner\",\"db\":\"admin\"}," +
                        "]});\n",
                        mongoBuilder.adminUsername, mongoBuilder.adminPassword))
        executeShellCommand(scriptText, "admin", null, null)
    }

    fun addUser(databaseName: String, userName: String, password: String) {
        val scriptText: String = StringUtils.join(String.format("db = db.getSiblingDB('%s'); " +
                "db.createUser({\"user\":\"%s\",\"pwd\":\"%s\",\"roles\":[%s]});\n" +
                "db.getUser('%s');",
                databaseName, userName, password, StringUtils.join(roles, ","), userName), "")
        executeShellCommand(scriptText, "admin", mongoBuilder.adminUsername, mongoBuilder.adminPassword)
    }

    private fun executeShellCommand(text: String, databaseName: String, userName: String?, password: String?) {
        val runtimeConfig = RuntimeConfigBuilder()
                .defaults(Command.Mongo)
                .processOutput(ProcessOutput.getDefaultInstanceSilent())
                .build()
        val shellStarter = MongoShellStarter.getInstance(runtimeConfig)
        val tmpScriptFile = writeTmpScriptFile(text)
        val builder = MongoShellConfigBuilder()
        builder.dbName(databaseName)
        if (userName != null) builder.username(userName)
        if (password != null) builder.password(password)
        shellStarter.prepare(builder
                .scriptName(tmpScriptFile.absolutePath)
                .version(mongodConfig!!.version())
                .net(mongodConfig!!.net())
                .build()).start().waitFor()

    }

    @Throws(IOException::class)
    private fun writeTmpScriptFile(scriptText: String): File {
        val scriptFile: File = File.createTempFile("tempfile", ".js")
        scriptFile.deleteOnExit()
        val bw = BufferedWriter(FileWriter(scriptFile))
        bw.write(scriptText)
        bw.close()
        return scriptFile
    }

}

fun main() {
    MongoController(MongoBuilder()
            .setHost("localhost")
            .setPort(45678)
            .setAdminUserName("adminUsername")
            .setAdminPassword("adminPassword")
            .setDatabase("cloud")
            .setDirectory(".mongo")
            .setUserName("simplecloud")
            .setUserPassword("cloudpassword"))
            .start()
}