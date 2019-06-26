package com.example.stohre.api;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.stohre.objects.User;
import com.example.stohre.objects.Users;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class APIRequest {

    private Context context;
    private boolean userExists = false;

    public APIRequest(Context context) {
        this.context = context;
    }
    public boolean readOneUser(final GoogleSignInAccount account) {
        //final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        //progressDialog.setMessage("Loading....");
        //progressDialog.show();command
        /*Create handle for the RetrofitInstance interface*/
        Log.v("READ ONE USER ID",String.valueOf(account.getId()));
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<ReadOneUserResponse> call = service.readOneUser(String.valueOf(account.getId()));
        call.enqueue(new Callback<ReadOneUserResponse>() {
            @Override
            public void onResponse(Call<ReadOneUserResponse> call, Response<ReadOneUserResponse> response) {
                //progressDialog.dismiss();
                if (response.isSuccessful()) {
                    Log.v("READ ONE USER", "SUCCESSFUL, NOT CREATING USER");
                    Log.v("RESPONSE_CODE", String.valueOf(response.code()));
                    Log.v("BODY", String.valueOf(response.body()));
                    userExists = true;
                }
                else {
                    Log.v("READ ONE USER", "UNSUCCESSUL, ATTEMPTING TO CREATE USER");
                    userExists = false;
                }
            }
            @Override
            public void onFailure(Call<ReadOneUserResponse> call, Throwable t) {
                Log.d("call",call.toString());
                Log.d("throwable",t.toString());
                //progressDialog.dismiss();
                Toast.makeText(context, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });
        return userExists;
    }

    public void createUser(GoogleSignInAccount account) {
        CreateUserRequest createUserRequest = new CreateUserRequest(String.valueOf(account.getId()),account.getDisplayName());
        //final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        //progressDialog.setMessage("Loading....");
        //progressDialog.show();command
        /*Create handle for the RetrofitInstance interface*/
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<CreateUserResponse> call = service.createUser(createUserRequest);
        call.enqueue(new Callback<CreateUserResponse>() {
            @Override
            public void onResponse(Call<CreateUserResponse> call, Response<CreateUserResponse> response) {
                //progressDialog.dismiss();
                if (response.isSuccessful()) {
                    Log.v("CREATE USER", "SUCCESSFUL");
                    Log.v("RESPONSE_CODE", String.valueOf(response.code()));
                    Log.v("BODY", String.valueOf(response.body()));
                }
            }
            @Override
            public void onFailure(Call<CreateUserResponse> call, Throwable t) {
                Log.d("call",call.toString());
                Log.d("throwable",t.toString());
                //progressDialog.dismiss();
                Toast.makeText(context, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getAllUsers() {
        /*Create handle for the RetrofitInstance interface*/
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<Users> call = service.readyAllUsers();
        call.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                Log.v("RESPONSE_CALLED", "ON_RESPONSE_CALLED");
                String didItWork = String.valueOf(response.isSuccessful());
                Log.v("SUCCESS?", didItWork);
                Log.v("RESPONSE_CODE", String.valueOf(response.code()));
                ArrayList<User> users = response.body().getUsers();
                for (User user: users) {
                    Log.v("RESPONSE_BODY", "response:" + user.getUSER_NAME());
                }
            }
            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                Log.d("call",call.toString());
                Log.d("throwable",t.toString());
                Toast.makeText(context, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
