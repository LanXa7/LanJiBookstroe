package org.lanmao.config

import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.QueueBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitConfiguration {
    @Bean("emailQueue")
    fun emailQueue(): Queue =
        QueueBuilder
            .durable("mail")
            .build()

}