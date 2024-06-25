package org.lanmao.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.TimeUnit

@Component
class JwtUtils(
    @Value("\${spring.security.jwt.key}")
    val key: String,
    @Value("\${spring.security.jwt.expire}")
    val expire: Int,
    val redisTemplate: StringRedisTemplate
) {
    /**
     * 创建JWT
     */
    fun createJWT(
        details: UserDetails,
        id: Int,
        username: String
    ): String {
        val algorithm = Algorithm.HMAC256(key)
        val expire = expireTime()
        return JWT.create()
            .withJWTId(UUID.randomUUID().toString())
            .withClaim("id", id)
            .withClaim("name", username)
            .withClaim("authorities", details.authorities.stream().map { it.authority }.toList())
            .withExpiresAt(expire)
            .withIssuedAt(Date())
            .sign(algorithm)
    }

    /**
     * 解析JWT
     */
    fun resolveJwt(headerToken: String?): DecodedJWT? {
        val token = convertToken(headerToken) ?: return null
        val algorithm = Algorithm.HMAC256(key)
        val jwtVerifier = JWT.require(algorithm).build()
        return try {
            val jwt = jwtVerifier.verify(token)
            if (this.isInvalidToken(jwt.id))
                return null
            if (Date() > jwt.expiresAt) null else jwt
        } catch (e: JWTVerificationException) {
            null
        }
    }

    /**
     * 令JWT失效
     */
    fun invalidateJwt(headerToken: String?): Boolean {
        val token = convertToken(headerToken) ?: return false
        println(token)
        val algorithm = Algorithm.HMAC256(key)
        val jwtVerifier = JWT.require(algorithm).build()
        return try {
            val jwt = jwtVerifier.verify(token)
            val id = jwt.id
            deleteToken(id, jwt.expiresAt)
        } catch (e: JWTVerificationException) {
            return false
        }
    }

    /**
     * 删除redis里的token
     */
    private fun deleteToken(uuid: String, time: Date): Boolean {
        if (this.isInvalidToken(uuid))
            return false
        val now = Date()
        val expire = (time.time - now.time).coerceAtLeast(0)
        redisTemplate.opsForValue().set(Const.JWT_BLACK_LIST + uuid, "", expire, TimeUnit.MILLISECONDS)
        return true
    }

    /**
     * token是否存在
     */
    private fun isInvalidToken(uuid: String): Boolean =
        redisTemplate.hasKey(Const.JWT_BLACK_LIST + uuid)


    /**
     * 转换token
     */
    private fun convertToken(headerToken: String?): String? =
        headerToken?.takeIf { it.startsWith("Bearer ") }?.substring(7)

    /**
     * 通过jwt封装用户信息
     */
    fun toUser(jwt: DecodedJWT): UserDetails {
        val claims = jwt.claims
        val username = claims["name"]?.asString() ?: throw IllegalArgumentException("Username claim is missing or null")
        val authorities =
            claims["authorities"]?.asArray(String::class.java)?.map { SimpleGrantedAuthority(it) } ?: emptyList()

        return User
            .withUsername(username)
            .password("******") // 这里应该使用密码编码器来设置密码
            .authorities(authorities)
            .build()
    }

    fun toId(jwt: DecodedJWT): Int {
        val claims = jwt.claims
        return claims["id"]?.asInt() ?: throw IllegalArgumentException("ID claim is missing or null")
    }

    fun expireTime(): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.HOUR, expire * 24)
        return calendar.time
    }
}