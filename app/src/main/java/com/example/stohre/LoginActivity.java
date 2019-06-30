package com.example.stohre;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stohre.api.CreateUserRequest;
import com.example.stohre.api.CreateUserResponse;
import com.example.stohre.api.GetDataService;
import com.example.stohre.api.ReadOneUserResponse;
import com.example.stohre.api.RetrofitClientInstance;
import com.example.stohre.objects.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private GoogleSignInClient googleSignInClient;
    private GoogleSignInAccount googleSignInAccount;
    private final int RC_SIGN_IN = 1;
    private SharedPreferences sharedPreferences;
    private EditText createUsernameEditText;
    private Button createUsernameButton;
    private ProgressDialog progressDialog;
    private GetDataService service;
    private User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSharedPreferences("com.example.Stohre", 0).edit().clear().commit();
        setContentView(R.layout.fragment_login);
        createUsernameEditText = findViewById(R.id.create_user_username_edit_text);
        createUsernameButton = findViewById(R.id.create_user_name_button);
        createUsernameButton.setOnClickListener(this);
        sharedPreferences = getSharedPreferences("com.example.Stohre", MODE_PRIVATE);
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build(); //check for last sign on
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //check google authentication result
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            googleSignInAccount = completedTask.getResult(ApiException.class);
            Toast.makeText(this, "google account verified", Toast.LENGTH_SHORT).show();
        } catch (ApiException e) {
            Log.w("SIGN IN FAILURE", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(this, "google sign in failure", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.create_user_name_button:
                String username = createUsernameEditText.getText().toString();
                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(this, "please enter a username" + username, Toast.LENGTH_SHORT).show();
                }
                else {
                    if(googleSignInAccount != null) {
                        user = new User();
                        user.setUSER_ID(googleSignInAccount.getId());
                        user.setUSER_NAME(username);
                        logIn(user);
                    }
                }
                break;
        }
    }

    public void logIn(final User user) {
        service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("lots of stuff happening....");
        progressDialog.show();
        Call<ReadOneUserResponse> call = service.readOneUserByUsername(String.valueOf(user.getUSER_NAME()));
        call.enqueue(new Callback<ReadOneUserResponse>() {
            @Override
            public void onResponse(Call<ReadOneUserResponse> call, Response<ReadOneUserResponse> response) {
                if (response.isSuccessful()) {
                    Log.v("READ ONE USER", "SUCCESSFUL, NOT CREATING USER");
                    Log.v("RESPONSE_CODE", String.valueOf(response.code()));
                    Log.v("BODY", String.valueOf(response.body()));
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "welcome " + user.getUSER_NAME(),Toast.LENGTH_LONG).show();
                    addUserToSharedPrefs(user);
                    navigateToMainActivity(user);
                }
                else {
                    Log.v("READ ONE USER", "UNSUCCESSUL, ATTEMPTING TO CREATE USER");
                    createUser(user);
                }
            }
            @Override
            public void onFailure(Call<ReadOneUserResponse> call, Throwable t) {
                Log.d("call",call.toString());
                Log.d("throwable",t.toString());
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "READ USER API FAILURE", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void createUser(final User user) {
        CreateUserRequest createUserRequest = new CreateUserRequest(String.valueOf(user.getUSER_ID()),user.getUSER_NAME());
        Call<CreateUserResponse> call = service.writeUser(createUserRequest);
        call.enqueue(new Callback<CreateUserResponse>() {
            @Override
            public void onResponse(Call<CreateUserResponse> call, Response<CreateUserResponse> response) {
                //progressDialog.dismiss();
                if (response.isSuccessful()) {
                    Log.v("CREATE USER", "SUCCESSFUL");
                    Log.v("RESPONSE_CODE", String.valueOf(response.code()));
                    Log.v("BODY", String.valueOf(response.body()));
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "account" + user.getUSER_NAME() + " created!",Toast.LENGTH_LONG).show();
                    addUserToSharedPrefs(user);
                    navigateToMainActivity(user);
                }
            }
            @Override
            public void onFailure(Call<CreateUserResponse> call, Throwable t) {
                Log.d("call",call.toString());
                Log.d("throwable",t.toString());
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "CREATE USER API FAILURE", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addUserToSharedPrefs(User user) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        prefsEditor.putString("user", json);
        prefsEditor.commit();
    }

    private void navigateToMainActivity(User user) {
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("user", user);
        startActivity(i);
    }
}
