package com.example.stohre;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.stohre.objects.User;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private NavController navController;
    private SharedPreferences sharedPreferences;
    private User user;
    private ImageView profileImageView;
    private boolean accountCreated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSharedPreferences("com.example.stohre", 0).edit().clear().commit();
        setContentView(R.layout.activity_main);
        getUserOrStartLoginActivity();
        configureViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        int id = item.getItemId();
        if (id == R.id.stories) {
            navController.navigate(R.id.stories);
        }
        else if (id == R.id.new_story) {
            navController.navigate(R.id.new_story);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void getUserOrStartLoginActivity() {
        sharedPreferences = getSharedPreferences("com.example.stohre", MODE_PRIVATE);
        if (sharedPreferences.getString("user", "").isEmpty()) {
            accountCreated = false;
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        else {
            accountCreated = true;
            Gson gson = new Gson();
            String json = sharedPreferences.getString("user", "");
            user = gson.fromJson(json, User.class);
        }
    }

    private void configureViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
        navController = Navigation.findNavController(this, R.id.main_content);
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout);
        NavigationUI.setupWithNavController(navigationView, navController);
        profileImageView = navigationView.getHeaderView(0).findViewById(R.id.profile_image_view);
        if (accountCreated && !TextUtils.isEmpty(user.getPHOTO_URI())) {
            Picasso.get().load(Uri.parse(user.getPHOTO_URI())).into(profileImageView);
        }
        else {
            Toast.makeText(getApplicationContext(), "unable to load photo",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() { return NavigationUI.navigateUp(navController, drawerLayout); }


    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted
                } else {
                    // permission denied
                }
                return;
            }
        }
    }

}
