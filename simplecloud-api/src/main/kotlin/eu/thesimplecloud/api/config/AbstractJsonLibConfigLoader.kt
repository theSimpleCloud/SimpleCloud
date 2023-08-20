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

package eu.thesimplecloud.api.config

import com.google.gson.Gson
import eu.thesimplecloud.jsonlib.JsonLib
import java.io.File

abstract class AbstractJsonLibConfigLoader<T : Any>(
    private val configClass: Class<T>,
    private val configFie: File,
    private val lazyDefaultObject: () -> T,
    private val saveDefaultOnFistLoad: Boolean,
    private val gsonToUse: Gson = JsonLib.GSON
) : IConfigLoader<T> {

    override fun loadConfig(): T {
        val objectFromFile = JsonLib.fromJsonFile(configFie, gsonToUse)?.getObjectOrNull(configClass)
        if (objectFromFile == null) {
            val defaultObject = lazyDefaultObject()
            if (saveDefaultOnFistLoad && !doesConfigFileExist())
                saveConfig(defaultObject)
            return defaultObject
        }
        return objectFromFile
    }

    override fun saveConfig(value: T) {
        JsonLib.fromObject(value, gsonToUse).saveAsFile(configFie)
    }

    override fun doesConfigFileExist(): Boolean = this.configFie.exists()


}