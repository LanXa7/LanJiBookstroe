package org.lanmao.model

import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Id

@Entity
interface AccountDetails {
    @Id
    val id: Int
    val gender: Int?
    val age: Int?
    val phone: String?
    val identificationNumber: String?
    val address: String?
    val synopsis: String?

}