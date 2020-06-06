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
        if (password.isBlank() || userName.isBlank()) {
            return ConnectionString("mongodb://$host:$port/$databaseName")
        }

        return ConnectionString("mongodb://$userName:$password@$host:$port/$databaseName")
    }

    fun createMongoClient(): MongoClient {
        //return KMongo.createClient(MongoClientSettings.builder().applyConnectionString(getConnectionString())
        //              .codecRegistry(CodecRegistries.fromRegistries(
        //                CodecRegistries.fromCodecs(UuidCodec(UuidRepresentation.STANDARD))
        //      )).build())
        return KMongo.createClient(getConnectionString())
    }
}