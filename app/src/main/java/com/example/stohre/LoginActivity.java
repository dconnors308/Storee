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
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stohre.api.CreateUserRequest;
import com.example.stohre.api.GenericResponse;
import com.example.stohre.api.APICalls;
import com.example.stohre.api.ReadOneUserResponse;
import com.example.stohre.api.APIInstance;
import com.example.stohre.objects.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.gson.Gson;
import com.shobhitpuri.custombuttons.GoogleSignInButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.android.gms.auth.api.signin.internal.zzh.getSignInResultFromIntent;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private final int GOOGLE_AUTH_REQUEST_CODE = 9001;
    private GoogleSignInClient googleSignInClient;
    private GoogleSignInAccount googleSignInAccount;
    private GoogleSignInOptions googleSignInOptions;
    private SharedPreferences sharedPreferences;
    private GoogleSignInButton googleSignInButton;
    private EditText createUsernameEditText;
    private Button createUsernameButton;
    private ProgressDialog progressDialog;
    private APICalls apiCalls;
    private User user;
    private LinearLayout createUsernameParentView, googleSignInParentView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressDialog = new ProgressDialog(this,R.style.AppTheme_ProgressStyle);
        googleSignInButton = findViewById(R.id.sign_in_button);
        createUsernameEditText = findViewById(R.id.create_user_username_edit_text);
        createUsernameButton = findViewById(R.id.create_user_name_button);
        createUsernameParentView = findViewById(R.id.create_username_parent_view);
        googleSignInParentView = findViewById(R.id.google_login_parent_view);
        googleSignInButton.setOnClickListener(this);
        createUsernameButton.setOnClickListener(this);
        sharedPreferences = getSharedPreferences("com.example.stohre", MODE_PRIVATE);
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build(); //check for last sign on
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        apiCalls = APIInstance.getRetrofitInstance().create(APICalls.class);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //progressDialog.hide();
        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_AUTH_REQUEST_CODE) {
            GoogleSignInResult result = getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("SIGN IN", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            googleSignInAccount = result.getSignInAccount();
            googleSignInParentView.setVisibility(View.GONE);
            createUsernameParentView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.sign_in_button:
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, GOOGLE_AUTH_REQUEST_CODE);
                break;
            case R.id.create_user_name_button:
                String username = createUsernameEditText.getText().toString().trim();
                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(this, "please enter a username" + username, Toast.LENGTH_SHORT).show();
                }
                else {
                    if (googleSignInAccount != null) {
                        progressDialog.setMessage("creating storee account...");
                        progressDialog.show();
                        user = new User();
                        user.setUSER_ID(googleSignInAccount.getId());
                        user.setUSER_NAME(username);
                        user.setPHOTO_URI(String.valueOf(googleSignInAccount.getPhotoUrl()));
                        logIn(user);
                    }
                }
                break;
        }
    }

    public void logIn(final User user) {
        Call<ReadOneUserResponse> call = apiCalls.readOneUserByUsername(String.valueOf(user.getUSER_NAME()));
        call.enqueue(new Callback<ReadOneUserResponse>() {
            @Override
            public void onResponse(Call<ReadOneUserResponse> call, Response<ReadOneUserResponse> response) {
                if (response.isSuccessful()) {
                    Log.v("READ ONE USER", "SUCCESSFUL, NOT CREATING USER");
                    Log.v("RESPONSE_CODE", String.valueOf(response.code()));
                    Log.v("BODY", String.valueOf(response.body()));
                    progressDialog.dismiss();
                    addUserToSharedPrefs(user);
                    navigateToMainActivity(user);
                }
                else {
                    Log.v("READ ONE USER","UNSUCCESSUL, ATTEMPTING TO CREATE USER");
                    createUser(user);
                }
            }
            @Override
            public void onFailure(Call<ReadOneUserResponse> call, Throwable t) {
                Log.d("call",call.toString());
                Log.d("throwable",t.toString());
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "request failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void createUser(final User user) {
        CreateUserRequest createUserRequest = new CreateUserRequest(String.valueOf(user.getUSER_ID()),user.getUSER_NAME());
        Call<GenericResponse> call = apiCalls.createUser(createUserRequest);
        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                //progressDialog.dismiss();
                if (response.isSuccessful()) {
                    Log.v("CREATE USER", "SUCCESSFUL");
                    Log.v("RESPONSE_CODE", String.valueOf(response.code()));
                    Log.v("BODY", String.valueOf(response.body()));
                    progressDialog.dismiss();
                    addUserToSharedPrefs(user);
                    navigateToMainActivity(user);
                }
            }
            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                Log.d("call",call.toString());
                Log.d("throwable",t.toString());
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "request failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addUserToSharedPrefs(User user) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        prefsEditor.putString("user", json);
        prefsEditor.commit();
        Toast.makeText(getApplicationContext(), "welcome " + user.getUSER_NAME() + "!",Toast.LENGTH_LONG).show();
    }

    private void navigateToMainActivity(User user) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}
