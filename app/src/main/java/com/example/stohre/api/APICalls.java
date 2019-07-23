package com.example.stohre.api;

import com.example.stohre.objects.Stories;
import com.example.stohre.objects.Story;
import com.example.stohre.objects.StoryGroup;
import com.example.stohre.objects.StoryGroups;
import com.example.stohre.objects.User;
import com.example.stohre.objects.Users;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APICalls {
    /*****GETS*****/
    @GET("api/users/read_one.php")
    Call<User> readOneUserByUsername(@Query("USER_NAME") String USER_NAME);
    @GET("api/users/read_friends.php")
    Call<Users> readFriendsByUserId(@Query("USER_ID") String USER_ID);
    @GET("api/stories/read_one_id.php")
    Call<Story> readStoryId(@Query("USER_ID") String USER_ID, @Query("STORY_NAME")String STORY_NAME);
    @GET("api/stories/read_all.php")
    Call<Stories> readStoriesByUserId(@Query("USER_ID") String USER_ID);
    @GET("api/story_groups/read.php")
    Call<StoryGroups> readStoryGroupByStoryId(@Query("STORY_ID") String STORY_ID);
    /*****POSTS*****/
    @POST("api/users/create.php")
    Call<GenericPOSTResponse> createUser(@Body User user);
    @POST("api/stories/create.php")
    Call<GenericPOSTResponse> createStory(@Body Story story);
    @POST("api/stories/update.php")
    Call<GenericPOSTResponse> updateStory(@Body Story story);
    @POST("api/stories/delete.php")
    Call<GenericPOSTResponse> deleteStory(@Body Story story);
    @POST("api/story_groups/create.php")
    Call<GenericPOSTResponse> addUserToStory(@Body StoryGroup storyGroup);
    @POST("api/story_groups/update.php")
    Call<GenericPOSTResponse> updateMemberEditingOrder(@Body StoryGroup storyGroup);
}