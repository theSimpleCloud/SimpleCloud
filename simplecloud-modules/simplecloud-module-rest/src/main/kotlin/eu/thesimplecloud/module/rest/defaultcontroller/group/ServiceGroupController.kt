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

package eu.thesimplecloud.module.rest.defaultcontroller.group

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.api.servicegroup.grouptype.ICloudLobbyGroup
import eu.thesimplecloud.api.servicegroup.grouptype.ICloudProxyGroup
import eu.thesimplecloud.api.servicegroup.grouptype.ICloudServerGroup
import eu.thesimplecloud.api.servicegroup.impl.DefaultLobbyGroup
import eu.thesimplecloud.api.servicegroup.impl.DefaultProxyGroup
import eu.thesimplecloud.api.servicegroup.impl.DefaultServerGroup
import eu.thesimplecloud.jsonlib.JsonLib
import eu.thesimplecloud.module.rest.annotation.*
import eu.thesimplecloud.module.rest.controller.IController
import io.javalin.http.Context
import kotlin.reflect.KClass

/**
 * Created by IntelliJ IDEA.
 * Date: 05.10.2020
 * Time: 17:13
 * @author Frederick Baier
 */
@RestController("cloud/group/")
class ServiceGroupController : IController {

    //Get groups

    @RequestMapping(RequestType.GET, "", "web.cloud.group.get.all")
    fun handleGetAllGroups(): List<ICloudServiceGroup> {
        return CloudAPI.instance.getCloudServiceGroupManager().getAllCachedObjects()
    }

    @RequestMapping(RequestType.GET, "name/:name", "web.cloud.group.get.one")
    fun handleGetOneGroup(@RequestPathParam("name") name: String): ICloudServiceGroup? {
        return CloudAPI.instance.getCloudServiceGroupManager().getServiceGroupByName(name)
    }

    @RequestMapping(RequestType.GET, "name/:name/services", "web.cloud.group.get.services")
    fun handleGetServicesOfGroup(@RequestPathParam("name") name: String): List<ICloudService> {
        return CloudAPI.instance.getCloudServiceManager().getCloudServicesByGroupName(name)
    }

    //Get group types

    @RequestMapping(RequestType.GET, "lobby/", "web.cloud.group.get.type")
    fun handleGetLobbyGroups(): List<ICloudLobbyGroup> {
        return CloudAPI.instance.getCloudServiceGroupManager().getLobbyGroups()
    }

    @RequestMapping(RequestType.GET, "server/", "web.cloud.group.get.type")
    fun handleGetServerGroups(): List<ICloudServerGroup> {
        return CloudAPI.instance.getCloudServiceGroupManager().getServerGroups()
    }

    @RequestMapping(RequestType.GET, "proxy/", "web.cloud.group.get.type")
    fun handleGetProxyGroups(): List<ICloudProxyGroup> {
        return CloudAPI.instance.getCloudServiceGroupManager().getProxyGroups()
    }

    //Create groups

    @RequestMapping(RequestType.POST, "lobby/", "web.cloud.group.create")
    fun handleCreateLobbyGroup(@RequestBody group: DefaultLobbyGroup): Boolean {
        return CloudAPI.instance.getCloudServiceGroupManager().createServiceGroup(group).syncUninterruptibly().isSuccess
    }

    @RequestMapping(RequestType.POST, "server/", "web.cloud.group.create")
    fun handleCreateServerGroup(@RequestBody group: DefaultServerGroup): Boolean {
        return CloudAPI.instance.getCloudServiceGroupManager().createServiceGroup(group).syncUninterruptibly().isSuccess
    }

    @RequestMapping(RequestType.POST, "proxy/", "web.cloud.group.create")
    fun handleCreateProxyGroup(@RequestBody group: DefaultProxyGroup): Boolean {
        return CloudAPI.instance.getCloudServiceGroupManager().createServiceGroup(group).syncUninterruptibly().isSuccess
    }

    //Update groups

    @RequestMapping(RequestType.PUT, "", "web.cloud.group.update")
    fun handleUpdateLobbyGroup(ctx: Context): Boolean {
        val jsonLib = JsonLib.fromJsonString(ctx.body())
        val groupName = jsonLib.getString("name") ?: throwNoSuchElement()
        if (!doesGroupExist(groupName)) throwNoSuchElement()
        val groupClass = getRegisteredGroupTypeClassByName(groupName)
        val updateGroup = jsonLib.getObject(groupClass.java)
        CloudAPI.instance.getCloudServiceGroupManager().update(updateGroup)
        return true
    }

    //delete groups
    @RequestMapping(RequestType.DELETE, "name/:name", "web.cloud.group.delete")
    fun handleDeleteServiceGroup(@RequestPathParam("name") name: String): Boolean {
        if (!doesGroupExist(name)) throwNoSuchElement()
        val group = CloudAPI.instance.getCloudServiceGroupManager().getServiceGroupByName(name)!!
        CloudAPI.instance.getCloudServiceGroupManager().delete(group)
        return true
    }

    private fun getRegisteredGroupTypeClassByName(groupName: String): KClass<out ICloudServiceGroup> {
        return CloudAPI.instance.getCloudServiceGroupManager().getServiceGroupByName(groupName)!!::class
    }

    private fun doesGroupExist(group: ICloudServiceGroup): Boolean {
        return doesGroupExist(group.getName())
    }

    private fun doesGroupExist(name: String): Boolean {
        return CloudAPI.instance.getCloudServiceGroupManager().getServiceGroupByName(name) != null
    }

}