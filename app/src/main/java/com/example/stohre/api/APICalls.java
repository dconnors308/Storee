package com.example.stohre.api;
import com.example.stohre.objects.Stories;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APICalls {

    @GET("api/users/read_one.php")
    Call<ReadOneUserResponse> readOneUserByUsername(@Query("USER_NAME") String USER_NAME);

    @POST("api/users/create.php")
    Call<GenericResponse> createUser(@Body CreateUserRequest createUserRequest);

    @GET("api/stories/read_one_id.php")
    Call<ReadStoryIdResponse> readStoryId(@Body ReadStoryIdRequest readStoryIdRequest);

    @GET("api/stories/read_all.php")
    Call<Stories> readStoriesByUserId(@Query("USER_ID") String USER_ID);

    @POST("api/stories/create.php")
    Call<GenericResponse> createStory(@Body CreateStoryRequest createStoryRequest);

    @POST("api/story_groups/create.php")
    Call<GenericResponse> addUserToStory(@Body AddUserToStoryRequest addUserToStoryRequest);

}