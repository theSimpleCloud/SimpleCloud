package eu.thesimplecloud.base.manager.mongo

import de.flapdoodle.embed.mongo.Command
import de.flapdoodle.embed.mongo.MongoShellStarter
import de.flapdoodle.embed.mongo.MongodExecutable
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.*
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.config.io.ProcessOutput
import de.flapdoodle.embed.process.io.IStreamProcessor
import de.flapdoodle.embed.process.io.NamedOutputStreamProcessor
import de.flapdoodle.embed.process.io.Processors
import de.flapdoodle.embed.process.runtime.Network
import org.apache.commons.lang3.StringUtils
import ru.yandex.qatools.embed.service.LogWatchStreamProcessor
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.*
import kotlin.collections.HashSet


class MongoStarter(val host: String, val port: Int, val databaseName: String, val userName: String, val password: String, val adminUserName: String, val adminPassword: String, databaseDirectory: String) {

    val databaseDirectory = File(databaseDirectory)
    val newDatabase = !this.databaseDirectory.exists()


    private val roles = arrayOf("\"readWrite\"", "{\"db\":\"local\",\"role\":\"read\"}")

    private lateinit var mongodConfig: IMongodConfig
    private lateinit var mongodExecutable: MongodExecutable

    fun startMongo() {
        println(databaseDirectory.absolutePath)
        val starter: MongodStarter = MongodStarter.getDefaultInstance()
        val cmdOptions: IMongoCmdOptions = MongoCmdOptionsBuilder().verbose(false).defaultSyncDelay()
                .enableAuth(true).build()
        mongodConfig = MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(Net(host, port, Network.localhostIsIPv6()))
                .replication(Storage(databaseDirectory.path, null, 0))
                .cmdOptions(cmdOptions).build()
        mongodExecutable = starter.prepare(mongodConfig)
        mongodExecutable.start()
        println("newDatabase: $newDatabase")
        if (newDatabase) {
        }
        addAdmin()
        addUser()
    }

    fun stop() {
        mongodExecutable.stop()
    }

    @Throws(IOException::class)
    private fun addAdmin() {
        val scriptText = StringUtils.join(String.format("db.createUser(" +
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
                adminUserName, adminPassword))
        runScriptAndWait(scriptText, MongoEmbeddedService.USER_ADDED_TOKEN, arrayOf("couldn't add user", "failed to load", "login failed"), "admin", null, null)
    }

    @Throws(IOException::class)
    private fun addUser() {
        val scriptText = StringUtils.join(String.format("db = db.getSiblingDB('%s'); " +
                "db.createUser({\"user\":\"%s\",\"pwd\":\"%s\",\"roles\":[%s]});\n" +
                "db.getUser('%s');",
                databaseName, userName, password, StringUtils.join(roles, ","), userName), "")
        runScriptAndWait(scriptText, MongoEmbeddedService.USER_ADDED_TOKEN, arrayOf("already exists", "failed to load", "login failed"), "admin", adminPassword, adminPassword)
    }

    @Throws(IOException::class)
    private fun runScriptAndWait(scriptText: String, token: String, failures: Array<String>?, dbName: String, username: String?, password: String?) {
        println("$scriptText : $token : $failures : $dbName : $username : $password")
        val mongoOutput: IStreamProcessor
        mongoOutput = if (!StringUtils.isEmpty(token)) {
            LogWatchStreamProcessor(String.format(token),
                    if (failures != null) HashSet(Arrays.asList(*failures)) else emptySet(),
                    Processors.silent())
        } else {
            NamedOutputStreamProcessor("[mongo shell output]", Processors.silent())
        }
        val runtimeConfig = RuntimeConfigBuilder()
                .defaults(Command.Mongo)
                .processOutput(ProcessOutput(
                        mongoOutput,
                        Processors.namedConsole("[mongo shell error]"),
                        Processors.console()))
                .build()
        val starter = MongoShellStarter.getInstance(runtimeConfig)
        val scriptFile: File = writeTmpScriptFile(scriptText)
        val builder = MongoShellConfigBuilder()
        if (!StringUtils.isEmpty(dbName)) {
            builder.dbName(dbName)
        }
        if (!StringUtils.isEmpty(username)) {
            builder.username(username)
        }
        if (!StringUtils.isEmpty(password)) {
            builder.password(password)
        }
        starter.prepare(builder
                .scriptName(scriptFile.absolutePath)
                .version(mongodConfig.version())
                .net(mongodConfig.net())
                .build()).start()
        if (mongoOutput is LogWatchStreamProcessor) {
            mongoOutput.waitForResult(MongoEmbeddedService.INIT_TIMEOUT_MS.toLong())
        }
    }

    @Throws(IOException::class)
    private fun writeTmpScriptFile(scriptText: String): File {
        val scriptFile = File.createTempFile("tempfile", ".js")
        scriptFile.deleteOnExit()
        val bw = BufferedWriter(FileWriter(scriptFile))
        bw.write(scriptText)
        bw.close()
        return scriptFile
    }

}

fun main() {
    val mongoStarter = MongoStarter("127.0.0.1", 45678, "cloud", "simplecloud", "cloudpassword", "admin", "admin", "/custom/database")
    mongoStarter.startMongo()
    Thread.sleep(1000 * 30)
    mongoStarter.stop()


}