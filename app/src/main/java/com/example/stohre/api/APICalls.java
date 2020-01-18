package com.example.stohre.api;

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
    @GET("api/users/read_user_name.php")
    Call<User> readUserName(@Query("USER_NAME") String USER_NAME);
    @GET("api/users/read_user_id.php")
    Call<User> readUserId(@Query("USER_ID") String USER_ID);
    @GET("api/stories/read.php")
    Call<Story> readStoryByStoryId(@Query("STORY_ID") String STORY_ID);
    @GET("api/stories/multiread.php")
    Call<Stories> readStoriesByUserId(@Query("USER_ID") String USER_ID);
    @GET("api/notifications/read.php")
    Call<Notification> readNotificationByUserId(@Query("USER_ID") String USER_ID);

    /*****POSTS*****/
    @POST("api/users/create.php")
    Call<POSTResponse> createUser(@Body User user);
    @POST("api/stories/upsert.php")
    Call<Story> upsertStory(@Body Story story);
    @POST("api/story_edits/create.php")
    Call<POSTResponse> createStoryEdit(@Body StoryEdit storyEdit);

    /*****DELETES*****/
    @POST("api/stories/delete.php")
    Call<POSTResponse> deleteStory(@Body Story story);
    @POST("api/notifications/delete.php")
    Call<POSTResponse> deleteNotification(@Body Notification notification);
}