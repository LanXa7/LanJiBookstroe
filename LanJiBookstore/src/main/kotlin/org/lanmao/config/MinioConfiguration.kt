package org.lanmao.config

import io.minio.MinioClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.tinylog.kotlin.Logger

@Configuration
class MinioConfiguration(
    @Value("\${spring.minio.endpoint}")
    val endpoint: String,
    @Value("\${spring.minio.username}")
    val username: String,
    @Value("\${spring.minio.password}")
    val password: String
) {

    @Bean
    fun minioClient(): MinioClient {
        Logger.info("Init minio client...")
        return MinioClient.builder()
            .endpoint(endpoint)
            .credentials(username, password)
            .build()
    }
}