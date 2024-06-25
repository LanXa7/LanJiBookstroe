package org.lanmao.controller

import org.babyfish.jimmer.sql.kt.KSqlClient
import org.lanmao.entity.RestBean
import org.lanmao.model.Type
import org.lanmao.model.dto.typeInput
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/type")
class TypeController(val sqlClient: KSqlClient) {

    @GetMapping("/list")
    fun typeList(): RestBean<List<Type>> =
        RestBean.success(sqlClient
            .createQuery(Type::class) {
                select(table)
            }
            .execute())

    @PostMapping("/add")
    fun addType(@RequestBody typeInput: typeInput): RestBean<Void> {
        val result = sqlClient
            .insert(typeInput).totalAffectedRowCount > 0
        return if (result)
            RestBean.success()
        else
            RestBean.failure(400, "内部错误，请联系管理员")
    }

    @GetMapping("/delete")
    fun deleteType(@RequestParam id: Int): RestBean<Void> {
        sqlClient
            .deleteById(Type::class, id)
        return RestBean.success()
    }

}