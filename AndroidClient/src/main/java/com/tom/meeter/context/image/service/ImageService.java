package com.tom.meeter.context.image.service;

import static com.tom.meeter.infrastructure.common.Globals.AUTH_HEADER;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface ImageService {

    @GET("/images/event{imagePath}")
    Call<ResponseBody> downloadEventImage(
          @Header(AUTH_HEADER) String authHeader,
          @Path(value = "imagePath", encoded = true) String imagePath);

    @GET("/images/user{imagePath}")
    Call<ResponseBody> downloadUserImage(
          @Header(AUTH_HEADER) String authHeader,
          @Path(value = "imagePath", encoded = true) String imagePath);

    @Multipart
    @POST
    Call<ResponseBody> uploadImage(
          @Header(AUTH_HEADER) String authHeader,
          @Url String url, @Part MultipartBody.Part file);

    @Multipart
    @POST("/images/user")
    Call<ResponseBody> uploadUserImage(
          @Header(AUTH_HEADER) String authHeader, @Part MultipartBody.Part file);

    @Multipart
    @POST("/images/event")
    Call<ResponseBody> uploadEventImage(@Url String url, @Part MultipartBody.Part file);

}
