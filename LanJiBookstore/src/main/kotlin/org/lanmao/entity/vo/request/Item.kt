package org.lanmao.entity.vo.request

class Item(
    val id: Int,
    val name: String?,
    val cover: String?,
    val author: String?,
    val press: String?,
    val isbn: String?,
    val status:Int,
    var number: Int,
    val price: Double
)  {
    //constructor() : this(0, null, null, null, null, null, 0,0, 0.0)
}