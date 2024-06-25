package org.lanmao.entity.vo.request

import jakarta.validation.constraints.Email
import org.hibernate.validator.constraints.Length

class EmailResetVO(
    @Email
    val email: String,
    @Length(min = 6, max = 6)
    val code: String,
    @Length(min = 6, max = 20)
    val password: String
)