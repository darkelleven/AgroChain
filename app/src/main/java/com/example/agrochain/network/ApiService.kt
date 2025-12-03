package com.example.agrochain.network

import com.example.agrochain.model.Listing
import com.google.gson.JsonObject
import com.example.agrochain.model.Offer
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    // Listings
    @GET("listings")
    suspend fun getAllListings(): Response<List<JsonObject>>

    @GET("listings/{id}")
    suspend fun getListing(@Path("id") id: String): Response<JsonObject>

    @POST("listings")
    suspend fun createListing(@Body listing: Listing): Response<JsonObject>

    @PUT("listings/{id}")
    suspend fun updateListing(@Path("id") id: String, @Body listing: Listing): Response<JsonObject>

    @DELETE("listings/{id}")
    suspend fun deleteListing(@Path("id") id: String): Response<Map<String, String>>

    @GET("listings/owner/{ownerId}")
    suspend fun getListingsByOwner(@Path("ownerId") ownerId: String): Response<List<JsonObject>>

    // Offers
    @GET("offers")
    suspend fun getAllOffers(): Response<List<Offer>>

    @POST("offers")
    suspend fun createOffer(@Body offer: Offer): Response<Offer>

    @PUT("offers/{id}")
    suspend fun updateOffer(@Path("id") id: String, @Body offer: Offer): Response<Offer>
}


