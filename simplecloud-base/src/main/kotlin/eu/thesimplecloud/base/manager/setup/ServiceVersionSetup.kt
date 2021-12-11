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

package eu.thesimplecloud.base.manager.setup

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.service.version.ServiceVersion
import eu.thesimplecloud.api.service.version.loader.LocalServiceVersionHandler
import eu.thesimplecloud.api.service.version.type.ServiceAPIType
import eu.thesimplecloud.base.manager.serviceversion.ManagerServiceVersionHandler
import eu.thesimplecloud.base.manager.setup.provider.ServiceAPITypeAnswerProvider
import eu.thesimplecloud.launcher.console.setup.ISetup
import eu.thesimplecloud.launcher.console.setup.annotations.SetupFinished
import eu.thesimplecloud.launcher.console.setup.annotations.SetupQuestion
import eu.thesimplecloud.launcher.startup.Launcher
import java.net.MalformedURLException
import java.net.URL

/**
 * Created by IntelliJ IDEA.
 * Date: 29/01/2021
 * Time: 21:38
 * @author Frederick Baier
 */
class ServiceVersionSetup : ISetup {

    private lateinit var name: String
    private lateinit var serviceAPIType: ServiceAPIType
    private lateinit var downloadURL: String
    private var isPaperclip: Boolean = false

    @SetupQuestion(0, "manager.setup.service-versions.question.name")
    fun nameSetup(name: String): Boolean {
        this.name = name
        Launcher.instance.consoleSender.sendPropertyInSetup("manager.setup.service-versions.question.name.success")
        return true
    }

    @SetupQuestion(1, "manager.setup.service-versions.question.apitype", ServiceAPITypeAnswerProvider::class)
    fun typeSetup(type: ServiceAPIType): Boolean {
        this.serviceAPIType = type
        Launcher.instance.consoleSender.sendPropertyInSetup("manager.setup.service-versions.question.apitype.success")
        return true
    }

    @SetupQuestion(2, "manager.setup.service-versions.question.url")
    fun downloadUrlSetup(url: String): Boolean {
        try {
            URL(url)
        } catch (ex: MalformedURLException) {
            Launcher.instance.consoleSender.sendPropertyInSetup("manager.setup.service-versions.question.url.invalid")
            return false
        }
        this.downloadURL = url
        Launcher.instance.consoleSender.sendPropertyInSetup("manager.setup.service-versions.question.url.success")
        return true
    }

    @SetupQuestion(3, "manager.setup.service-versions.question.paperclip")
    fun isPaperclipSetup(isPaperclip: Boolean): Boolean {
        this.isPaperclip = isPaperclip
        Launcher.instance.consoleSender.sendPropertyInSetup("manager.setup.service-versions.question.paperclip.success")
        return true
    }

    @SetupFinished
    fun setupFinished() {
        val serviceVersion = ServiceVersion(name, serviceAPIType, downloadURL, isPaperclip)
        LocalServiceVersionHandler().saveServiceVersion(serviceVersion)
        val serviceVersionHandler = CloudAPI.instance.getServiceVersionHandler() as ManagerServiceVersionHandler
        serviceVersionHandler.reloadServiceVersions()
        Launcher.instance.consoleSender.sendPropertyInSetup("manager.setup.service-versions.finished",  name)
    }


}