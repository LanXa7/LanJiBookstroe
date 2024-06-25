package org.lanmao.controller

import org.babyfish.jimmer.Page
import org.lanmao.entity.RestBean
import org.lanmao.model.dto.ChangeIndentInput
import org.lanmao.model.dto.ChangeStatusInput
import org.lanmao.model.dto.IndentPage
import org.lanmao.model.dto.PersonChangeIndentInput
import org.lanmao.repository.IndentRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/indent")
class IndentController(val indentRepository: IndentRepository) {
    @GetMapping("/person-page")
    fun getBooksPage(
        @RequestParam
        pageIndex: Int,
        @RequestParam
        pageSize: Int,
        @RequestParam
        searchText: String?,
        @RequestParam
        accountId: Int
    ): RestBean<Page<IndentPage>> {
        val books = indentRepository.findIndents(pageIndex, pageSize, searchText, accountId)
        return RestBean.success(books)
    }

    @GetMapping("/page")
    fun getBooksPage(
        @RequestParam
        pageIndex: Int,
        @RequestParam
        pageSize: Int,
        @RequestParam
        searchText: String?,
    ): RestBean<Page<IndentPage>> {
        val books = indentRepository.findIndents(pageIndex, pageSize, searchText)
        return RestBean.success(books)
    }

    @PostMapping("/status")
    fun changeStatus(@RequestBody changeStatusInput: ChangeStatusInput): RestBean<Void> {
        println(changeStatusInput)
        val result = indentRepository.changeStatus(changeStatusInput)
        return if (result)
            RestBean.success()
        else
            RestBean.failure(400, "内部错误，请联系管理员")
    }

    @PostMapping("/person-update")
    fun update(@RequestBody personChangeIndentInput: PersonChangeIndentInput): RestBean<Void> {
        val result = indentRepository.personChangeIndent(personChangeIndentInput)
        return if (result)
            RestBean.success()
        else
            RestBean.failure(400, "内部错误，请联系管理员")
    }

    @PostMapping("/update")
    fun update(@RequestBody changeIndentInput: ChangeIndentInput): RestBean<Void> {
        val result = indentRepository.changeIndent(changeIndentInput)
        return if (result)
            RestBean.success()
        else
            RestBean.failure(400, "内部错误，请联系管理员")
    }


}