package com.example.adminquickmart.models



data class Orders(
    val orderId : String = "",
    val orderList : List<cartProducts>? = emptyList(),
    val userAddress : String? = "",
    val orderStatus : Int? = 0,
    val orderDate : String?= "",
    val orderUserId : String?= ""
)
