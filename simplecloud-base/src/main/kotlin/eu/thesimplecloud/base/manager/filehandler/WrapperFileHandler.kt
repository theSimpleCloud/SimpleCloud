/*
 * MIT License
 *
 * Copyright (C) 2020 SimpleCloud-Team
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

package eu.thesimplecloud.base.manager.filehandler

import eu.thesimplecloud.api.config.IFileHandler
import eu.thesimplecloud.api.directorypaths.DirectoryPaths
import eu.thesimplecloud.api.wrapper.IWrapperInfo
import eu.thesimplecloud.api.wrapper.impl.DefaultWrapperInfo
import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import java.io.File

class WrapperFileHandler : IFileHandler<IWrapperInfo> {

    private val directory = File(DirectoryPaths.paths.wrappersPath)

    override fun save(value: IWrapperInfo) {
        JsonData.fromObject(value).saveAsFile(getFile(value))
    }

    override fun delete(value: IWrapperInfo) {
        getFile(value).delete()
    }

    override fun loadAll(): Set<IWrapperInfo> = directory.listFiles().mapNotNull { JsonData.fromJsonFile(it)?.getObject(DefaultWrapperInfo::class.java) }.toSet()


    fun getFile(wrapperInfo: IWrapperInfo): File = File(directory, wrapperInfo.getName() + ".json")
}