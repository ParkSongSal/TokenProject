package com.example.tokenproject.Retrofit2;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserApi {

      String BaseUrl = "http://tkdanr2427.cafe24.com/Study/";


//    @Multipart
//    @POST("NickLogin.php")
//    Call<ResponseBody>Nick_login(@Part("Nick_Name") RequestBody title,
//                                  @Part("PW") RequestBody pw);



    // Login
    //@FormUrlEncoded
    @GET("UserLogin.php")
    Call<ResultModel> User_login(@Query("UserId") String userId,
                                 @Query("UserPw") String userPw);


    //닉네임 중복검사
    @GET("Nick_Name_Validate.php")
    Call<ResultModel> NickName_Validate(@Query("Nick_Name") String nick_name);

    //학번 중복검사
    @GET("Student_Id_Validate.php")
    Call<ResultModel> StudentId_Validate(@Query("Student_Id") String student_Id);



    //닉네임 회원가입
    @GET("UserRegister.php")
    Call<ResultModel> UserRegister(@Query("userName") String user_name,
                                        @Query("userPhone") String user_phone,
                                        @Query("userId") String nick_name,
                                        @Query("userPw") String password,
                                        @Query("Student_Id") String student_id,
                                        @Query("Department") String department,
                                        @Query("rgs_date") String rgs_date);



    //사용자 Token Update
    @GET("UserTokenUpdate.php")
    Call<ResultModel> UserTokenUpdate(@Query("userId") String nick_name,
                                      @Query("userToken") String token);


    @GET("retrofit_login.php")
    Call<ResultModel> Nick_loginLog(@Query("UserId") String nick_name,
                                    @Query("UserPw") String password,
                                    @Query("LastedDate") String lastdate);

    // 메인_최근로그인일시 조회
    @POST("LastedLogin.php")
    Call<List<ResultModel>> getLastLoginDate(@Query("Nick_Name") String nick_name);




}
