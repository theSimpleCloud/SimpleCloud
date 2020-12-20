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

package eu.thesimplecloud.api.service.start.future

import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromiseListener
import io.netty.util.concurrent.Future
import io.netty.util.concurrent.GenericFutureListener
import java.util.function.Consumer

/**
 * Created by IntelliJ IDEA.
 * Date: 20.12.2020
 * Time: 18:49
 * @author Frederick Baier
 */
interface IServiceStartPromise : ICommunicationPromise<ICloudService> {

    /**
     * Calls the specified [consumer] when the service was registered
     */
    fun onServiceRegistered(consumer: Consumer<ICloudService>)

    /**
     * Calls the specified [consumer] when the service is starting
     */
    fun onServiceStarting(consumer: Consumer<ICloudService>)

    /**
     * Calls the specified [consumer] when the service was started
     */
    fun onServiceStarted(consumer: Consumer<ICloudService>)


    override fun addCommunicationPromiseListeners(vararg listener: ICommunicationPromiseListener<ICloudService>): IServiceStartPromise

    override fun addCompleteListener(listener: (ICommunicationPromise<ICloudService>) -> Unit): IServiceStartPromise

    override fun addCompleteListener(listener: ICommunicationPromiseListener<ICloudService>): IServiceStartPromise

    override fun addFailureListener(listener: (Throwable) -> Unit): IServiceStartPromise

    override fun addListener(listener: GenericFutureListener<out Future<in ICloudService>>?): IServiceStartPromise

    override fun addListeners(vararg listeners: GenericFutureListener<out Future<in ICloudService>>?): IServiceStartPromise

    override fun addResultListener(listener: (ICloudService) -> Unit): IServiceStartPromise

    override fun await(): IServiceStartPromise

    override fun awaitUninterruptibly(): IServiceStartPromise

    override fun removeListener(listener: GenericFutureListener<out Future<in ICloudService>>?): IServiceStartPromise

    override fun removeListeners(vararg listeners: GenericFutureListener<out Future<in ICloudService>>?): IServiceStartPromise

    override fun setFailure(cause: Throwable): IServiceStartPromise

    override fun setSuccess(result: ICloudService): IServiceStartPromise

    override fun sync(): IServiceStartPromise

    override fun syncUninterruptibly(): ICommunicationPromise<ICloudService>


}