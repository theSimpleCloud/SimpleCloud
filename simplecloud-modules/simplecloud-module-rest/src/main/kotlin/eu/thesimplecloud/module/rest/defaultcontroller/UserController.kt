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

package eu.thesimplecloud.module.rest.defaultcontroller

import eu.thesimplecloud.module.rest.annotation.*
import eu.thesimplecloud.module.rest.auth.AuthService
import eu.thesimplecloud.module.rest.auth.user.User
import eu.thesimplecloud.module.rest.controller.IController

/**
 * Created by IntelliJ IDEA.
 * Date: 05.10.2020
 * Time: 17:14
 * @author Frederick Baier
 */
@RestController("user/")
class UserController(
        private val authService: AuthService
) : IController {

    @RequestMapping(RequestType.GET, "", "web.user.get.all")
    fun handleGetAllUsers(): List<User> {
        return authService.getUsers()
    }

    @RequestMapping(RequestType.GET, "self/", "web.user.get.self")
    fun handleGetSelfUser(@RequestingUser user: User): User {
        return user
    }

    @RequestMapping(RequestType.GET, "name/:name", "web.user.get.one")
    fun handleGetOneUsers(@RequestPathParam("name") username: String): User {
        return authService.handleUserGet(username) ?: throwNoSuchElement()
    }

    @RequestMapping(RequestType.POST, "", "web.user.create")
    fun handleCreateUser(@RequestBody user: User): User {
        return authService.handleAddUser(user)
    }

    @RequestMapping(RequestType.PUT, "", "web.user.update")
    fun handleUpdateUser(@RequestBody user: User): User {
        return authService.handleUserUpdate(user)
    }

    @RequestMapping(RequestType.DELETE, "", "web.user.delete")
    fun handleDeleteUser(@RequestBody user: User): User {
        authService.handleUserDelete(user)
        return user
    }

}