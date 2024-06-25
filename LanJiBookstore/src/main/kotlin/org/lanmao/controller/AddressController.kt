package org.lanmao.controller

import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.lanmao.entity.RestBean
import org.lanmao.model.Account
import org.lanmao.model.Address
import org.lanmao.model.dto.AddressInput
import org.lanmao.model.dto.AddressSaveInput
import org.lanmao.model.fetchBy
import org.lanmao.model.id
import org.lanmao.utils.Const
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/address")
class AddressController(val sqlClient: KSqlClient) {

    @GetMapping("/get-address")
    fun getAddress(@RequestAttribute(Const.ATTR_USER_ID) userId: Int): RestBean<List<Account>> {
        return RestBean.success(
            sqlClient
                .createQuery(Account::class) {
                    where(table.id eq userId)
                    select(table.fetchBy {
                        addresses {
                            allScalarFields()
                        }
                    })
                }.execute()
        )
    }

    @PostMapping("/change")
    fun changeAddress(@RequestBody addressInput: AddressInput): RestBean<Void> {
        return if (sqlClient.update(addressInput).totalAffectedRowCount > 0)
            RestBean.success()
        else
            RestBean.failure(400, "内部错误，请联系管理员")
    }

    @PostMapping("/add")
    fun addAddress(
        @RequestBody addressSaveInput: AddressSaveInput
    ): RestBean<Void> {
        val result = sqlClient
            .save(addressSaveInput).totalAffectedRowCount > 0
        return if (result)
            RestBean.success()
        else
            RestBean.failure(400, "内部错误，请联系管理员")
    }

    @GetMapping("/delete")
    fun deleteAddress(
        @RequestParam addressId: Int
    ): RestBean<Void> {
        sqlClient
            .deleteById(Address::class, addressId)
        return RestBean.success()
    }
}