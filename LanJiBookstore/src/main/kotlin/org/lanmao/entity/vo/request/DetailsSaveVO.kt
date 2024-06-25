package org.lanmao.entity.vo.request

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Pattern
import org.hibernate.validator.constraints.Length


class DetailsSaveVO(
    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$")
    @Length(min = 1, max = 10)
    val username: String,
    @Min(0)
    @Max(1)
    val gender: Int?,
    @Min(0)
    @Max(100)
    val age: Int?,
    @Length(max = 11)
    val phone: String?,
    @Length(max = 18)
    val identificationNumber: String?,
    val address: String?,
    val synopsis:String?
)