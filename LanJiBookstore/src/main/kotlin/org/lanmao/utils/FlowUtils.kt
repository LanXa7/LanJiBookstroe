package org.lanmao.utils

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class FlowUtils(val redisTemplate: StringRedisTemplate) {
    fun limitOnceCheck(key:String,blockTime:Int):Boolean{
        if(redisTemplate.hasKey(key)){
            return false
        }else{
            redisTemplate.opsForValue().set(key,"",blockTime.toLong(),TimeUnit.SECONDS)
        }
        return true
    }
}