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

package eu.thesimplecloud.module.rest.auth

import eu.thesimplecloud.jsonlib.JsonLib
import eu.thesimplecloud.module.rest.auth.controller.LoginDto
import eu.thesimplecloud.module.rest.auth.user.User
import eu.thesimplecloud.module.rest.auth.user.permission.Permission
import eu.thesimplecloud.module.rest.controller.IExceptionHelper
import org.apache.commons.lang3.RandomStringUtils
import java.io.File

/**
 * Created by IntelliJ IDEA.
 * Date: 05.10.2020
 * Time: 13:05
 * @author Frederick Baier
 */
class AuthService : IExceptionHelper {

    private val usersFile = File("modules/rest/users.json")
    private val users: MutableList<User>

    init {
        if (!usersFile.exists()) {
            this.users = ArrayList()
            val standardUser = User("admin", RandomStringUtils.randomAlphabetic(32))
            standardUser.addPermission(Permission("*", true))
            this.users.add(standardUser)
            saveUpdateToFile()
        } else {
            this.users = JsonLib.fromJsonFile(this.usersFile)!!.getObject(Array<User>::class.java).toMutableList()
        }
    }

    fun handleLogin(login: LoginDto): String? {
        val user = getUserByName(login.username)
        if (user == null || user.password != login.password)
            return null
        return JwtProvider.instance.provider.generateToken(user)
    }

    fun handleAddUser(user: User): User {
        if (doesUserExist(user.username))
            throwElementAlreadyExist()

        val user = cloneUser(user)
        this.users.add(user)
        saveUpdateToFile()
        return user
    }

    fun handleUserUpdate(user: User): User {
        if (!doesUserExist(user.username))
            throwNoSuchElement()

        val user = cloneUser(user)
        this.users.removeIf { it.username == user.username }
        this.users.add(user)
        saveUpdateToFile()
        return user
    }

    fun handleUserDelete(user: User) {
        if (!doesUserExist(user.username))
            throwNoSuchElement()

        this.users.removeIf { it.username == user.username }
        saveUpdateToFile()
    }

    fun handleUserGet(username: String): User? {
        return getUserByName(username)
    }

    fun getUserByName(userName: String): User? {
        return this.users.firstOrNull { it.username == userName }
    }

    fun doesUserExist(userName: String): Boolean {
        return getUserByName(userName) != null
    }

    fun getUsers(): List<User> {
        return this.users
    }

    private fun cloneUser(user: User): User {
        return User(user.username, user.password)
    }

    private fun saveUpdateToFile() {
        JsonLib.fromObject(users).saveAsFile(usersFile)
    }

    class UserAlreadyExistException() : Exception("The user does already exist")

    class UserNotExistException() : Exception("The user does not exist")

}