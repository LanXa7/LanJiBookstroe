package org.lanmao.repository

import org.babyfish.jimmer.View
import org.babyfish.jimmer.kt.new
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.lanmao.entity.vo.request.DetailsSaveVO
import org.lanmao.model.*
import org.springframework.beans.BeanUtils
import org.springframework.stereotype.Repository
import kotlin.reflect.KClass

@Repository
class AccountDetailsRepository(
    val sqlClient: KSqlClient,
    val accountRepository: AccountRepository
) {

    fun <V : View<AccountDetails>> findAccountById(
        id: Int,
        viewType: KClass<V>
    ): V? =
        sqlClient.findById(
            viewType,
            id
        )

    fun saveAccountDetails(userId: Int, vo: DetailsSaveVO): Boolean {
        val account = accountRepository.findAccountByNameOrEmail(vo.username)
        if (account == null || account.id == userId) {
            sqlClient
                .createUpdate(Account::class) {
                    set(table.username, vo.username)
                    where(table.id eq userId)
                }
                .execute()
            sqlClient
                .save(new(AccountDetails::class).by {
                    BeanUtils.copyProperties(vo, this)
                    id = userId
//                    gender = vo.gender
//                    age = vo.age
//                    phone = vo.phone
//                    identificationNumber = vo.identificationNumber
//                    address = vo.address
//                    synopsis = vo.synopsis
                })

            return true
        }
        return false
    }

}