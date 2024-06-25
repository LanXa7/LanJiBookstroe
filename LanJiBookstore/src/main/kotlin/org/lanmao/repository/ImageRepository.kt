package org.lanmao.repository

import io.minio.GetObjectArgs
import io.minio.MinioClient
import io.minio.PutObjectArgs
import org.apache.commons.compress.utils.IOUtils
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.lanmao.model.*
import org.springframework.stereotype.Repository
import org.springframework.web.multipart.MultipartFile
import org.tinylog.kotlin.Logger
import java.io.OutputStream
import java.util.*

@Repository
class ImageRepository(
    val sqlClient: KSqlClient,
    val client: MinioClient
) {
    fun uploadAvatar(file: MultipartFile, id: Int): String? {
        var imageName = UUID.randomUUID().toString().replace("-", "")
        imageName = "/avatar/$imageName"
        val args = PutObjectArgs.builder()
            .bucket("bookstore")
            .stream(file.inputStream, file.size, -1)
            .`object`(imageName)
            .build()
        try {
            client.putObject(args)
            sqlClient
                .createUpdate(Account::class) {
                    set(table.avatar, imageName)
                    where(table.id.eq(id))
                }
                .execute()
            return imageName
        } catch (ex: Exception) {
            Logger.error(ex, "图片上传异常 {}", ex.message)
            return null
        }
    }

    fun uploadCover(file: MultipartFile, id: Int): String? {
        var imageName = UUID.randomUUID().toString().replace("-", "")
        imageName = "/cover/$imageName"
        val args = PutObjectArgs.builder()
            .bucket("bookstore")
            .stream(file.inputStream, file.size, -1)
            .`object`(imageName)
            .build()
        try {
            client.putObject(args)
            if (id != 0) {
                sqlClient
                    .createUpdate(Book::class) {
                        set(table.cover, imageName)
                        where(table.id.eq(id))
                    }
                    .execute()
            }
            return imageName
        } catch (ex: Exception) {
            Logger.error(ex, "图片上传异常 {}", ex.message)
            return null
        }
    }

    fun fetchImageFromMinio(stream: OutputStream, image: String) {
        val args = GetObjectArgs.builder()
            .bucket("bookstore")
            .`object`(image)
            .build()
        val response = client.getObject(args)
        IOUtils.copy(response, stream)
    }
}