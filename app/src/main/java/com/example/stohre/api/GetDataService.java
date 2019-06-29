package com.example.stohre.api;
import com.example.stohre.objects.Stories;
import com.example.stohre.objects.Story;
import com.example.stohre.objects.Users;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetDataService {
    @GET("api/users/read.php")
    Call<Users> readAllUsers();

    @GET("api/users/read_one.php")
    Call<ReadOneUserResponse> readOneUserByUsername(@Query("USER_NAME") String USER_NAME);

    @POST("api/users/create.php")
    Call<CreateUserResponse> writeUser(@Body CreateUserRequest createUserRequest);

    @GET("api/stories/read_all.php")
    Call<Stories> readStoriesByUserId(@Query("USER_ID") String USER_ID);
}