package org.lanmao.controller

import org.lanmao.entity.RestBean
import org.lanmao.entity.vo.request.Item
import org.lanmao.entity.vo.request.ItemChangeVO
import org.lanmao.model.dto.BuyBookInput


import org.lanmao.repository.CartRepository
import org.lanmao.utils.Const
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/cart")
class CartController(val cartRepository: CartRepository) {

    @PostMapping("/add")
    fun addCart(
        @RequestAttribute(Const.ATTR_USER_ID) userId: Int,
        @RequestBody book: Item
    ): RestBean<Void> {
        return if (cartRepository.addCart(userId, book))
            RestBean.success()
        else RestBean.failure(400, "内部错误，请联系管理员")
    }

    @GetMapping("/get-cart")
    fun getCart(@RequestAttribute(Const.ATTR_USER_ID) userId: Int): RestBean<List<Item>> {
        val result = cartRepository.showCart(userId)
        return if (result != null)
            RestBean.success(result)
        else
            RestBean.success(null)
    }

    @PostMapping("/update-cart")
    fun updateCart(
        @RequestAttribute(Const.ATTR_USER_ID) userId: Int,
        @RequestBody book: ItemChangeVO
    ): RestBean<Void> {
        return if (cartRepository.updateNumber(userId, book))
            RestBean.success()
        else RestBean.failure(400, "内部错误，请联系管理员")
    }

    @GetMapping("/delete")
    fun deleteCart(
        @RequestAttribute(Const.ATTR_USER_ID) userId: Int,
        @RequestParam bookId: Int
    ): RestBean<Void> {
        return if (cartRepository.deleteCartById(userId, bookId))
            RestBean.success()
        else RestBean.failure(400, "内部错误，请联系管理员")
    }

    @PostMapping("/buy")
    fun buyBook(@RequestBody buyBookInput: BuyBookInput): RestBean<Void> {
        val result = cartRepository.buyBook(buyBookInput)
        return if (result)
            RestBean.success()
        else
            RestBean.failure(400, "内部错误，请联系管理员")
    }


    @PostMapping("/buys")
    fun buyBooks(
        @RequestAttribute(Const.ATTR_USER_ID) userId: Int,
        @RequestBody bookList: List<BuyBookInput>
    ): RestBean<Void> {
        val result = cartRepository.buyBooks(userId, bookList)
        return if (result)
            RestBean.success()
        else
            RestBean.failure(400, "内部错误，请联系管理员")
    }

    @PostMapping("/deleteAll")
    fun deleteAllCart(
        @RequestAttribute(Const.ATTR_USER_ID) userId: Int,
        @RequestBody bookIds: List<Int>
    ): RestBean<Void> {
        val result = cartRepository.deleteCartByIds(userId, bookIds)
        return if (result)
            RestBean.success()
        else
            RestBean.failure(400, "内部错误，请联系管理员")
    }


}