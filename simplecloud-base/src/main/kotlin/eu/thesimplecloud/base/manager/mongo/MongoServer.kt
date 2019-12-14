package eu.thesimplecloud.base.manager.mongo

import com.mongodb.Mongo
import eu.thesimplecloud.clientserverapi.lib.bootstrap.IBootstrap
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.log4j.BasicConfigurator
import org.litote.kmongo.KMongo
import java.io.File
import java.util.logging.Level
import java.util.logging.Logger


class MongoServer(private val mongoServerConfig: MongoConnectionInformation) : IBootstrap {

    lateinit var mongo: MongoEmbeddedService

    override fun isActive(): Boolean = mongo.isStarted


    override fun start() {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1\$tT] [%4$-7s] %5\$s %n")
        //BasicConfigurator.configure()
        val mongoLogger = Logger.getLogger("org.mongodb.driver")
        mongoLogger.level = Level.OFF
        mongo = MongoEmbeddedService(
                "${mongoServerConfig.host}:${mongoServerConfig.port}",
                mongoServerConfig.databaseName,
                mongoServerConfig.userName,
                mongoServerConfig.password,
                "local",
                "database",
                true,
                10000
        )
        mongo.start()
    }

    override fun shutdown() {
        mongo.stop()
    }

}

fun main() {
    println(File(".").absolutePath)
    val mongoConnectionInformation = MongoConnectionInformation("127.0.0.1", 45678, "cloud", "simplecloud", "cloudpassword")
    val mongoServer = MongoServer(mongoConnectionInformation)
    GlobalScope.launch {
        println("starting")
        mongoServer.start()
        println("started")
    }
    /*
    Thread.sleep(2000)
    println("starting client")
    val client = KMongo.createClient(mongoConnectionInformation.getConnectionString())
    */
    while (true) {
        Thread.sleep(20)
    }

}