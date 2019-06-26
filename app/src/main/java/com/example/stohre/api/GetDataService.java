package com.example.stohre.api;
import com.example.stohre.objects.Contact;
import com.example.stohre.objects.Users;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetDataService {
    @POST("api/users/read.php")
    Call<Users> readyAllUsers();

    @POST("api/users/read_one.php")
    Call<ReadOneUserResponse> readOneUser(@Query("USER_ID") String USER_ID);

    @POST("api/users/create.php")
    Call<CreateUserResponse> createUser(@Body CreateUserRequest createUserRequest);

    @POST("group/{id}/users")
    Call<ArrayList<Contact>> readGroup(@Path("GROUP_ID") int GROUP_ID, @Query("STORY_ID") String STORY_ID);
}