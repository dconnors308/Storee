package com.example.stohre;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stohre.api.APICalls;
import com.example.stohre.api.APIInstance;
import com.example.stohre.api.GenericPOSTResponse;
import com.example.stohre.objects.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.shobhitpuri.custombuttons.GoogleSignInButton;

import java.util.Objects;

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
    private TextInputEditText createUsernameEditText;
    private Button createUsernameButton;
    private ProgressBar progressBar;
    private APICalls apiCalls;
    private User user;
    private LinearLayout createUsernameParentView, googleSignInParentView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressBar = findViewById(R.id.progress_bar_horizontal_activity_login);
        googleSignInButton = findViewById(R.id.sign_in_button);
        createUsernameEditText = findViewById(R.id.create_user_username_edit_text);
        createUsernameButton = findViewById(R.id.create_user_name_button);
        createUsernameParentView = findViewById(R.id.create_username_parent_view);
        googleSignInParentView = findViewById(R.id.google_login_parent_view);
        googleSignInButton.setOnClickListener(this);
        createUsernameButton.setOnClickListener(this);
        sharedPreferences = getSharedPreferences("com.example.stohre", MODE_PRIVATE);
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        apiCalls = APIInstance.getRetrofitInstance().create(APICalls.class);
    }

    @Override
    protected void onStart() {
        super.onStart();
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
            if (result != null) {
                handleSignInResult(result);
            }
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("SIGN IN", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            googleSignInAccount = result.getSignInAccount();
            googleSignInParentView.setVisibility(View.GONE);
            createUsernameParentView.setVisibility(View.VISIBLE);
            createUsernameEditText.requestFocus();
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
                String username = Objects.requireNonNull(createUsernameEditText.getText()).toString().trim();
                if (TextUtils.isEmpty(username)) {
                    TextInputLayout textInputLayout = findViewById(R.id.create_user_username_edit_text_layout);
                    textInputLayout.setError("enter a username");
                }
                else {
                    if (googleSignInAccount != null) {
                        progressBar.setVisibility(View.VISIBLE);
                        user = new User();
                        user.setUSER_ID(googleSignInAccount.getId());
                        user.setUSER_NAME(username);
                        if (googleSignInAccount.getPhotoUrl() != null) {
                            user.setPHOTO_URI(String.valueOf(googleSignInAccount.getPhotoUrl()));
                        }
                        else {
                            user.setPHOTO_URI("");
                        }
                        attemptToCreateNewAccount(user);
                    }
                }
                break;
        }
    }

    public void attemptToCreateNewAccount(final User user) {
        Call<User> call = apiCalls.readOneUserByUsername(String.valueOf(user.getUSER_NAME()));
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Log.v("READ ONE USER", "SUCCESSFUL, NOT CREATING USER");
                    Log.v("RESPONSE_CODE", String.valueOf(response.code()));
                    Log.v("BODY", String.valueOf(response.body()));
                    progressBar.setVisibility(View.GONE);
                    createUsernameEditText.setText("");
                    Snackbar.make(findViewById(R.id.login_activity), "username already exists" , Snackbar.LENGTH_SHORT).show();
                }
                else {
                    Log.v("READ ONE USER","UNSUCCESSUL, ATTEMPTING TO CREATE USER");
                    createAccount(user);
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("call",call.toString());
                Log.d("throwable",t.toString());
                progressBar.setVisibility(View.GONE);
                Snackbar.make(findViewById(R.id.login_activity), "failure" , Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    public void createAccount(final User user) {
        Call<GenericPOSTResponse> call = apiCalls.createUser(user);
        call.enqueue(new Callback<GenericPOSTResponse>() {
            @Override
            public void onResponse(Call<GenericPOSTResponse> call, Response<GenericPOSTResponse> response) {
                //progressDialog.dismiss();
                if (response.isSuccessful()) {
                    Log.v("CREATE USER", "SUCCESSFUL");
                    Log.v("RESPONSE_CODE", String.valueOf(response.code()));
                    Log.v("BODY", String.valueOf(response.body()));
                    progressBar.setVisibility(View.GONE);
                    addUserToSharedPrefs(user);
                    navigateToMainActivity(user);
                }
            }
            @Override
            public void onFailure(Call<GenericPOSTResponse> call, Throwable t) {
                Log.d("call",call.toString());
                Log.d("throwable",t.toString());
                progressBar.setVisibility(View.GONE);
                Snackbar.make(findViewById(R.id.login_activity), "failure" , Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void addUserToSharedPrefs(User user) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        prefsEditor.putString("user", json);
        prefsEditor.apply();
        Snackbar.make(findViewById(R.id.login_activity), "welcome " + user.getUSER_NAME() + "!" , Snackbar.LENGTH_LONG).show();
    }

    private void navigateToMainActivity(User user) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}
