package org.lanmao.config

import org.lanmao.utils.FastJson2JsonRedisSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration

class RedisConfiguration {


    @Bean
    fun redisTemplate(
        redisConnectionFactory: RedisConnectionFactory
    ): RedisTemplate<Any, Any> {
        val redisTemplate = RedisTemplate<Any, Any>()
        redisTemplate.connectionFactory = redisConnectionFactory

        val serializer: FastJson2JsonRedisSerializer<*> = FastJson2JsonRedisSerializer(Any::class.java)
        // key 采用String的序列化方式
        redisTemplate.keySerializer = StringRedisSerializer()
        // value 序列化方式采用 jackson
        redisTemplate.valueSerializer = serializer
        // hash 的key也采用 String 的序列化方式
        redisTemplate.hashKeySerializer =  StringRedisSerializer()
        // hash 的 value 采用 jackson
        redisTemplate.hashValueSerializer = serializer
        redisTemplate.afterPropertiesSet();
        return redisTemplate
    }

    @Bean
    fun redisIIntTemplate(
        redisConnectionFactory: RedisConnectionFactory
    ): RedisTemplate<Int, Any> {
        val redisTemplate = RedisTemplate<Int, Any>()
        redisTemplate.connectionFactory = redisConnectionFactory

        val serializer: FastJson2JsonRedisSerializer<*> = FastJson2JsonRedisSerializer(Any::class.java)
        // key 采用String的序列化方式
        redisTemplate.keySerializer = StringRedisSerializer()
        // value 序列化方式采用 jackson
        redisTemplate.valueSerializer = serializer
        // hash 的key也采用 String 的序列化方式
        redisTemplate.hashKeySerializer =  StringRedisSerializer()
        // hash 的 value 采用 jackson
        redisTemplate.hashValueSerializer = serializer
        redisTemplate.afterPropertiesSet();
        return redisTemplate
    }


}
