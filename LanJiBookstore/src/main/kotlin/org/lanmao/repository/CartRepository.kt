package org.lanmao.repository

import org.babyfish.jimmer.sql.kt.KSqlClient
import org.lanmao.entity.vo.request.Item
import org.lanmao.entity.vo.request.ItemChangeVO
import org.lanmao.model.copy
import org.lanmao.model.dto.BuyBookInput
import org.lanmao.utils.Const
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class CartRepository(
    val redisTemplate: RedisTemplate<Any, Any>,
    val sqlClient: KSqlClient,
) {

    private fun getMap(userid: Int, key: String): HashMap<Int, Item> {
        val map = redisTemplate.opsForValue().get(key)
        val bookMap: HashMap<Int, Item>

        if (map != null && map is HashMap<*, *>) {
            @Suppress("UNCHECKED_CAST")
            return map as HashMap<Int, Item>
        } else {
            //如果是第一次添加购物车
            return hashMapOf()
        }
    }


    fun addCart(userid: Int, book: Item): Boolean {
        //用户在redis存储的key
        val key = Const.CART + userid
        val bookMap = getMap(userid, key)
        //判断图书是否已经存在
        if (bookMap.containsKey(book.id)) {
            println(bookMap[book.id])
            val item = bookMap[book.id] as Item
            item.number += book.number
            redisTemplate.opsForValue().set(key, bookMap)
        } else {
            //如果图书不存在,放入到map中
            val item = book
            bookMap[book.id] = item
            redisTemplate.opsForValue().set(key, bookMap)
        }
        return true
    }

    fun showCart(userid: Int): List<Item>? {
        val key = Const.CART + userid
        val bookMap = getMap(userid, key)
        val list = bookMap.values.toList()
        return list
    }

    fun updateNumber(userid: Int, book: ItemChangeVO): Boolean {
        val key = Const.CART + userid
        val bookMap = getMap(userid, key)
        val number = book.number
        val bookId = book.id
        val item: Item = bookMap[bookId] as Item
        item.number = number
        redisTemplate.opsForValue().set(key, bookMap)
        return true
    }

    fun deleteCartById(userid: Int, bookId: Int): Boolean {
        val key = Const.CART + userid
        val bookMap = getMap(userid, key)
        bookMap.remove(bookId)
        redisTemplate.opsForValue().set(key, bookMap)
        return true
    }

    fun deleteCartByIds(userid: Int, bookIds: List<Int>): Boolean {
        for (bookId in bookIds) {
            deleteCartById(userid, bookId)
        }
        return true
    }

    fun buyBook(buyBookInput: BuyBookInput): Boolean {
        val temp = buyBookInput.toEntity().copy {
            createTime = Date()
        }
        println(temp)
        deleteCartById(buyBookInput.accountId, buyBookInput.bookId)
        return sqlClient.save(temp).totalAffectedRowCount > 0
    }

    fun buyBooks(userId: Int, bookList: List<BuyBookInput>): Boolean {
        val bookIds = bookList.map { it.bookId }
        for (bookId in bookIds) {
            deleteCartById(userId, bookId)
        }
        val entities = bookList.map {
            it.toEntity().copy {
                createTime = Date()
            }

        }.toList()
        return sqlClient
            .entities
            .saveEntities(
                entities
            ).totalAffectedRowCount > 0
    }


}