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

package eu.thesimplecloud.module.rest.defaultcontroller.wrapper

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.wrapper.IWrapperInfo
import eu.thesimplecloud.api.wrapper.impl.DefaultWrapperInfo
import eu.thesimplecloud.module.rest.annotation.*
import eu.thesimplecloud.module.rest.controller.IController

/**
 * Created by IntelliJ IDEA.
 * Date: 07.10.2020
 * Time: 19:20
 * @author Frederick Baier
 */
@RestController("cloud/wrapper/")
class WrapperController : IController {

    @RequestMapping(RequestType.GET, "", "web.cloud.wrapper.get.all")
    fun handleGetAllWrappers(): List<IWrapperInfo> {
        return CloudAPI.instance.getWrapperManager().getAllCachedObjects()
    }

    @RequestMapping(RequestType.GET, "name/:name/", "web.cloud.wrapper.get.one")
    fun handleGetOneWrapper(@RequestPathParam("name") name: String): IWrapperInfo? {
        return getWrapperByName(name)
    }

    @RequestMapping(RequestType.POST, "", "web.cloud.wrapper.create")
    fun handleCreateWrapper(@RequestBody wrapper: DefaultWrapperInfo): IWrapperInfo {
        if (doesWrapperExist(wrapper.getName())) throwElementAlreadyExist()
        CloudAPI.instance.getWrapperManager().update(wrapper)
        return wrapper
    }

    @RequestMapping(RequestType.PUT, "", "web.cloud.wrapper.update")
    fun handleUpdateWrapper(@RequestBody wrapper: DefaultWrapperInfo): IWrapperInfo? {
        if (!doesWrapperExist(wrapper.getName())) throwNoSuchElement()
        CloudAPI.instance.getWrapperManager().update(wrapper)
        return wrapper
    }

    @RequestMapping(RequestType.DELETE, "name/:name", "web.cloud.wrapper.delete")
    fun handleDeleteWrapper(@RequestPathParam("name") name: String): IWrapperInfo? {
        if (!doesWrapperExist(name)) throwNoSuchElement()
        val wrapper = getWrapperByName(name)!!
        CloudAPI.instance.getWrapperManager().delete(wrapper)
        return wrapper
    }


    private fun getWrapperByName(name: String): IWrapperInfo? {
        return CloudAPI.instance.getWrapperManager().getWrapperByName(name)
    }

    private fun doesWrapperExist(name: String): Boolean {
        return getWrapperByName(name) != null
    }

}