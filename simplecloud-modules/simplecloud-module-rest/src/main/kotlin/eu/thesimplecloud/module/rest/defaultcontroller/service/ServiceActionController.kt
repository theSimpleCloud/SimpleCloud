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

package eu.thesimplecloud.module.rest.defaultcontroller.service

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.module.rest.annotation.*
import eu.thesimplecloud.module.rest.controller.IController
import eu.thesimplecloud.module.rest.defaultcontroller.dto.CommandDto
import eu.thesimplecloud.module.rest.defaultcontroller.dto.PathDto

/**
 * Created by IntelliJ IDEA.
 * Date: 06.10.2020
 * Time: 16:38
 * @author Frederick Baier
 */
@RestController("cloud/action/service/")
class ServiceActionController : IController {

    @RequestMapping(RequestType.POST, "name/:name/stop", "web.cloud.action.service.stop")
    fun handleServiceStop(@RequestPathParam("name") name: String): ICloudService {
        val service = getServiceByName(name) ?: throwNoSuchElement()
        service.shutdown()
        return service
    }

    @RequestMapping(RequestType.POST, "name/:name/copy", "web.cloud.action.service.copy")
    fun handleCopyService(@RequestPathParam("name") name: String, @RequestBody path: PathDto): ICloudService {
        val service = getServiceByName(name) ?: throwNoSuchElement()
        service.copy(path.path)
        return service
    }

    @RequestMapping(RequestType.POST, "name/:name/execute", "web.cloud.action.service.execute")
    fun handleCommandExecute(@RequestPathParam("name") name: String, @RequestBody command: CommandDto): ICloudService {
        val service = getServiceByName(name) ?: throwNoSuchElement()
        service.executeCommand(command.command)
        return service
    }

    private fun getServiceByName(name: String): ICloudService? {
        return CloudAPI.instance.getCloudServiceManager().getCloudServiceByName(name)
    }

}