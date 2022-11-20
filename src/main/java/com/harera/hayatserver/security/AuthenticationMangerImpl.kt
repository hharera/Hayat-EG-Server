package com.harera.hayatserver.security

import com.harera.security.AuthenticationManager
import com.harera.hayatserver.security.utils.Constant.MILLIS_IN_DAY
import com.harera.hayatserver.security.utils.Constant.USER_TOKEN_EXPIRATION_IN_DAYS
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*
import java.util.function.Function

@Service
class AuthenticationMangerImpl : AuthenticationManager {

    @Value("\${secret-key}")
    private lateinit var SECRET_KEY: String

    override fun extractUsernameFromToken(token: String): String {
        return extractClaim(token) { obj: Claims -> obj.subject }
    }

    override fun extractExpiration(token: String?): Date {
        return extractClaim(token) { obj: Claims -> obj.expiration }
    }

    override fun <T> extractClaim(token: String?, claimsResolver: Function<Claims, T>): T {
        val claims = extractAllClaims(token)
        return claimsResolver.apply(claims)
    }

    override fun extractAllClaims(token: String?): Claims {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).body
    }

    override fun isTokenExpired(token: String?): Boolean {
        return extractExpiration(token).before(Date())
    }

    override fun extractUsernameFromAuthorization(barearToken: String): String {
        return extractUsernameFromToken(barearToken.substring(7))
    }

    override fun generateAdminToken(userDetails: UserDetails): String {
        val claims: Map<String, Any> = HashMap()
        return createToken(claims, userDetails.username, MILLIS_IN_DAY)
    }

    override fun generateUserToken(userDetails: UserDetails): String {
        val claims: Map<String, Any> = HashMap()
        val date = DateTime.now().plusDays(USER_TOKEN_EXPIRATION_IN_DAYS).toDate()
        return createToken(claims, userDetails.username, date)
    }

    override fun generateToken(username: String): String {
        val claims: Map<String, Any> = HashMap()
        return createToken(claims, username, MILLIS_IN_DAY * USER_TOKEN_EXPIRATION_IN_DAYS)
    }

    override fun generateDriverToken(userDetails: UserDetails): String {
        val claims: Map<String, Any> = HashMap()
        return createToken(claims, userDetails.username, MILLIS_IN_DAY)
    }

    override fun createToken(claims: Map<String, Any>, subject: String, millis: Int): String {
        return Jwts
            .builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + millis))
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact()
    }

    override fun createToken(claims: Map<String, Any>, subject: String, date: Date): String {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(date)
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact()
    }

    override fun validateToken(token: String?, userDetails: UserDetails): Boolean {
        if (token.isNullOrBlank()) return false
        val username = extractUsernameFromToken(token)
        return username == userDetails.username && !isTokenExpired(token)
    }
}