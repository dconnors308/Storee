package com.example.stohre.api;
import com.example.stohre.objects.Stories;
import com.example.stohre.objects.Story;

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
    Call<ReadStoryIdResponse> readStoryId(@Query("USER_ID") String USER_ID, @Query("STORY_NAME")String STORY_NAME);

    @GET("api/stories/read_all.php")
    Call<Stories> readStoriesByUserId(@Query("USER_ID") String USER_ID);

    @POST("api/stories/create.php")
    Call<GenericResponse> createStory(@Body CreateStoryRequest createStoryRequest);

    @POST("api/stories/update.php")
    Call<GenericResponse> updateStory(@Body Story story);

    @POST("api/story_groups/create.php")
    Call<GenericResponse> addUserToStory(@Body AddUserToStoryRequest addUserToStoryRequest);

}