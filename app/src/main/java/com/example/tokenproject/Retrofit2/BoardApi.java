package com.example.tokenproject.Retrofit2;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface BoardApi {

    String BaseUrl = "http://tkdanr2427.cafe24.com/Study/";



    //Board List Select
    //@FormUrlEncoded
    // 게시판 리스트 조회
    @POST("getBoardList.php")
    Call<List<ResultBoard>> getBoardList();

    // 게시판 Insert (이미지 o)
    @Multipart
    @POST("BoardInsert.php")
    Call<ResponseBody> InsertBoard(@Part("USER") RequestBody user,
                                   @Part("TITLE") RequestBody title,
                                   @Part("CONTENT") RequestBody content,
                                   @Part("DATE") RequestBody date,
                                   @Part MultipartBody.Part image,
                                   @Part("TOKEN") RequestBody token);



    // 게시판 Insert (이미지 x)
    @Multipart
    @POST("BoardInsert_NoImage.php")
    Call<ResponseBody> InsertBoard_NoImage(@Part("USER") RequestBody user,
                                           @Part("TITLE") RequestBody title,
                                           @Part("CONTENT") RequestBody content,
                                           @Part("DATE") RequestBody date,
                                           @Part("TOKEN") RequestBody token);

    // 게시판 Update (이미지 o)
    @Multipart
    @POST("BoardUpdate.php")
    Call<ResponseBody> UpdateBoard(@Part("Seq") RequestBody seq,
                                   @Part("USER") RequestBody user,
                                   @Part("TITLE") RequestBody title,
                                   @Part("CONTENT") RequestBody content,
                                   @Part("DATE") RequestBody date,
                                   @Part MultipartBody.Part image);

    // 게시판 Update (이미지 x)
    @Multipart
    @POST("BoardUpdate_NoImage.php")
    Call<ResponseBody> UpdateBoard_NoImage(@Part("Seq") RequestBody seq,
                                           @Part("USER") RequestBody user,
                                           @Part("TITLE") RequestBody title,
                                           @Part("CONTENT") RequestBody content,
                                           @Part("DATE") RequestBody date);



    // 게시판 댓글 Insert (이미지 x)
    @Multipart
    @POST("BoardReplyInsert.php")
    Call<ResultBoard_Reply> InsertBoard_Reply(
            @Part("USER") RequestBody user,
            @Part("REPLY") RequestBody reply,
            @Part("DATE") RequestBody date,
            @Part("BoardSeq") RequestBody boardSeq);

    // 게시판 댓글 List
    @Multipart
    @POST("BoardReplyList.php")
    Call<List<ResultBoard_Reply>> getBoardReplyList(@Part("BoardSeq") RequestBody board_seq);




}
