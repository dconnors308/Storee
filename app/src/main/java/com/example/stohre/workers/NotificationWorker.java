package com.example.stohre.workers;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.stohre.MainActivity;
import com.example.stohre.R;
import com.example.stohre.api.APICalls;
import com.example.stohre.api.APIInstance;
import com.example.stohre.api.POSTResponse;
import com.example.stohre.objects.Notification;
import com.example.stohre.objects.User;
import com.google.gson.Gson;

import java.util.Objects;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class NotificationWorker extends Worker {

    private String NOTIFICATION_CHANNEL_ID = "STOREE";
    private User user;
    private APICalls service;
    private SharedPreferences sharedPreferences;
    private Random random;


    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        random = new Random();
    }

    @NonNull
    @Override
    public Result doWork() {
        getUser();
        if (user != null) {
            readNotificationByUserId(user);
        }
        return Result.retry();
    }

    private void getUser() {
        sharedPreferences = Objects.requireNonNull(getApplicationContext()).getSharedPreferences("com.example.stohre", MODE_PRIVATE);
        if (!sharedPreferences.getString("user", "").isEmpty()) {
            Gson gson = new Gson();
            String json = sharedPreferences.getString("user", "");
            user = gson.fromJson(json, User.class);
        }
    }

    private void readNotificationByUserId(final User user) {
        Log.v("READING NOTIFICATION","TRUE");
        service = APIInstance.getRetrofitInstance().create(APICalls.class);
        Call<Notification> call = service.readNotificationByUserId(user.getUSER_ID());
        call.enqueue(new Callback<Notification>() {
            @Override
            public void onResponse(Call<Notification> call, Response<Notification> response) {
                Log.v("NOTIFICATION SERVICE RESPONSE_CODE", String.valueOf(response.code()));
                Log.v("NOTIFICATION SERVICE BODY", String.valueOf(response.body()));
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        showNotification(response.body());
                        deleteNotification(response.body());
                    }
                }
            }
            @Override
            public void onFailure(Call<Notification> call, Throwable t) { }
        });
    }

    private void showNotification(Notification notification) {
        int randomNumber = random.nextInt(1000);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.launcher)
                .setContentTitle(notification.getSTORY_NAME())
                .setContentText(notification.getNOTIFICATION_TEXT())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(randomNumber, builder.build());
    }

    private void deleteNotification(final Notification notification) {
        service = APIInstance.getRetrofitInstance().create(APICalls.class);
        Call<POSTResponse> call = service.deleteNotification(notification);
        call.enqueue(new Callback<POSTResponse>() {
            @Override
            public void onResponse(Call<POSTResponse> call, Response<POSTResponse> response) {
                Log.v("NOTIFICATION SERVICE RESPONSE_CODE", String.valueOf(response.code()));
                Log.v("NOTIFICATION SERVICE BODY", String.valueOf(response.body()));
            }
            @Override
            public void onFailure(Call<POSTResponse> call, Throwable t) { }
        });
    }
}
