package org.lanmao.entity.vo.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern
import org.hibernate.validator.constraints.Length

class EmailRegisterVO(
    @Email
    val email: String,
    @Length(max = 6, min = 6)
    val code: String,
    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+\$")
    @Length(max = 10, min = 1)
    val username: String,
    @Length(min = 6, max = 20)
    val password: String
)