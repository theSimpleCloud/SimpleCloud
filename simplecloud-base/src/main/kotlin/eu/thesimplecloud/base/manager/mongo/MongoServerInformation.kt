package eu.thesimplecloud.base.manager.mongo

class MongoServerInformation(
        host: String,
        port: Int,
        databaseName: String,
        userName: String,
        password: String,
        collectionPrefix: String,
        val adminUserName: String,
        val adminPassword: String
) : MongoConnectionInformation(host, port, databaseName, userName, password, collectionPrefix)