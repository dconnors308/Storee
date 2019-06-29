package com.example.stohre;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.stohre.api.CreateUserRequest;
import com.example.stohre.api.CreateUserResponse;
import com.example.stohre.api.GetDataService;
import com.example.stohre.api.ReadOneUserResponse;
import com.example.stohre.api.RetrofitClientInstance;
import com.example.stohre.dialogs.CreateUsername;
import com.example.stohre.fragments.CreateStory;
import com.example.stohre.fragments.Stories;
import com.example.stohre.objects.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CreateUsername.CreateUsernameDialogListener {

    public ProgressDialog progressDialog;
    public GetDataService service;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private GoogleSignInClient googleSignInClient;
    private GoogleSignInAccount googleSignInAccount;
    private final int RC_SIGN_IN = 1;
    private final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private SharedPreferences sharedPreferences;
    public boolean loggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("com.example.Stohre", MODE_PRIVATE);
        prepareView();
    }

    private void showCreateUsernameDialog() {
        FragmentManager fm = getSupportFragmentManager();
        CreateUsername createUsernameDialog = CreateUsername.newInstance("Create Username");
        createUsernameDialog.show(fm, "fragment_create_username");
    }

    @Override
    protected void onStart() {
        super.onStart();
        //check for last sign on
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (!sharedPreferences.getString("username", "").isEmpty()) {
            User user = new User();
            user.setUSER_ID(googleSignInAccount.getId());
            user.setUSER_NAME(sharedPreferences.getString("username", ""));
            verifyOrCreateAccount(user);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sharedPreferences.getBoolean("firstrun", true)) {
            //if first run, create account with google authenticator
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)) return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /*
            case R.id.button:
                signIn();
                break;
            */
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //check google account creation result
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            if (sharedPreferences.getBoolean("firstrun", true)) {
                sharedPreferences.edit().putBoolean("firstrun", false).commit();
                showCreateUsernameDialog();
            }
            googleSignInAccount = completedTask.getResult(ApiException.class);
            if (!sharedPreferences.getString("username", "").isEmpty()) {
                User user = new User();
                user.setUSER_ID(googleSignInAccount.getId());
                user.setUSER_NAME(sharedPreferences.getString("username", ""));
                verifyOrCreateAccount(user);
            }
        } catch (ApiException e) {
            Log.w("SIGN IN FAILURE", "signInResult:failed code=" + e.getStatusCode());
            verifyOrCreateAccount(null);
        }
    }

    private void prepareView() {
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,R.string.action_open, R.string.action_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id)
                {
                    case R.id.groups:
                        Toast.makeText(MainActivity.this, "Groups",Toast.LENGTH_SHORT).show();break;
                    case R.id.settings:
                        Toast.makeText(MainActivity.this, "Settings",Toast.LENGTH_SHORT).show();break;
                    case R.id.sign_out:
                        Toast.makeText(MainActivity.this, "Sign Out",Toast.LENGTH_SHORT).show();break;
                    default:
                        return true;
                }
                return true;

            }
        });
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void verifyOrCreateAccount(User user) {
        if (googleSignInAccount != null) {
            Log.v("NAME",user.getUSER_NAME());
            Log.v("ID",String.valueOf(user.getUSER_ID()));
            progressDialog = new ProgressDialog(this);
            logIn(user);
        }
        else {
            Toast.makeText(MainActivity.this, "google authentication failure",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    public void onFinishEditDialog(String username) {
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "no username entered, account creation unsuccessful" + username, Toast.LENGTH_SHORT).show();
        }
        else {
            sharedPreferences.edit().putString("username", username).commit();
            User user = new User();
            user.setUSER_ID(googleSignInAccount.getId());
            user.setUSER_NAME(username);
            verifyOrCreateAccount(user);
        }
    }

    public void logIn(final User user) {
        service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
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
                    openStoriesFragment(user);
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
                loggedIn = false;
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
                    openStoriesFragment(user);
                    Toast.makeText(getApplicationContext(), "account" + user.getUSER_NAME() + " created!",Toast.LENGTH_LONG).show();
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

    public void openStoriesFragment(User user) {
        if (findViewById(R.id.fragment_container) != null) {
            Stories storiesFragment = Stories.newInstance(user);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, storiesFragment).commit();
        }
    }
    public void openCreateStoryFragment(User user) {
        if (findViewById(R.id.fragment_container) != null) {
            CreateStory createStoryFragment = CreateStory.newInstance(user);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, createStoryFragment).commit();
        }
    }
}
