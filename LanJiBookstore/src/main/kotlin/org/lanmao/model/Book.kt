package org.lanmao.model

import org.babyfish.jimmer.sql.*

@Entity
interface Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int

    val name: String

    val cover: String?

    val author: String

    val press: String

    val status: Int

    @Key
    val isbn: String

    val number: Int

    val price: Double

    @OneToOne
    val type: Type

    @OneToOne(mappedBy = "book")
    val indent:Indent?

}