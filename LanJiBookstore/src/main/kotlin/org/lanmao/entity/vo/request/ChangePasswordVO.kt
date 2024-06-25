package org.lanmao.entity.vo.request

import org.hibernate.validator.constraints.Length

class ChangePasswordVO(
    @Length(min = 6, max = 20)
    val password: String,
    @Length(min = 6, max = 20)
    val new_password: String,
)