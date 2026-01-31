package com.incominggoods.app.api

import com.incominggoods.app.models.ApiResponse
import com.incominggoods.app.models.Order
import com.incominggoods.app.models.PhotoUpload
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    @GET("api/orders/barcode/{barcode}")
    suspend fun getOrderByBarcode(@Path("barcode") barcode: String): Response<Order>
    
    @GET("api/orders")
    suspend fun getAllOrders(): Response<List<Order>>
    
    @GET("api/orders/{id}")
    suspend fun getOrder(@Path("id") id: Int): Response<Order>
    
    @POST("api/orders/{id}/arrival")
    suspend fun reportArrival(@Path("id") id: Int): Response<ApiResponse>
    
    @POST("api/orders/{id}/completion")
    suspend fun reportCompletion(@Path("id") id: Int): Response<ApiResponse>
    
    @POST("api/orders/{id}/close")
    suspend fun closeOrder(@Path("id") id: Int): Response<ApiResponse>
    
    @POST("api/orders/{id}/photos")
    suspend fun uploadPhoto(@Path("id") id: Int, @Body photo: PhotoUpload): Response<ApiResponse>
}
