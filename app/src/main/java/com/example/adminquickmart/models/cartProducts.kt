package com.example.adminquickmart.models

import androidx.annotation.NonNull



data class cartProducts (
    var productId: String = "random", // cant apply nullablity check here
    var producttitle: String ?= null,
    var productQuantity: String ? = null,
    var productCount: Int ?= null,
    var productPrice: String ?= null,
    var productStock: Int ?= null,
    var productcategory: String ?= null,
    var productImage: String ?= null,
    var adminUid: String ?= null,
    var productType : String ?= null
)

