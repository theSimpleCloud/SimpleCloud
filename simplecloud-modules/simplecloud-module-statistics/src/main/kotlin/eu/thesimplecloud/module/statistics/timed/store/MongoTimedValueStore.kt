/*
 * MIT License
 *
 * Copyright (C) 2020-2022 The SimpleCloud authors
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

package eu.thesimplecloud.module.statistics.timed.store

import com.mongodb.BasicDBObject
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.IndexOptions
import eu.thesimplecloud.jsonlib.JsonLib
import eu.thesimplecloud.module.statistics.timed.TimedValue
import org.bson.Document
import org.litote.kmongo.ensureIndex
import org.litote.kmongo.find
import org.litote.kmongo.getCollection

/**
 * Created by IntelliJ IDEA.
 * Date: 25.12.2020
 * Time: 23:23
 * @author Frederick Baier
 */
class MongoTimedValueStore<T : Any>(
    private val classOfT: Class<T>,
    private val collectionName: String,
    database: MongoDatabase
) : ITimedValueStore<T> {

    private val collection = database.getCollection<Document>(collectionName)

    init {
        collection.ensureIndex(BasicDBObject("timeStamp", 1), IndexOptions().unique(false))
    }

    override fun store(timedValue: TimedValue<T>) {
        val document = JsonLib.fromObject(timedValue).getObject(Document::class.java)
        collection.insertOne(document)
    }

    override fun getAll(): List<TimedValue<T>> {
        return collection.find().toList().map { constructTimedValueFromDocument(it) }
    }

    override fun get(fromTimeStamp: Long, toTimeStamp: Long): List<TimedValue<T>> {
        return collection.find("{ timeStamp : { \$gt :  $fromTimeStamp, \$lt : $toTimeStamp}}").toList()
            .map { constructTimedValueFromDocument(it) }
    }

    override fun getCollectionName(): String {
        return this.collectionName
    }

    private fun constructTimedValueFromDocument(document: Document): TimedValue<T> {
        val jsonLib = JsonLib.fromObject(document)
        val value = jsonLib.getObject("value", classOfT)!!
        val timeStamp = jsonLib.getLong("timeStamp")!!
        return TimedValue<T>(value, timeStamp)
    }

    override fun count(): Int {
        return this.collection.countDocuments().toInt()
    }
}