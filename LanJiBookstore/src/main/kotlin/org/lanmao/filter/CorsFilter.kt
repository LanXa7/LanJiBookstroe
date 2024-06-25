package org.lanmao.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpFilter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.lanmao.utils.Const
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(Const.ORDER_CORS)
class CorsFilter : HttpFilter() {
    override fun doFilter(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        this.addCorsHeaders(request, response)
        filterChain.doFilter(request, response)
    }

    private fun addCorsHeaders(
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        response.addHeader("Access-Control-Allow-Origin", request.getHeader("Origin"))
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
        response.addHeader("Access-Control-Allow-Headers", "Authorization, Content-Type")
    }
}