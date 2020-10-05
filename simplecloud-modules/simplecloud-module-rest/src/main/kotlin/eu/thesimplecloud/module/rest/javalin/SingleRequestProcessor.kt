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

package eu.thesimplecloud.module.rest.javalin

import eu.thesimplecloud.jsonlib.JsonLib
import eu.thesimplecloud.module.rest.annotation.RequestBody
import eu.thesimplecloud.module.rest.annotation.RequestParam
import eu.thesimplecloud.module.rest.auth.user.User
import eu.thesimplecloud.module.rest.controller.RequestMethodData
import io.javalin.http.Context

/**
 * Created by IntelliJ IDEA.
 * Date: 05.10.2020
 * Time: 13:43
 * @author Frederick Baier
 */
class SingleRequestProcessor(
        private val ctx: Context,
        private val requestMethodData: RequestMethodData,
        private val requestingUser: User?
) {

    fun processRequest() {
        val parameterDataToInvokeValue = requestMethodData.parameters
                .map { it to handleValueForParameter(it) }.toMap()

        if (parameterDataToInvokeValue.any { isValueIncorrect(it.key, it.value) }) {
            ctx.result("Bad Request")
            ctx.status(400)
            return
        }

        val valueArray = parameterDataToInvokeValue.values.toTypedArray()
        val result = this.requestMethodData.method.invoke(this.requestMethodData.controller, *valueArray)

        //then the result was set by the called method
        if (this.ctx.resultString() != null)
            return

        if (result == null) {
            ctx.result(JsonLib.empty().append("result", "null").getAsJsonString())
            return
        }
        if (result != Unit) {
            ctx.result(JsonLib.fromObject(result, RestServer.instance.webGson).getAsJsonString())
        }
    }

    private fun isValueIncorrect(parameterData: RequestMethodData.RequestParameterData, value: Any?): Boolean {
        val annotation = parameterData.annotation
        if (annotation is RequestParam) {
            return annotation.required && value == null
        }
        return false
    }


    private fun handleValueForParameter(parameterData: RequestMethodData.RequestParameterData): Any? {
        if (parameterData.parameterType == Context::class.java) {
            return this.ctx
        }
        if (parameterData.parameterType == User::class.java) {
            return this.requestingUser
        }

        val annotation = parameterData.annotation!!
        when (annotation) {
            is RequestBody -> {
                return JsonLib.fromJsonString(this.ctx.body()).getObject(parameterData.parameterType)
            }
            is RequestParam -> {
                return this.ctx.req.getParameter(annotation.parameterName)
            }
        }
        throw IllegalStateException()
    }

}