package com.example.tokenproject.Retrofit2

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

data class ResponseDTO(var result: String? = null)

interface UserService{

    //    @Multipart
    //    @POST("NickLogin.php")
    //    Call<ResponseBody>Nick_login(@Part("Nick_Name") RequestBody title,
    //                                  @Part("PW") RequestBody pw);
    // Login
    //@FormUrlEncoded
    @GET("retrofit_login.php")
    fun User_login(
        @Query("UserId") userId: String,
        @Query("UserPw") userPw: String?
    ): Call<ResponseDTO>


    //닉네임 회원가입
    @GET("UserRegister.php")
    fun UserRegister(
        @Query("userName") user_name: String,
        @Query("userPhone") user_phone: String,
        @Query("userId") nick_name: String,
        @Query("userPw") password: String,
        @Query("Student_Id") student_id: String?,
        @Query("Department") department: String?,
        @Query("rgs_date") rgs_date: String
    ): Call<ResponseDTO>

}