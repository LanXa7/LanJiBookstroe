package org.lanmao.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpFilter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.lanmao.entity.RestBean
import org.lanmao.utils.Const
import org.springframework.core.annotation.Order
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
@Order(Const.ORDER_LIMIT)
class FlowLimitFilter(val redisTemplate: StringRedisTemplate) : HttpFilter() {

    override fun doFilter(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain
    ) {
        val address = request.remoteAddr
        if (this.tryCount(address)) {
            chain.doFilter(request, response)
        } else {
            this.writeBlockMessage(response)
        }
    }

    private fun writeBlockMessage(response: HttpServletResponse) {
        response.status = HttpServletResponse.SC_FORBIDDEN
        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"
        response.writer.write(RestBean.forbidden<Nothing>("操作频繁，请稍后再试").asJsonString())
    }

    fun tryCount(ip: String): Boolean {
        synchronized(ip.intern()) {
            if (redisTemplate.hasKey(Const.FLOW_LIMIT_BLOCK + ip))
                return false
            return this.limitPeriodCheck(ip)
        }
    }

    private fun limitPeriodCheck(ip: String): Boolean {
        if (redisTemplate.hasKey(Const.FLOW_LIMIT_COUNTER + ip)) {
            val increment = redisTemplate.opsForValue().increment(Const.FLOW_LIMIT_COUNTER + ip) ?: 0L
            if (increment > 1000) {
                redisTemplate.opsForValue().set(Const.FLOW_LIMIT_BLOCK + ip, "", 30, TimeUnit.SECONDS)
                return false
            }
        } else {
            redisTemplate.opsForValue().set(Const.FLOW_LIMIT_COUNTER + ip, "1", 3, TimeUnit.SECONDS)
        }
        return true
    }

}