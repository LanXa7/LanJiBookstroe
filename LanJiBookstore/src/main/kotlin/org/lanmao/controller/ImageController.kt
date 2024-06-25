package org.lanmao.controller

import org.lanmao.entity.RestBean
import org.lanmao.repository.ImageRepository
import org.lanmao.utils.Const
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.tinylog.kotlin.Logger


@RestController
@RequestMapping("/api/image")
class ImageController(
    val imageRepository: ImageRepository
) {
    @PostMapping("/avatar")
    fun uploadAvatar(
        @RequestParam("file") file: MultipartFile,
        @RequestAttribute(Const.ATTR_USER_ID) id: Int
    ): RestBean<String> {
        if (file.size > 1024 * 1024)
            return RestBean.failure(400, "头像图片不能大于100KB")
        Logger.info("正在进行头像上传操作")
        val url = imageRepository.uploadAvatar(file, id)
        if (url != null) {
            Logger.info("头像上传成功，大小：{}", file.size)
            return RestBean.success(url)
        } else {
            return RestBean.failure(400, "头像上传失败，请联系管理员！")
        }
    }

    @PostMapping("/cover")
    fun uploadCover(
        @RequestParam("file") file: MultipartFile,
        id: Int
    ): RestBean<String> {
        if (file.size > 1024 * 1024)
            return RestBean.failure(400, "头像图片不能大于100KB")
        Logger.info("正在进行图书封面上传操作")
        val url = imageRepository.uploadCover(file, id)
        if (url != null) {
            Logger.info("图书封面上传成功，大小：{}", file.size)
            return RestBean.success(url)
        } else {
            return RestBean.failure(400, "图书封面上传失败，请联系管理员！")
        }
    }

}