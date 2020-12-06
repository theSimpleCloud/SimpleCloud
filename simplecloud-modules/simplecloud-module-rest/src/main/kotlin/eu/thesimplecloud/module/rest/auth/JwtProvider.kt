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


