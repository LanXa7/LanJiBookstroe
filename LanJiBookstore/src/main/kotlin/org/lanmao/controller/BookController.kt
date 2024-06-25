package org.lanmao.controller

import org.babyfish.jimmer.Page
import org.lanmao.entity.RestBean
import org.lanmao.model.Book
import org.lanmao.model.dto.BookChangeStatusInput
import org.lanmao.model.dto.BookInsertInput
import org.lanmao.model.dto.BookPage
import org.lanmao.model.dto.BookUpdateInput
import org.lanmao.repository.BookRepository
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/book")
class BookController(val bookRepository: BookRepository) {

    @GetMapping("/list")
    fun getBookList(): RestBean<List<Book>> =
        RestBean.success(bookRepository.getBookList())

    @GetMapping("/page")
    fun getBooksPage(
        @RequestParam
        pageIndex: Int,
        @RequestParam
        pageSize: Int,
        @RequestParam
        searchText: String?,
        @RequestParam
        typeId: Int?
    ): RestBean<Page<BookPage>> {
        val books = bookRepository.findBooks(pageIndex, pageSize, searchText, typeId)
        return RestBean.success(books)
    }

    @GetMapping("/admin-page")
    fun adminGetBooksPage(
        @RequestParam
        pageIndex: Int,
        @RequestParam
        pageSize: Int,
        @RequestParam
        searchText: String?,
        @RequestParam
        typeId: Int?
    ): RestBean<Page<BookPage>> {
        val books = bookRepository.adminFindBooks(pageIndex, pageSize, searchText, typeId)
        return RestBean.success(books)
    }

    @PostMapping("/add")
    fun addBook(@RequestBody bookInsertInput: BookInsertInput): RestBean<Void> {
        val result = bookRepository.addBook(bookInsertInput)
        return if (result)
            RestBean.success()
        else
            RestBean.failure(400, "内部错误，请联系管理员")
    }

    @PostMapping("/update")
    fun updateBook(@RequestBody bookUpdateInput: BookUpdateInput): RestBean<Void> {
        println(bookUpdateInput)
        val result = bookRepository.updateBook(bookUpdateInput)
        return if (result)
            RestBean.success()
        else
            RestBean.failure(400, "内部错误，请联系管理员")
    }

    @PostMapping("/change-status")
    fun changeStatus(@RequestBody bookChangeStatusInput: BookChangeStatusInput): RestBean<Void> {
        val result = bookRepository.changeBookStatus(bookChangeStatusInput)
        return if (result)
            RestBean.success()
        else
            RestBean.failure(400, "内部错误，请联系管理员")
    }

    @GetMapping("/delete")
    fun deleteBook(@RequestParam id: Int): RestBean<Void> {
        println(id)
        val result = bookRepository.deleteBook(id)
        return if (result)
            RestBean.success()
        else
            RestBean.failure(400, "内部错误，请联系管理员")
    }

}