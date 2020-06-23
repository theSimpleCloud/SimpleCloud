/*
 * MIT License
 *
 * Copyright (C) 2020 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.base.manager.setup.database

import eu.thesimplecloud.base.manager.config.mongo.DatabaseConfigLoader
import eu.thesimplecloud.base.manager.database.DatabaseConnectionInformation
import eu.thesimplecloud.base.manager.database.DatabaseType
import eu.thesimplecloud.launcher.console.setup.ISetup
import eu.thesimplecloud.launcher.console.setup.annotations.SetupFinished
import eu.thesimplecloud.launcher.console.setup.annotations.SetupQuestion
import eu.thesimplecloud.launcher.extension.sendMessage
import eu.thesimplecloud.launcher.startup.Launcher

class DatabaseConnectionSetup : ISetup {

    var host: String? = null
    var port: Int? = null
    var databaseName: String? = null
    var username: String? = null
    var password: String? = null
    var databaseType: DatabaseType? = null

    @SetupQuestion(0, "manager.setup.database-connection.question.type", "Please provide the type of your database.", DatabaseTypeSetupAnswerProvider::class)
    fun type(type: DatabaseType): Boolean {
        this.databaseType = type
        Launcher.instance.consoleSender.sendMessage(true, "manager.setup.database-connection.question.type.success", "Type set.")
        return true
    }

    @SetupQuestion(0, "manager.setup.database-connection.question.host", "Please provide the host of your database.")
    fun host(host: String): Boolean {
        if (host.isEmpty()) {
            Launcher.instance.consoleSender.sendMessage(true, "manager.setup.database-connection.host-invalid", "The entered host is invalid.")
            return false
        }
        this.host = host
        Launcher.instance.consoleSender.sendMessage(true, "manager.setup.database-connection.question.host.success", "Host set.")
        return true
    }

    @SetupQuestion(1, "manager.setup.database-connection.question.port", "Please provide the port of the database.")
    fun port(port: Int): Boolean {
        if (port <= 0) {
            Launcher.instance.consoleSender.sendMessage(true, "manager.setup.database-connection.port-invalid", "The entered port is invalid.")
            return false
        }
        this.port = port
        Launcher.instance.consoleSender.sendMessage(true, "manager.setup.database-connection.question.port.success", "Port set.")
        return true
    }

    @SetupQuestion(2, "manager.setup.database-connection.question.database", "Please provide the database name of the database.")
    fun database(database: String): Boolean {
        this.databaseName = database
        Launcher.instance.consoleSender.sendMessage(true, "manager.setup.database-connection.question.database.success", "Database set.")
        return true
    }

    @SetupQuestion(3, "manager.setup.database-connection.question.username", "Please provide the username of the database.")
    fun username(username: String): Boolean {
        this.username = username
        Launcher.instance.consoleSender.sendMessage(true, "manager.setup.database-connection.question.username.success", "Username set.")
        return true
    }

    @SetupQuestion(4, "manager.setup.database-connection.question.password", "Please provide the password of the database.")
    fun password(password: String): Boolean {
        this.password = password
        Launcher.instance.consoleSender.sendMessage(true, "manager.setup.database-connection.question.password.success", "Password set.")
        return true
    }

    @SetupFinished
    fun finished() {
        val databaseConnectionInformation = DatabaseConnectionInformation(
                this.host!!,
                this.port!!,
                this.databaseName!!,
                this.username!!,
                this.password!!,
                "cloud_",
                databaseType!!
        )
        DatabaseConfigLoader().saveConfig(databaseConnectionInformation)
    }


}