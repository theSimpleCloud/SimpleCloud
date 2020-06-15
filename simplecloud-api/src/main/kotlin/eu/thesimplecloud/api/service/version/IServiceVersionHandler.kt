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
 * Date: 15.06.2020
 * Time: 09:58
 * @author Frederick Baier
 */
interface IServiceVersionHandler {

    /**
     * Returns all available versions for the specified [serviceVersionType]
     */
    fun getVersions(serviceAPIType: ServiceAPIType): List<ServiceVersion>

    /**
     * Returns all versions with the specified [prefix]
     */
    fun getVersionsByPrefix(prefix: String): List<ServiceVersion>

    /**
     * Returns all version prefixes
     */
    fun getAllAvailablePrefixes(): Set<String>

    /**
     * Returns all versions suffixes for one [prefix]
     */
    fun getAllVersionSuffixes(prefix: String): List<String>

    /**
     * Returns the prefixes for the specified [serviceAPIType]
     */
    fun getPrefixesByServiceAPIType(serviceAPIType: ServiceAPIType): Set<String>

    /**
     * Returns the prefixes for the specified [serviceVersionType]
     */
    fun getVersionsByServiceVersionType(serviceVersionType: ServiceVersionType): List<ServiceVersion>

    /**
     * Returns the prefixes for the specified [serviceVersionType]
     */
    fun getPrefixesByServiceVersionType(serviceVersionType: ServiceVersionType): Set<String>

    /**
     * Returns the [ServiceVersion] found by the specified [name]
     */
    fun getServiceVersionByName(name: String): ServiceVersion?

    /**
     * Returns a list of all [ServiceVersion]s
     */
    fun getAllVersions(): List<ServiceVersion>

}