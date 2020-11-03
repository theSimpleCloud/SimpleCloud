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

package eu.thesimplecloud.base.manager.language

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.external.ICloudModule
import eu.thesimplecloud.api.language.LanguageManager
import eu.thesimplecloud.api.language.LoadedLanguageFile
import eu.thesimplecloud.api.network.packets.language.PacketIOLanguage
import eu.thesimplecloud.base.manager.startup.Manager

/**
 * Created by IntelliJ IDEA.
 * Date: 31.10.2020
 * Time: 23:06
 * @author Frederick Baier
 */
class LanguageManagerImpl : LanguageManager() {

    override fun registerLanguageFile(cloudModule: ICloudModule, languageFile: LoadedLanguageFile) {
        super.registerLanguageFile(cloudModule, languageFile)

        val allServices = CloudAPI.instance.getCloudServiceManager().getAllCachedObjects()
        val allServiceClients = allServices.map {
            Manager.instance.communicationServer.getClientManager().getClientByClientValue(it)
        }
        val packet = PacketIOLanguage(this.getAllProperties())
        allServiceClients.filterNotNull().forEach { it.sendUnitQuery(packet) }
    }


}