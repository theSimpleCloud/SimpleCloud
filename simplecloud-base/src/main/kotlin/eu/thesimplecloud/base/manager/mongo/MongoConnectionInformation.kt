package eu.thesimplecloud.base.manager.mongo

import com.mongodb.ConnectionString
import com.mongodb.client.MongoClient
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
        if (password.isBlank() || userName.isBlank()) {
            return ConnectionString("mongodb://$host:$port/$databaseName")
        }

        return ConnectionString("mongodb://$userName:$password@$host:$port/$databaseName")
    }

    fun createMongoClient(): MongoClient {
        return KMongo.createClient(getConnectionString())
    }
}