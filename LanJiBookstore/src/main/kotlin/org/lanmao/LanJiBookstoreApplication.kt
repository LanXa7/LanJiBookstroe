package org.lanmao

import org.babyfish.jimmer.client.EnableImplicitApi
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableImplicitApi
class LanJiBookStoreApplication

fun main(args: Array<String>) {
    runApplication<LanJiBookStoreApplication>(*args)
}
