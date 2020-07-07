package com.example.tokenproject.Retrofit2;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitBoard {

    private Retrofit mRetrofit;

    private BoardApi mBoardApi;

    public RetrofitBoard(){

        mRetrofit = new Retrofit.Builder()
                .baseUrl(BoardApi.BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mBoardApi = mRetrofit.create(BoardApi.class);
    }

    public BoardApi getImageApi(){
        return mBoardApi;

    }
}
