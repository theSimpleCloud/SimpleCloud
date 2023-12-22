package eu.thesimplecloud.module.statistics.rest.overview.data

import okhttp3.OkHttpClient

interface LabyResource<T> {

    fun createRequestURL(args: T): String
    fun retrieve(client: OkHttpClient): Boolean
}