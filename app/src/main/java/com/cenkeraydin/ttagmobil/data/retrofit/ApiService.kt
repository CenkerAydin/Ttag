package com.cenkeraydin.ttagmobil.data.retrofit

import com.cenkeraydin.ttagmobil.data.model.account.AvailableDriver
import com.cenkeraydin.ttagmobil.data.model.auth.AuthBody
import com.cenkeraydin.ttagmobil.data.model.car.CarResponse
import com.cenkeraydin.ttagmobil.data.model.account.DeleteAccountResponse
import com.cenkeraydin.ttagmobil.data.model.account.DriverInfoResponse
import com.cenkeraydin.ttagmobil.data.model.auth.ForgotPasswordRequest
import com.cenkeraydin.ttagmobil.data.model.auth.ForgotPasswordResponse
import com.cenkeraydin.ttagmobil.data.model.auth.LoginRequest
import com.cenkeraydin.ttagmobil.data.model.auth.RegisterRequest
import com.cenkeraydin.ttagmobil.data.model.auth.ResetPasswordRequest
import com.cenkeraydin.ttagmobil.data.model.account.UpdateDriverInfoRequest
import com.cenkeraydin.ttagmobil.data.model.account.UpdateUserInfoRequest
import com.cenkeraydin.ttagmobil.data.model.account.UploadResponse
import com.cenkeraydin.ttagmobil.data.model.account.UserInfoResponse
import com.cenkeraydin.ttagmobil.data.model.car.CarCreateRequest
import com.cenkeraydin.ttagmobil.data.model.reservation.CreateReservationRequest
import com.cenkeraydin.ttagmobil.data.model.reservation.ReservationResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
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
    suspend fun forgotPasswordAccount(
        @Body request: ForgotPasswordRequest
    ): Response<ForgotPasswordResponse>


    @POST("api/Account/reset-password")
    suspend fun resetAccountPassword(
        @Body request: ResetPasswordRequest
    ): Response<ResponseBody>

    @POST("api/Driver/authenticate")
    suspend fun loginDriver(@Body request: LoginRequest): Response<AuthBody>

    @POST("api/Driver/reset-password")
    suspend fun resetDriverPassword(
        @Body request: ResetPasswordRequest
    ): Response<ResponseBody>



    @GET("/api/v1/car")
    suspend fun getCars(): Response<CarResponse>

    @GET("api/UserManagement/info")
    suspend fun getUserInfo(@Query("email") email: String): Response<UserInfoResponse>

    @PUT("api/UserManagement/info")
    suspend fun updateUserInfo(
        @Body request: UpdateUserInfoRequest
    ): Response<Void>

    @DELETE("api/UserManagement/account")
    suspend fun deleteAccount(
        @Query("email") email: String
    ): Response<DeleteAccountResponse>

    @GET("api/DriverManagement/info")
    suspend fun getDriverInfo(@Query("email") email: String): Response<DriverInfoResponse>


    @DELETE("api/DriverManagement/account")
    suspend fun deleteDriverAccount(
        @Query("email") email: String
    ): Response<DeleteAccountResponse>

    @PUT("api/DriverManagement/info")
    suspend fun updateDriverInfo(
        @Body request: UpdateDriverInfoRequest
    ): Response<Void>


    @Multipart
    @POST("/api/Image/profilePictureEdit")
    suspend fun uploadProfilePicture(
        @Part image: MultipartBody.Part,
        @Part("UserId") userId: RequestBody?
    ): Response<Unit>

    @Multipart
    @POST("api/image/driverLicenseEdit")
    suspend fun uploadDriverLicense(
        @Part image: MultipartBody.Part,
        @Part("userId") userId: RequestBody?,
        ): Response<UploadResponse>

    @POST("api/DriverManagement/car")
    suspend fun addCar(@Body car: CarCreateRequest): Response<Unit>

    @Multipart
    @POST("api/Image/carImageEdit")
    suspend fun uploadCarImage(
        @Part image: MultipartBody.Part,
        @Part("CarId") carId: RequestBody,
    ): Response<Unit>

    @GET("api/Reservation/available-drivers")
    suspend fun getAvailableDrivers(
        @Query("startDateTime") startDateTime: String,
        @Query("endDateTime") endDateTime: String
    ): Response<List<AvailableDriver>>

    @POST("api/Reservation/CreateReservation")
    suspend fun createReservation(@Body request: CreateReservationRequest): Response<ReservationResponse>

    @GET("api/Reservation/user/{userId}")
    suspend fun getUserReservations(
        @Path("userId") userId: String?
    ): List<ReservationResponse>

    @GET("api/Reservation/driver/{driverId}")
    suspend fun getDriverReservations(
        @Path("driverId") driverId: String?
    ): List<ReservationResponse>



    @PUT("api/Reservation/{id}/status")
    suspend fun updateReservationStatus(
        @Path("id") reservationId: String,
        @Body status: Int
    )

    @DELETE("api/v1/DriverManagement/car/{carId}")
    suspend fun deleteCar(
        @Path("carId") carId: String
    ): Response<Unit>





}

