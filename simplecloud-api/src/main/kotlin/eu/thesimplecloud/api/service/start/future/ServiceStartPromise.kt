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
import eu.thesimplecloud.clientserverapi.lib.promise.flatten
import io.netty.util.concurrent.Future
import io.netty.util.concurrent.GenericFutureListener
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

/**
 * Created by IntelliJ IDEA.
 * Date: 20.12.2020
 * Time: 18:49
 * @author Frederick Baier
 */
class ServiceStartPromise(
    private val delegatePromise: ICommunicationPromise<ICloudService>
) : IServiceStartPromise {

    override fun onServiceRegistered(consumer: Consumer<ICloudService>) {
        delegatePromise.then { consumer.accept(it) }
    }

    override fun onServiceStarting(consumer: Consumer<ICloudService>) {
        delegatePromise.then { it.createStartingPromise() }.flatten(timeoutEnabled = false)
            .then { consumer.accept(it.cloudService) }
    }

    override fun onServiceStarted(consumer: Consumer<ICloudService>) {
        delegatePromise.then { it.createStartedPromise() }.flatten(timeoutEnabled = false)
            .then { consumer.accept(it.cloudService) }
    }

    override fun addCommunicationPromiseListeners(vararg listener: ICommunicationPromiseListener<ICloudService>): ICommunicationPromise<ICloudService> {
        return delegatePromise.addCommunicationPromiseListeners(*listener)
    }

    override fun addCompleteListener(listener: (ICommunicationPromise<ICloudService>) -> Unit): ICommunicationPromise<ICloudService> {
        return delegatePromise.addCompleteListener(listener)
    }

    override fun addCompleteListener(listener: ICommunicationPromiseListener<ICloudService>): ICommunicationPromise<ICloudService> {
        return delegatePromise.addCompleteListener(listener)
    }

    override fun addFailureListener(listener: (Throwable) -> Unit): ICommunicationPromise<ICloudService> {
        return delegatePromise.addFailureListener(listener)
    }

    override fun addListener(listener: GenericFutureListener<out Future<in ICloudService>>?): ICommunicationPromise<ICloudService> {
        return delegatePromise.addListener(listener)
    }

    override fun addListeners(vararg listeners: GenericFutureListener<out Future<in ICloudService>>?): ICommunicationPromise<ICloudService> {
        return delegatePromise.addListeners(*listeners)
    }

    override fun addResultListener(listener: (ICloudService) -> Unit): ICommunicationPromise<ICloudService> {
        return delegatePromise.addResultListener(listener)
    }

    override fun await(): ICommunicationPromise<ICloudService> {
        return delegatePromise.await()
    }

    override fun await(timeout: Long, unit: TimeUnit?): Boolean {
        return delegatePromise.await(timeout, unit)
    }

    override fun await(timeoutMillis: Long): Boolean {
        return delegatePromise.await(timeoutMillis)
    }

    override fun awaitUninterruptibly(): ICommunicationPromise<ICloudService> {
        return delegatePromise.awaitUninterruptibly()
    }

    override fun awaitUninterruptibly(timeout: Long, unit: TimeUnit?): Boolean {
        return delegatePromise.awaitUninterruptibly(timeout, unit)
    }

    override fun awaitUninterruptibly(timeoutMillis: Long): Boolean {
        return delegatePromise.awaitUninterruptibly(timeoutMillis)
    }

    override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
        return delegatePromise.cancel(mayInterruptIfRunning)
    }

    override fun cause(): Throwable {
        return delegatePromise.cause()
    }

    override fun combine(
        communicationPromise: ICommunicationPromise<*>,
        sumUpTimeouts: Boolean
    ): ICommunicationPromise<Unit> {
        return delegatePromise.combine(communicationPromise, sumUpTimeouts)
    }

    override fun combineAll(
        promises: List<ICommunicationPromise<*>>,
        sumUpTimeouts: Boolean
    ): ICommunicationPromise<Unit> {
        return delegatePromise.combineAll(promises, sumUpTimeouts)
    }

    override fun copyStateFromOtherPromise(otherPromise: ICommunicationPromise<ICloudService>) {
        return delegatePromise.copyStateFromOtherPromise(otherPromise)
    }

    override fun get(): ICloudService {
        return delegatePromise.get()
    }

    override fun get(timeout: Long, unit: TimeUnit): ICloudService {
        return delegatePromise.get(timeout, unit)
    }

    override fun getNow(): ICloudService? {
        return delegatePromise.getNow()
    }

    override fun getTimeout(): Long {
        return delegatePromise.getTimeout()
    }

    override fun isCancellable(): Boolean {
        return delegatePromise.isCancellable
    }

    override fun isCancelled(): Boolean {
        return delegatePromise.isCancelled
    }

    override fun isDone(): Boolean {
        return delegatePromise.isDone
    }

    override fun isSuccess(): Boolean {
        return delegatePromise.isSuccess
    }

    override fun isTimeoutEnabled(): Boolean {
        return delegatePromise.isTimeoutEnabled()
    }

    override fun removeListener(listener: GenericFutureListener<out Future<in ICloudService>>?): ICommunicationPromise<ICloudService> {
        return delegatePromise.removeListener(listener)
    }

    override fun removeListeners(vararg listeners: GenericFutureListener<out Future<in ICloudService>>?): ICommunicationPromise<ICloudService> {
        return delegatePromise.removeListeners(*listeners)
    }

    override fun setFailure(cause: Throwable): ICommunicationPromise<ICloudService> {
        return delegatePromise.setFailure(cause)
    }

    override fun setSuccess(result: ICloudService): ICommunicationPromise<ICloudService> {
        return delegatePromise.setSuccess(result)
    }

    override fun setUncancellable(): Boolean {
        return delegatePromise.setUncancellable()
    }

    override fun sync(): ICommunicationPromise<ICloudService> {
        return delegatePromise.sync()
    }

    override fun syncUninterruptibly(): ICommunicationPromise<ICloudService> {
        return delegatePromise.syncUninterruptibly()
    }

    override fun <R : Any> thenDelayed(
        delay: Long,
        timeUnit: TimeUnit,
        function: (ICloudService) -> R?
    ): ICommunicationPromise<R> {
        return delegatePromise.thenDelayed(delay, timeUnit, function)
    }

    override fun tryFailure(cause: Throwable?): Boolean {
        return delegatePromise.tryFailure(cause)
    }

    override fun trySuccess(result: ICloudService?): Boolean {
        return delegatePromise.trySuccess(result)
    }


}