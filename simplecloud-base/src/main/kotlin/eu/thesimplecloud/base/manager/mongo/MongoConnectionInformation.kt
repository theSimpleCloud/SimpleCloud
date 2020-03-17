package eu.thesimplecloud.base.manager.mongo

import com.mongodb.ConnectionString
import com.mongodb.MongoClient
import org.litote.kmongo.KMongo

open class MongoConnectionInformation(
        val host: String,
        val port: Int,
        val databaseName: String,
        val userName: String,
        val password: String,
        val collectionPrefix: String
) {

    private fun getConnectionString(): ConnectionString {
        return ConnectionString("mongodb://$userName:$password@$host:$port/$databaseName")
    }

    fun createMongoClient(): MongoClient {
        if (password.isBlank() || userName.isBlank()) {
            return KMongo.createClient(host, port)
        }
        return KMongo.createClient(getConnectionString())
    }
}