package org.lanmao.model

import org.babyfish.jimmer.sql.*
import java.util.*

@Entity
interface Indent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int

    val status: Int

    val createTime: Date

    val paymentTime: Date?

    val shippingTime: Date?

    val finishTime: Date?

    val totalPrice: Double

    val totalNumber: Int

    @ManyToOne
    val account: Account

    @OneToOne
    val book: Book

    @OneToOne
    val address:Address

}