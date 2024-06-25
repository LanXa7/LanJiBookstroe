package org.lanmao.controller

import jakarta.validation.Valid
import org.lanmao.entity.RestBean
import org.lanmao.entity.vo.request.ChangePasswordVO
import org.lanmao.entity.vo.request.DetailsSaveVO
import org.lanmao.entity.vo.request.ModifyEmailVO
import org.lanmao.model.dto.AccountVO

import org.lanmao.model.dto.DetailsVO
import org.lanmao.repository.AccountDetailsRepository
import org.lanmao.repository.AccountRepository
import org.lanmao.utils.Const
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user")
class AccountController(
    val accountRepository: AccountRepository,
    val accountDetailsRepository: AccountDetailsRepository
) {

    @GetMapping("/info")
    fun info(@RequestAttribute(Const.ATTR_USER_ID) id: Int): RestBean<AccountVO> {
        val account = accountRepository.findAccountById(id, AccountVO::class)
        return RestBean.success(account)
    }

    @GetMapping("/details")
    fun details(@RequestAttribute(Const.ATTR_USER_ID) id: Int): RestBean<DetailsVO> {
        val details = accountDetailsRepository.findAccountById(id, DetailsVO::class)
        return RestBean.success(details)
    }

    @PostMapping("/save-details")
    fun saveDetails(
        @RequestAttribute(Const.ATTR_USER_ID) id: Int,
        @RequestBody @Valid vo: DetailsSaveVO
    ): RestBean<Void> {
        val success = accountDetailsRepository.saveAccountDetails(id, vo)
        if (!success)
            return RestBean.failure(400, "此用户名已被其它用户使用，请重新更换！")
        return RestBean.success()
    }

    @PostMapping("/modify-email")
    fun modifyEmail(
        @RequestAttribute(Const.ATTR_USER_ID) id: Int,
        @RequestBody @Valid vo: ModifyEmailVO
    ): RestBean<Void> {
        val result = accountRepository.modifyEmail(id, vo)
        return result?.let { RestBean.failure(400, it) } ?: RestBean.success()
    }

    @PostMapping("/change-password")
    fun changePassword(
        @RequestAttribute(Const.ATTR_USER_ID) id: Int,
        @RequestBody @Valid vo: ChangePasswordVO
    ): RestBean<Void> {
        val result = accountRepository.changePassword(id, vo)
        return result?.let { RestBean.failure(400, it) } ?: RestBean.success()
    }

}