package org.lanmao.controller

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern
import org.lanmao.entity.RestBean
import org.lanmao.entity.vo.request.ConfirmResetVO
import org.lanmao.entity.vo.request.EmailRegisterVO
import org.lanmao.entity.vo.request.EmailResetVO
import org.lanmao.repository.AccountRepository
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Validated
@RestController
@RequestMapping("/api/auth")
class AuthorizeController(val accountRepository: AccountRepository) {

    /**
     * 获取验证码
     */
    @GetMapping("/ask-code")
    fun askVerifyCode(
        @RequestParam @Email email: String,
        @RequestParam @Pattern(regexp = "(register|reset|modify)") type: String,
        request: HttpServletRequest
    ): RestBean<Void> {
        val message = accountRepository.registerEmailVerifyCode(type, email, request.remoteAddr)
        return message?.let { RestBean.failure(400, it) } ?: RestBean.success()
    }

    /**
     * 注册用户
     */
    @PostMapping("/register")
    fun register(@RequestBody @Valid vo: EmailRegisterVO): RestBean<Void> {
        val message = accountRepository.registerEmailAccount(vo)
        return message?.let { RestBean.failure(400, it) } ?: RestBean.success()
    }


    @PostMapping("/reset-confirm")
    fun resetConfirm(@RequestBody @Valid vo: ConfirmResetVO): RestBean<Void> {
        val message = accountRepository.resetConfirm(vo)
        return message?.let { RestBean.failure(400, it) } ?: RestBean.success()
    }

    @PostMapping("/reset-password")
    fun resetPassword(@RequestBody @Valid vo: EmailResetVO): RestBean<Void> {
        val message = accountRepository.resetEmailAccountPassword(vo)
        return message?.let { RestBean.failure(400, it) } ?: RestBean.success()
    }
}