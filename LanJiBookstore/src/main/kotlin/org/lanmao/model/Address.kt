package org.lanmao.model

import org.babyfish.jimmer.sql.*

@Entity
interface Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int

    val name: String

    val phone: String

    val address: String

    @ManyToMany(mappedBy = "addresses")
    val accounts: List<Account>

    @OneToOne(mappedBy = "address")
    val indent:Indent?
}