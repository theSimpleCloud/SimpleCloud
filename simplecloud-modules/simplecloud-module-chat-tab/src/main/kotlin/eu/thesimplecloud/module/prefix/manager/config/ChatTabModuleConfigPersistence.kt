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

package eu.thesimplecloud.module.prefix.manager.config

import eu.thesimplecloud.jsonlib.JsonLib
import java.io.File

/**
 * Created by IntelliJ IDEA.
 * Date: 10.10.2020
 * Time: 17:11
 * @author Frederick Baier
 */
object ChatTabModuleConfigPersistence {

    private val configFile = File("modules/chat+tablist/config.json")

    fun save(chatTabConfig: ChatTabConfig) {
        JsonLib.fromObject(chatTabConfig).saveAsFile(configFile)
    }

    fun load(): ChatTabConfig {
        if (!configFile.exists()) return createDefaultConfig()
        return JsonLib.fromJsonFile(configFile)!!.getObject(ChatTabConfig::class.java)
    }

    private fun createDefaultConfig(): ChatTabConfig {
        return ChatTabConfig()
    }

}