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

package eu.thesimplecloud.module.rest.controller

import eu.thesimplecloud.module.rest.annotation.*
import eu.thesimplecloud.module.rest.auth.user.User
import eu.thesimplecloud.module.rest.javalin.RestServer
import io.javalin.http.Context
import java.lang.reflect.Method
import java.lang.reflect.Parameter

/**
 * Created by IntelliJ IDEA.
 * Date: 04.10.2020
 * Time: 17:56
 * @author Frederick Baier
 */
class ControllerHandler(private val restServer: RestServer) {


    fun registerController(controller: IController) {
        val javaClass = controller::class.java
        val restController = javaClass.getAnnotation(RestController::class.java)!!
        val methods = javaClass.declaredMethods
        val methodsWithAnnotation = methods.filter { it.isAnnotationPresent(RequestMapping::class.java) }

        methodsWithAnnotation.forEach {
            registerMethod(restController, it, controller)
        }
    }

    private fun registerMethod(restController: RestController, method: Method, controller: IController) {
        val requestMapping = method.getAnnotation(RequestMapping::class.java)
        val parameterDataList = method.parameters.map { createParameterData(it) }
        val requestMethodData = RequestMethodData(
                controller,
                method,
                restController.path + requestMapping.additionalPath,
                requestMapping.requestType,
                requestMapping.permission,
                parameterDataList
        )
        restServer.registerRequestMethod(requestMethodData)
    }

    private fun createParameterData(parameter: Parameter): RequestMethodData.RequestParameterData {
        if (parameter.type == Context::class.java)
            return createContextParameterData()
        if (parameter.isAnnotationPresent(RequestingUser::class.java)) {
            if (parameter.type != User::class.java) {
                throw IllegalStateException("RequestingUser annotation can only be used on User")
            }
            val requestingUser = parameter.getAnnotation(RequestingUser::class.java)!!
            return createRequestingUserParameterData(requestingUser)
        }

        val requestParam = parameter.getAnnotation(RequestParam::class.java)
        if (requestParam != null) {
            return RequestMethodData.RequestParameterData(parameter.type, requestParam)
        }
        val requestBody = parameter.getAnnotation(RequestBody::class.java)
        if (requestBody != null) {
            return RequestMethodData.RequestParameterData(parameter.type, requestBody)
        }
        val requestPathParam = parameter.getAnnotation(RequestPathParam::class.java)
        if (requestPathParam != null) {
            return RequestMethodData.RequestParameterData(parameter.type, requestPathParam)
        }
        throw IllegalArgumentException("Parameter with type ${parameter.type.name} in not annotated with RequestBody, RequestParam or RequestPathParam")
    }

    private fun createRequestingUserParameterData(requestingUser: RequestingUser): RequestMethodData.RequestParameterData {
        return RequestMethodData.RequestParameterData(User::class.java, requestingUser)
    }

    private fun createContextParameterData(): RequestMethodData.RequestParameterData {
        return RequestMethodData.RequestParameterData(Context::class.java, null)
    }


}