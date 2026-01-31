package com.incominggoods.app.models

import com.google.gson.annotations.SerializedName

data class Order(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("barcode")
    val barcode: String,
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("supplierName")
    val supplierName: String?,
    
    @SerializedName("orderDate")
    val orderDate: String,
    
    @SerializedName("arrivalDate")
    val arrivalDate: String?,
    
    @SerializedName("completionDate")
    val completionDate: String?,
    
    @SerializedName("isClosed")
    val isClosed: Boolean,
    
    @SerializedName("status")
    val status: String,
    
    @SerializedName("photos")
    val photos: List<Photo> = emptyList()
)

data class Photo(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("fileName")
    val fileName: String,
    
    @SerializedName("uploadDate")
    val uploadDate: String
)

data class PhotoUpload(
    @SerializedName("fileName")
    val fileName: String,
    
    @SerializedName("base64Data")
    val base64Data: String
)

data class ApiResponse(
    @SerializedName("message")
    val message: String,
    
    @SerializedName("arrivalDate")
    val arrivalDate: String? = null,
    
    @SerializedName("completionDate")
    val completionDate: String? = null
)
