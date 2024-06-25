package org.lanmao.config

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.lanmao.entity.RestBean
import org.lanmao.entity.vo.response.AuthorizeVO
import org.lanmao.filter.JwtAuthorizeFilter
import org.lanmao.repository.AccountRepository
import org.lanmao.utils.JwtUtils
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.logout.LogoutFilter

@Configuration
class SecurityConfiguration(
    val jwtUtils: JwtUtils,
    val jwtAuthorizeFilter: JwtAuthorizeFilter,
    val accountRepository: AccountRepository
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .authorizeHttpRequests {
                it
                    .requestMatchers("/api/auth/**","/error").permitAll()
                    .requestMatchers("/images/**").permitAll()
                    .requestMatchers("/book/list","/book/page","/type/list").permitAll()
                    .anyRequest().authenticated()
            }
            .formLogin {
                it
                    .loginProcessingUrl("/api/auth/login")
                    .successHandler { request, response, authentication ->
                        onAuthenticationSuccess(request, response, authentication)
                    }
                    .failureHandler { request, response, exception ->
                        onAuthenticationFailure(request, response, exception)
                    }
            }
            .logout {
                it
                    .logoutUrl("/api/auth/logout")
                    .logoutSuccessHandler { request, response, authentication ->
                        onLogoutSuccess(request, response, authentication)
                    }
            }
            .exceptionHandling {
                it
                    .authenticationEntryPoint { request, response, authException ->
                        onUnauthorized(request, response, authException)
                    }
                    .accessDeniedHandler { request, response, accessDeniedException ->
                        onAccessDeny(request, response, accessDeniedException)
                    }
            }
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .addFilterBefore(jwtAuthorizeFilter, UsernamePasswordAuthenticationFilter::class.java)
            .addFilterBefore(jwtAuthorizeFilter, LogoutFilter::class.java)
            .build()

    fun formatting(response: HttpServletResponse) {
        response.contentType = "application/json"
        response.characterEncoding = "utf-8"
    }

    fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        formatting(response)
        val user = authentication.principal as User
        val account = accountRepository.findAccountByNameOrEmail(user.username)
        val token = account?.let { jwtUtils.createJWT(user, it.id, account.username) }
        val vo = account?.let { AuthorizeVO(it.username, account.role, token, jwtUtils.expireTime()) }
        response.writer.write(RestBean.success(vo).asJsonString())
    }


    fun onLogoutSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        formatting(response)
        val writer = response.writer
        val authorization = request.getHeader("Authorization")
        if (jwtUtils.invalidateJwt(authorization)) {
            writer.write(RestBean.success(null).asJsonString())
        } else {
            writer.write(RestBean.failure<Nothing>(400, "退出登录失败").asJsonString())
        }
    }

    fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        formatting(response)
        response.writer.write(RestBean.unauthorized<Nothing>(exception.message).asJsonString())
    }

    fun onUnauthorized(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        formatting(response)
        response.writer.write(RestBean.unauthorized<Nothing>(exception.message).asJsonString())
    }

    fun onAccessDeny(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException,
    ) {
        formatting(response)
        response.writer.write(RestBean.forbidden<Nothing>(accessDeniedException.message).asJsonString())
    }


}