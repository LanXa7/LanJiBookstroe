package org.lanmao.entity

import com.alibaba.fastjson2.JSONObject
import com.alibaba.fastjson2.JSONWriter

data class RestBean<T>(var code: Int, var data: T?, var message: String?) {
    companion object {

        fun <T> success(data: T? = null): RestBean<T> =
            RestBean(200, data, "请求成功")

        fun <T> failure(code: Int, message: String?): RestBean<T> =
            RestBean(code, null, message)

        fun <T> unauthorized(message: String?): RestBean<T> =
            RestBean(401, null, message)

        fun <T> forbidden(message: String?): RestBean<T> =
            RestBean(403, null, message)
    }

    fun asJsonString(): String =
        JSONObject.toJSONString(this, JSONWriter.Feature.WriteNulls)

}