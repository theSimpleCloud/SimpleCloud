package eu.thesimplecloud.module.rest.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import eu.thesimplecloud.module.rest.auth.user.User
import javalinjwt.JWTGenerator
import javalinjwt.JWTProvider
import java.util.*
import java.util.concurrent.TimeUnit


object JwtProvider {

    private val jwtAlgorithm: Algorithm = Algorithm.HMAC512("MmNkOGE5NzBiMDJhNGRkYmUyMjcyZjA1ZjNmODE4YzlkZGMyN2RjYWMwZTM2NzRhOWNmZDlmNTBmYWQ1MmM0ZTk2YjdlZWUxODc3NWJjYjgxNjk0MTVkMzVkODc4NjljM2ZmOTExYmFhMzk3MTM0MjE4ZDVjMmFjNjY3MWI0NTM=")

    private val jwtGenerator = JWTGenerator { user: User, algorithm: Algorithm ->
        val token = JWT.create()
                .withClaim("username", user.username)
                .withExpiresAt(Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7)))

        token.sign(algorithm)
    }
    private val jwtVerifier: JWTVerifier = JWT.require(jwtAlgorithm).build()

    val provider = JWTProvider(jwtAlgorithm, jwtGenerator, jwtVerifier)
}


