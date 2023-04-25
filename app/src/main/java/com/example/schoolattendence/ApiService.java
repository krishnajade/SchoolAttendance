package com.example.schoolattendence;


import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {
    @FormUrlEncoded
    @POST("sereng.php?apicall=login")
    Call<String> login(@Field("username") String username, @Field("password") String password);

    @FormUrlEncoded
    @POST("sereng.php?apicall=read")
    Call<User> getUser(@Field("mobile") String mobileNumber);
}