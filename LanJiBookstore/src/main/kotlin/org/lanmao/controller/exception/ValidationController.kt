package org.lanmao.controller.exception

import jakarta.validation.ValidationException
import org.lanmao.entity.RestBean
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice
class ValidationController {

    val log: Logger = LoggerFactory.getLogger(ValidationController::class.java)

    @ExceptionHandler(ValidationException::class)
    fun validateException(exception: ValidationException): RestBean<Void> {
        log.warn("Resolved [${exception.javaClass.name}: ${exception.message}]")
        return RestBean.failure(400, "请求参数有误")
    }
}