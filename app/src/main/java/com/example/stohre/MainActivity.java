package com.example.stohre;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.stohre.objects.User;
import com.example.stohre.workers.NotificationWorker;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public NavController navController;
    private DrawerLayout drawerLayout;
    private ImageView profileImageView;
    private NavigationView navigationView;
    private AppBarConfiguration appBarConfiguration;
    private SharedPreferences sharedPreferences;
    private User user;
    boolean isNewUser = false;
    private final String NOTIFICATION_CHANNEL_ID = "STOREE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSharedPreferences("com.example.stohre", 0).edit().clear().commit();
        setContentView(R.layout.activity_main);
        if (!isNewUser()) {
            configureNotifications();
            configureNavigation();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() { super.onDestroy(); }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
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


    private boolean isNewUser() {
        sharedPreferences = getSharedPreferences("com.example.stohre", MODE_PRIVATE);
        if (sharedPreferences.getString("user", "").isEmpty()) {
            isNewUser = true;
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        else {
            Gson gson = new Gson();
            String json = sharedPreferences.getString("user", "");
            user = gson.fromJson(json, User.class);
        }
        return isNewUser;
    }

    private void configureNavigation() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
        profileImageView = navigationView.getHeaderView(0).findViewById(R.id.profile_image_view);
        navController = Navigation.findNavController(this, R.id.main_content);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).setDrawerLayout(drawerLayout).build();
        NavigationUI.setupWithNavController(navigationView,navController);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        loadGooglePhoto();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.main_content);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void loadGooglePhoto() {

        if (!isNewUser && !TextUtils.isEmpty(user.getPHOTO_URI())) { //load google photo if exists
            Picasso.get().load(Uri.parse(user.getPHOTO_URI())).into(profileImageView);
        }
    }

    private void configureNotifications() {
        createNotificationChannel();
        PeriodicWorkRequest notificationWorkRequest = new PeriodicWorkRequest.Builder(NotificationWorker.class, 15, TimeUnit.SECONDS).build();
        WorkManager.getInstance(this).cancelAllWork();
        WorkManager.getInstance().cancelAllWork();
        WorkManager.getInstance().enqueue(notificationWorkRequest);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.notification_channel_name);
            String description = getString(R.string.notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
