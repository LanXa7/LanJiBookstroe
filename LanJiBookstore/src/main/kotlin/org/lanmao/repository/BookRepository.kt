package org.lanmao.repository

import org.babyfish.jimmer.Page
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.ast.expression.`eq?`
import org.babyfish.jimmer.sql.kt.ast.expression.`ilike?`
import org.babyfish.jimmer.sql.kt.ast.expression.or
import org.lanmao.model.*
import org.lanmao.model.dto.BookChangeStatusInput
import org.lanmao.model.dto.BookInsertInput
import org.lanmao.model.dto.BookPage
import org.lanmao.model.dto.BookUpdateInput
import org.springframework.stereotype.Repository

@Repository
class BookRepository(val sqlClient: KSqlClient) {

    fun findBooks(
        pageIndex: Int,
        pageSize: Int,
        searchText: String?,
        typeId: Int?
    ): Page<BookPage> {
        val result = sqlClient
            .createQuery(Book::class) {
                where(
                    or(
                        table.name `ilike?` searchText,
                        table.author `ilike?` searchText,
                        table.typeId `eq?` typeId
                    ),
                    table.status eq 0
                )
                select(
                    table.fetch(BookPage::class)
                )
            }
            .fetchPage(pageIndex, pageSize)
        return result
    }

    fun adminFindBooks(
        pageIndex: Int,
        pageSize: Int,
        searchText: String?,
        typeId: Int?
    ): Page<BookPage> {
        val result = sqlClient
            .createQuery(Book::class) {
                where(
                    or(
                        table.name `ilike?` searchText,
                        table.author `ilike?` searchText,
                        table.typeId `eq?` typeId
                    )
                )
                select(
                    table.fetch(BookPage::class)
                )
            }
            .fetchPage(pageIndex, pageSize)
        return result
    }

    fun getBookList(): List<Book> = sqlClient
        .createQuery(Book::class) {
            select(table)
        }
        .execute()


    fun addBook(
        bookInsertInput: BookInsertInput
    ): Boolean {
        return sqlClient.insert(bookInsertInput).totalAffectedRowCount > 0
    }

    fun updateBook(
        bookUpdateInput: BookUpdateInput
    ): Boolean {
        return sqlClient.update(bookUpdateInput).totalAffectedRowCount > 0
    }

    fun changeBookStatus(
        bookChangeStatusInput: BookChangeStatusInput
    ): Boolean {
        return sqlClient.update(bookChangeStatusInput).totalAffectedRowCount > 0
    }

    fun deleteBook(id: Int): Boolean = try {
        sqlClient
            .createDelete(Book::class) {
                where(table.id eq id)
            }
            .execute()
        true
    } catch (ex: Exception) {
        false
    }


}