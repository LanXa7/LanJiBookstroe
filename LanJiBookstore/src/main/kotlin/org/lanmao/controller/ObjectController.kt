package org.lanmao.controller

import io.minio.errors.ErrorResponseException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.lanmao.entity.RestBean
import org.lanmao.repository.ImageRepository

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.tinylog.kotlin.Logger

@RestController
class ObjectController(
    val imageRepository: ImageRepository
) {
    @GetMapping("/images/avatar/**")
    fun avatarFetch(request: HttpServletRequest, response: HttpServletResponse) {
        this.fetchImage(request, response)
    }

    @GetMapping("/images/cover/**")
    fun coverFetch(request: HttpServletRequest, response: HttpServletResponse) {
        this.fetchImage(request, response)
    }

    fun fetchImage(request: HttpServletRequest, response: HttpServletResponse) {
        val imagePath = request.servletPath.substring(7)
        val stream = response.outputStream
        if (imagePath.length <= 13) {
            response.status = 404
            stream.println(RestBean.failure<Nothing>(404, "Not found").toString())
        } else {
            try {
                imageRepository.fetchImageFromMinio(stream, imagePath)
                Logger.info("获取图片成功")
                response.setHeader("Cache-Control", "max-age=2592000")
            } catch (e: ErrorResponseException) {
                if (e.response().code == 404) {
                    response.status = 404
                    stream.println(RestBean.failure<Nothing>(404, "Not found").toString())
                } else {
                    Logger.error(e, "从Minio获取图片出现异常：{}", e.message)
                }
            }
        }
    }



}