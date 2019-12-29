package eu.thesimplecloud.base.manager.config

import eu.thesimplecloud.base.manager.mongo.MongoServerInformation

class MongoConfig(val embedMongo: Boolean, val mongoServerInformation: MongoServerInformation) {
}