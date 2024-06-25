package org.lanmao.model

import org.babyfish.jimmer.sql.*

@Entity
interface Type {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int

    val name: String

    @OneToOne(mappedBy = "type")
    val book: Book?
}