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

package eu.thesimplecloud.module.rest.defaultcontroller.filemanager

import eu.thesimplecloud.module.rest.annotation.RequestMapping
import eu.thesimplecloud.module.rest.annotation.RequestType
import eu.thesimplecloud.module.rest.annotation.RestController
import eu.thesimplecloud.module.rest.controller.IController
import io.javalin.core.util.FileUtil
import io.javalin.http.Context
import java.io.File

/**
 * Created by IntelliJ IDEA.
 * Date: 17.11.2020
 * Time: 20:18
 * @author Frederick Baier
 */
@RestController("filemanager/")
class FileManagerController : IController {

    @RequestMapping(RequestType.GET, "*", "web.filemanager.read")
    fun handleRead(ctx: Context): Collection<FileInfo> {
        checkForSuspiciousPath(ctx)
        val file = getFileFromRequest(ctx)
        if (!file.exists()) throwNoSuchElement()
        if (file.isDirectory) {
            val listFiles = file.listFiles().toList()
            val directories = listFiles.filter { it.isDirectory }.sortedBy { it.name }
            val files = listFiles.filter { !it.isDirectory }.sortedBy { it.name }
            return directories.union(files).map { FileInfo(it.name, it.isDirectory, it.length()) }
        }
        if (ctx.queryParam("view") == null)
            ctx.res.setHeader("Content-Disposition", "attachment; filename=${file.name}")
        ctx.result(file.readBytes())
        return emptyList()
    }

    @RequestMapping(RequestType.DELETE, "*", "web.filemanager.delete")
    fun handleDelete(ctx: Context): Boolean {
        checkForSuspiciousPath(ctx)
        val file = getFileFromRequest(ctx)
        if (!file.exists()) throwNoSuchElement()
        if (file.isDirectory) {
            //FileUtils.deleteDirectory(file)
        } else {
            file.delete()
        }
        return true
    }

    @RequestMapping(RequestType.POST, "*", "web.filemanager.create")
    fun handleCreate(ctx: Context): Boolean {
        checkForSuspiciousPath(ctx)
        val file = getFileFromRequest(ctx)
        println(file.absolutePath)
        if (file.exists() && file.isDirectory) return false
        if (file.exists()) file.delete()
        file.parentFile?.mkdirs()
        val uploadedFile = ctx.uploadedFile("file")
        uploadedFile?.let {
            println(it.filename)
            FileUtil.streamToFile(it.content, file.absolutePath)
        }
        return true
    }

    private fun getFileFromRequest(ctx: Context): File {
        val filepath = ctx.req.pathInfo.replace("/filemanager/", "")
        if (filepath.isBlank()) return File(".")
        return File(filepath)
    }

    private fun checkForSuspiciousPath(ctx: Context) {
        if (ctx.req.pathInfo.contains("..") || ctx.req.pathInfo.contains("//"))
            throw InvalidPathException("Invalid file path")
    }

    class FileAlreadyExistsException(message: String) : Exception(message)

    class InvalidPathException(message: String) : Exception(message)

    class FileInfo(val name: String, val isDirectory: Boolean, val size: Long)

}