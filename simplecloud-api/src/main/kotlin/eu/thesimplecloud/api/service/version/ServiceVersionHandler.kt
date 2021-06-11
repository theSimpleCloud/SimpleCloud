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

package eu.thesimplecloud.api.service.version

import eu.thesimplecloud.api.service.version.type.ServiceAPIType
import eu.thesimplecloud.api.service.version.type.ServiceVersionType

/**
 * Created by IntelliJ IDEA.
 * Date: 14.06.2020
 * Time: 13:27
 * @author Frederick Baier
 */
open class ServiceVersionHandler(
        @Volatile
        var versions: List<ServiceVersion> = emptyList()
) : IServiceVersionHandler {


    override fun getVersions(serviceAPIType: ServiceAPIType): List<ServiceVersion> {
        return this.versions.filter { it.serviceAPIType == serviceAPIType }
    }

    override fun getVersionsByPrefix(prefix: String): List<ServiceVersion> {
        if (prefix.contains("_")) return emptyList()
        val serviceVersions = versions.filter { it.name.startsWith(prefix.toUpperCase()) }
        if (serviceVersions.isEmpty()) return emptyList()
        //check if all versions have the same version type
        val firstVersionType = serviceVersions.first().serviceAPIType
        if (!serviceVersions.all { it.serviceAPIType == firstVersionType })
            throw IllegalStateException("API types of prefix $prefix are not equal")
        return serviceVersions
    }

    override fun getAllAvailablePrefixes(): Set<String> {
        return this.versions.map { getPrefixFromFullName(it.name) }.toSet()
    }

    override fun getAllVersionSuffixes(prefix: String): List<String> {
        return getVersionsByPrefix(prefix)
                .map { it.name }
                .map { it.replace((prefix + "_").toUpperCase(), "") }
    }

    override fun getPrefixesByServiceAPIType(serviceAPIType: ServiceAPIType): Set<String> {
        return getVersions(serviceAPIType).map { getPrefixFromFullName(it.name) }.toSet()
    }

    override fun getVersionsByServiceVersionType(serviceVersionType: ServiceVersionType): List<ServiceVersion> {
        return this.versions.filter { it.serviceAPIType.serviceVersionType == serviceVersionType }
    }

    override fun getPrefixesByServiceVersionType(serviceVersionType: ServiceVersionType): Set<String> {
        return getVersionsByServiceVersionType(serviceVersionType)
                .map { getPrefixFromFullName(it.name) }.toSet()
    }



    override fun getServiceVersionByName(name: String): ServiceVersion? {
        return this.versions.firstOrNull { it.name == name }
    }

    override fun getAllVersions(): List<ServiceVersion> {
        return this.versions
    }

    private fun getPrefixFromFullName(fullName: String): String {
        return fullName.split("_")[0]
    }


}