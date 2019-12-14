package eu.thesimplecloud.base.manager.mongo

import com.mongodb.ConnectionString

class MongoConnectionInformation(
  val host: String,
  val port: Int,
  val databaseName: String,
  val userName: String,
  val password: String
) {

    fun getConnectionString(): ConnectionString {
        return ConnectionString("mongodb://$userName:$password@$host:$port/$databaseName")
    }
}