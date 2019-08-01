package com.example.stohre.api;

import com.example.stohre.objects.Member;
import com.example.stohre.objects.Members;
import com.example.stohre.objects.Notification;
import com.example.stohre.objects.Stories;
import com.example.stohre.objects.Story;
import com.example.stohre.objects.StoryEdit;
import com.example.stohre.objects.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APICalls {

    /*****GETS*****/
    @GET("api/users/read_one.php")
    Call<User> readOneUserByUsername(@Query("USER_NAME") String USER_NAME);
    @GET("api/stories/read_one_id.php")
    Call<Story> readStoryId(@Query("USER_ID") String USER_ID, @Query("STORY_NAME")String STORY_NAME);
    @GET("api/stories/read_one.php")
    Call<Story> readStoryByStoryId(@Query("STORY_ID") String STORY_ID);
    @GET("api/stories/read_all.php")
    Call<Stories> readStoriesByUserId(@Query("USER_ID") String USER_ID);
    @GET("api/story_members/read.php")
    Call<Members> readMemberByStoryId(@Query("STORY_ID") String STORY_ID);
    @GET("api/notifications/read.php")
    Call<Notification> readNotificationByUserId(@Query("USER_ID") String USER_ID);

    /*****POSTS*****/
    @POST("api/users/create.php")
    Call<GenericPOSTResponse> createUser(@Body User user);
    @POST("api/stories/create.php")
    Call<GenericPOSTResponse> createStory(@Body Story story);
    @POST("api/stories/update_user_count.php")
    Call<GenericPOSTResponse> updateStoryUserCount(@Body Story story);
    @POST("api/story_members/create.php")
    Call<GenericPOSTResponse> addMemberToStory(@Body Member member);
    @POST("api/story_members/update.php")
    Call<GenericPOSTResponse> updateMemberEditingOrder(@Body Member member);
    @POST("api/story_edits/create.php")
    Call<GenericPOSTResponse> createStoryEdit(@Body StoryEdit storyEdit);

    /*****DELETES*****/
    @POST("api/stories/delete.php")
    Call<GenericPOSTResponse> deleteStory(@Body Story story);
    @POST("api/notifications/delete.php")
    Call<GenericPOSTResponse> deleteNotification(@Body Notification notification);
}