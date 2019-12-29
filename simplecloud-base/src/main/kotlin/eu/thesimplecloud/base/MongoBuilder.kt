package eu.thesimplecloud.base

class MongoBuilder {

    var host = "localhost"
        private set
    var port = 45678
        private set
    var adminUsername = "adminUsername"
        private set
    var adminPassword = "adminPassowrd"
        private set
    var userDatabase = "cloud"
        private set
    var userName = "cloud"
        private set
    var userPassword = "cloudpassowrd"
        private set
    var directory: String = "database"
        private set

    fun setHost(host: String): MongoBuilder {
        this.host = host
        return this
    }

    fun setPort(port: Int): MongoBuilder {
        this.port = port
        return this
    }

    fun setAdminUserName(adminUsername: String): MongoBuilder {
        this.adminUsername = adminUsername
        return this
    }

    fun setAdminPassword(adminPassword: String): MongoBuilder {
        this.adminPassword = adminPassword
        return this
    }

    fun setDatabase(database: String): MongoBuilder {
        this.userDatabase = database
        return this
    }

    fun setUserName(userName: String): MongoBuilder {
        this.userName = userName
        return this
    }

    fun setUserPassword(userPassword: String): MongoBuilder {
        this.userPassword = userPassword
        return this
    }

    fun setDirectory(directory: String): MongoBuilder {
        this.directory = directory
        return this
    }

}