package com.cenkeraydin.ttagmobil.data.retrofit

import com.cenkeraydin.ttagmobil.data.model.AuthBody
import com.cenkeraydin.ttagmobil.data.model.AuthResponse
import com.cenkeraydin.ttagmobil.data.model.CarResponse
import com.cenkeraydin.ttagmobil.data.model.ForgotPasswordRequest
import com.cenkeraydin.ttagmobil.data.model.ForgotPasswordResponse
import com.cenkeraydin.ttagmobil.data.model.LoginRequest
import com.cenkeraydin.ttagmobil.data.model.RegisterRequest
import com.cenkeraydin.ttagmobil.data.model.ResetPasswordRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("/api/Account/register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<ResponseBody>

    @POST("api/Driver/register")
    suspend fun registerDriver(@Body request: RegisterRequest): Response<ResponseBody>

    @POST("/api/Account/authenticate")
    suspend fun loginUser(@Body request: LoginRequest): Response<AuthBody>

    @GET("/api/Account/confirm-email")
    suspend fun confirmEmail(
        @Query("email") email: String,
        @Query("code") code: String
    ): Response<Void>

    @POST("api/Account/forgot-password")
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequest
    ): Response<ForgotPasswordResponse>

    @POST("api/Account/reset-password")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): Response<ResponseBody>


    @GET("/api/v1/car")
    suspend fun getCars(): Response<CarResponse>

}

