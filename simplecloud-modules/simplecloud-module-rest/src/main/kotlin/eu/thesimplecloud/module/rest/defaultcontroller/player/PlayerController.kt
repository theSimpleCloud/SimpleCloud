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

package eu.thesimplecloud.module.rest.defaultcontroller.player

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.player.IOfflineCloudPlayer
import eu.thesimplecloud.api.player.SimpleCloudPlayer
import eu.thesimplecloud.module.rest.annotation.RequestMapping
import eu.thesimplecloud.module.rest.annotation.RequestPathParam
import eu.thesimplecloud.module.rest.annotation.RequestType
import eu.thesimplecloud.module.rest.annotation.RestController
import eu.thesimplecloud.module.rest.controller.IController

/**
 * Created by IntelliJ IDEA.
 * Date: 07.10.2020
 * Time: 19:38
 * @author Frederick Baier
 */
@RestController("cloud/player/")
class PlayerController : IController {

    @RequestMapping(RequestType.GET, "name/:name", "web.cloud.player.get.one")
    fun handleGetOnePlayer(@RequestPathParam("name") name: String): IOfflineCloudPlayer? {
        return CloudAPI.instance.getCloudPlayerManager().getOfflineCloudPlayer(name).getBlockingOrNull()
    }

    @RequestMapping(RequestType.GET, "", "web.cloud.player.get.all")
    fun handleGetAllPlayer(): List<SimpleCloudPlayer> {
        return CloudAPI.instance.getCloudPlayerManager().getAllOnlinePlayers().getBlockingOrNull() ?: emptyList()
    }

}