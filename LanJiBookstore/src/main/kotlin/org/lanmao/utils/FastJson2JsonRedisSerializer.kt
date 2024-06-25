package org.lanmao.utils

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONReader
import com.alibaba.fastjson2.JSONWriter
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.data.redis.serializer.SerializationException
import java.nio.charset.Charset

class FastJson2JsonRedisSerializer<T>(private val clazz: Class<T>) : RedisSerializer<T?> {

    @Throws(SerializationException::class)
    override fun serialize(t: T?): ByteArray {
        return if (t == null) {
            ByteArray(0)
        } else JSON.toJSONString(t, JSONWriter.Feature.WriteClassName)
            .toByteArray(DEFAULT_CHARSET)
    }

    @Throws(SerializationException::class)
    override fun deserialize(bytes: ByteArray?): T? {
        return when {
            bytes == null || bytes.isEmpty() -> {
                null
            }
            else -> {
                val str = String(bytes, DEFAULT_CHARSET)
                JSON.parseObject(str, clazz, JSONReader.Feature.SupportAutoType)
            }
        }
    }

    companion object {
        val DEFAULT_CHARSET: Charset = Charset.forName("UTF-8")
    }
}