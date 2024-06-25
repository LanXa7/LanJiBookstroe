package org.lanmao.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.lanmao.utils.Const
import org.lanmao.utils.JwtUtils
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthorizeFilter(val jwtUtils: JwtUtils) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authorization = request.getHeader("Authorization")
        val jwt = jwtUtils.resolveJwt(authorization)
        if (jwt != null) {
            val user = jwtUtils.toUser(jwt)
            val authentication = UsernamePasswordAuthenticationToken(user, null, user.authorities)
            authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
            SecurityContextHolder.getContext().authentication = authentication
            request.setAttribute(Const.ATTR_USER_ID, jwtUtils.toId(jwt))
        }
        filterChain.doFilter(request, response)
    }
}