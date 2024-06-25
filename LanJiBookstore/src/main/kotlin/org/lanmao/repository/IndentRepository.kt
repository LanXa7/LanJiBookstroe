package org.lanmao.repository

import org.babyfish.jimmer.Page
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.ast.expression.`eq?`
import org.babyfish.jimmer.sql.kt.ast.expression.or
import org.lanmao.model.*
import org.lanmao.model.dto.ChangeIndentInput
import org.lanmao.model.dto.ChangeStatusInput
import org.lanmao.model.dto.IndentPage
import org.lanmao.model.dto.PersonChangeIndentInput
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class IndentRepository(
    val sqlClient: KSqlClient
) {
    fun findIndents(
        pageIndex: Int,
        pageSize: Int,
        searchText: String?,
        accountId: Int
    ): Page<IndentPage> {
        val result = sqlClient
            .createQuery(Indent::class) {
                where(
                    table.accountId eq accountId,
                    table.book.name `eq?` searchText
                )
                select(
                    table.fetch(IndentPage::class)
                )
            }
            .fetchPage(pageIndex, pageSize)
        return result
    }

    fun findIndents(
        pageIndex: Int,
        pageSize: Int,
        searchText: String?,
    ): Page<IndentPage> {
        val result = sqlClient
            .createQuery(Indent::class) {
                val id = searchText?.toIntOrNull()
                where(
                    or(
                        table.id `eq?` id,
                        table.account.username `eq?` searchText
                    )
                )
                select(
                    table.fetch(IndentPage::class)
                )
            }
            .fetchPage(pageIndex, pageSize)
        return result
    }

    fun changeStatus(changeStatusInput: ChangeStatusInput): Boolean {
        val entity = changeStatusInput.toEntity().copy {
            when (this.status) {
                1 -> paymentTime = Date()
                2 -> shippingTime = Date()
                3 -> finishTime = Date()
            }
        }
        println(entity)
        return sqlClient
            .update(entity).totalAffectedRowCount > 0
    }

    fun personChangeIndent(personChangeIndentInput: PersonChangeIndentInput): Boolean =
        sqlClient
            .update(personChangeIndentInput).totalAffectedRowCount > 0

    fun changeIndent(changeIndentInput: ChangeIndentInput): Boolean =
        sqlClient
            .update(changeIndentInput).totalAffectedRowCount > 0


}