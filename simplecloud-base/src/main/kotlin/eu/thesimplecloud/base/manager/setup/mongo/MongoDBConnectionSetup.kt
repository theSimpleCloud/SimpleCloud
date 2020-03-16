package eu.thesimplecloud.base.manager.setup.mongo

import eu.thesimplecloud.base.manager.config.MongoConfig
import eu.thesimplecloud.base.manager.config.MongoConfigLoader
import eu.thesimplecloud.base.manager.mongo.MongoConnectionInformation
import eu.thesimplecloud.base.manager.mongo.MongoServerInformation
import eu.thesimplecloud.launcher.console.setup.ISetup
import eu.thesimplecloud.launcher.console.setup.annotations.SetupFinished
import eu.thesimplecloud.launcher.console.setup.annotations.SetupQuestion
import eu.thesimplecloud.launcher.extension.sendMessage
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.launcher.utils.IpValidator

class MongoDBConnectionSetup : ISetup {

    var host: String? = null
    var port: Int? = null
    var databaseName: String? = null
    var username: String? = null
    var password: String? = null

    @SetupQuestion(0, "manager.setup.mongodb-connection.question.host", "Please provide the host of the mongodb installation.")
    fun host(host: String): Boolean {
        if (!IpValidator().validate(host)) {
            Launcher.instance.consoleSender.sendMessage("manager.setup.mongodb-connection.host-invalid", "The entered host is invalid.")
            return false
        }
        this.host = host
        Launcher.instance.consoleSender.sendMessage("manager.setup.mongodb-connection.question.host.success", "Host set.")
        return true
    }

    @SetupQuestion(1, "manager.setup.mongodb-connection.question.port", "Please provide the port of the mongodb installation. (default: 27017)")
    fun port(port: Int): Boolean {
        if (port <= 0) {
            Launcher.instance.consoleSender.sendMessage("manager.setup.mongodb-connection.port-invalid", "The entered port is invalid.")
            return false
        }
        this.port = port
        Launcher.instance.consoleSender.sendMessage("manager.setup.mongodb-connection.question.port.success", "Port set.")
        return true
    }

    @SetupQuestion(2, "manager.setup.mongodb-connection.question.database", "Please provide the database name of the mongodb installation.")
    fun database(database: String): Boolean {
        this.databaseName = database
        Launcher.instance.consoleSender.sendMessage("manager.setup.mongodb-connection.question.database.success", "Database set.")
        return true
    }

    @SetupQuestion(3, "manager.setup.mongodb-connection.question.username", "Please provide the username of the mongodb installation.")
    fun username(username: String): Boolean {
        this.username = username
        Launcher.instance.consoleSender.sendMessage("manager.setup.mongodb-connection.question.username.success", "Username set.")
        return true
    }

    @SetupQuestion(4, "manager.setup.mongodb-connection.question.password", "Please provide the password of the mongodb installation.")
    fun password(password: String): Boolean {
        this.password = password
        Launcher.instance.consoleSender.sendMessage("manager.setup.mongodb-connection.question.password.success", "Password set.")
        return true
    }

    @SetupFinished
    fun finished() {
        val mongoServerInformation = MongoServerInformation(this.host!!, this.port!!, this.databaseName!!, this.username!!, this.password!!, "", "/", "/")
        MongoConfigLoader().saveConfig(MongoConfig(false, mongoServerInformation))
    }


}