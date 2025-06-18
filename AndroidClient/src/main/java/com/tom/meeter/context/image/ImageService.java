package com.tom.meeter.context.image;

import static com.tom.meeter.infrastructure.common.Globals.AUTH_HEADER;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface ImageService {

    @GET("/images/event{imagePath}")
    Call<ResponseBody> downloadEventImage(
          @Header(AUTH_HEADER) String authHeader,
          @Path(value = "imagePath", encoded = true) String imagePath);

    @GET("/images/user{imagePath}")
    Call<ResponseBody> downloadUserImage(
          @Header(AUTH_HEADER) String authHeader,
          @Path(value = "imagePath", encoded = true) String imagePath);
}
