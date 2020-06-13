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

package eu.thesimplecloud.base.manager.config.updater

import eu.thesimplecloud.api.config.AbstractJsonLibConfigLoader
import eu.thesimplecloud.api.directorypaths.DirectoryPaths
import java.io.File

/**
 * Created by IntelliJ IDEA.
 * Date: 13.06.2020
 * Time: 07:46
 * @author Frederick Baier
 */
class ModuleUpdaterConfigLoader : AbstractJsonLibConfigLoader<ModuleUpdaterConfig>(ModuleUpdaterConfig::class.java,
        File(DirectoryPaths.paths.storagePath, "updateable-modules.json"),
        { ModuleUpdaterConfig(listOf(
                "SimpleCloud-HubCommand",
                "SimpleCloud-InternalWrapper",
                "SimpleCloud-Notify",
                "SimpleCloud-Permission",
                "SimpleCloud-Proxy",
                "SimpleCloud-Sign"
        )) },
        true
)