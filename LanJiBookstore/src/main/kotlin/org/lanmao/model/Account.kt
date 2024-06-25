package org.lanmao.model

import org.babyfish.jimmer.sql.*
import java.util.*

@Entity
interface Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int

    @Key
    val username: String

    val password: String

    @Key
    val email: String

    val role: String

    val avatar: String?

    val registerTime: Date

    @ManyToMany
    val addresses: List<Address>

    @OneToMany(mappedBy = "account")
    val indents: List<Indent>


}