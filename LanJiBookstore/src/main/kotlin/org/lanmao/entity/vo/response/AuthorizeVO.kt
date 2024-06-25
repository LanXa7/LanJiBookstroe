package org.lanmao.entity.vo.response

import java.util.*

class AuthorizeVO(
    var username: String,
    var role: String,
    var token: String?,
    var expire: Date
)