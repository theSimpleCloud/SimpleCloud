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

package eu.thesimplecloud.base.manager.update.converter.converter_1_2_to_1_3

import eu.thesimplecloud.jsonlib.JsonLib
import java.io.File

/**
 * Created by IntelliJ IDEA.
 * Date: 22.06.2020
 * Time: 19:04
 * @author Frederick Baier
 */
class PermissionFileConverter {

    fun convert() {
        val file = File("modules/permissions/groups.json")
        if (!file.exists()) return
        val jsonLib = JsonLib.fromJsonFile(file)!!
        val permissionGroupName = jsonLib.getString("defaultPermissionGroupName")!!
        val groups = jsonLib.getAsJsonArray("values")!!
        val allGroups = groups.map {
            val oneGroupObjElement = JsonLib.fromJsonElement(it)
            oneGroupObjElement.getProperty("obj")!!
        }
        JsonLib.empty().append("defaultPermissionGroupName", permissionGroupName)
                .append("groups", allGroups)
                .saveAsFile(file)
    }

}