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

package eu.thesimplecloud.module.rest.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import eu.thesimplecloud.module.rest.auth.user.User
import javalinjwt.JWTGenerator
import javalinjwt.JWTProvider
import java.util.*
import java.util.concurrent.TimeUnit


class JwtProvider(secret: String) {

    private val jwtAlgorithm: Algorithm = Algorithm.HMAC512(secret)

    private val jwtGenerator = JWTGenerator { user: User, algorithm: Algorithm ->
        val token = JWT.create()
            .withClaim("username", user.username)
            .withExpiresAt(Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7)))

        token.sign(algorithm)
    }
    private val jwtVerifier: JWTVerifier = JWT.require(jwtAlgorithm).build()

    val provider = JWTProvider(jwtAlgorithm, jwtGenerator, jwtVerifier)

    init {
        instance = this
    }

    companion object {
        lateinit var instance: JwtProvider
    }
}


