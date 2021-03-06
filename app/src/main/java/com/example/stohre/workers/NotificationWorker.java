package com.example.stohre.workers;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import com.example.stohre.database.DatabaseHelper;
import com.example.stohre.database.DatabaseModel;
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
    private DatabaseHelper databaseHelper;


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
        Call<Notification> call = service.readNotification(user.getUSER_ID());
        call.enqueue(new Callback<Notification>() {
            @Override
            public void onResponse(Call<Notification> call, Response<Notification> response) {
                Log.v("NOTIFICATION SERVICE RESPONSE_CODE", String.valueOf(response.code()));
                Log.v("NOTIFICATION SERVICE BODY", String.valueOf(response.body()));
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Notification notification = response.body();
                        showNotification(notification);
                        addToLocalDb(notification);
                        deleteNotification(notification);
                    }
                }
            }
            @Override
            public void onFailure(Call<Notification> call, Throwable t) { }
        });
    }

    private void addToLocalDb(Notification notification) {
        databaseHelper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String[] projection = {
                DatabaseModel.NOTIFICATIONS.NOTIFICATION_ID,
                DatabaseModel.NOTIFICATIONS.STORY_NAME,
                DatabaseModel.NOTIFICATIONS.NOTIFICATION_TYPE,
                DatabaseModel.NOTIFICATIONS.NOTIFICATION_TEXT,
                DatabaseModel.NOTIFICATIONS.NOTIFICATION_STATUS,
                DatabaseModel.NOTIFICATIONS.DATE_CREATED
        };
        String selection = DatabaseModel.NOTIFICATIONS.NOTIFICATION_ID + " = ?";
        String[] selectionArgs = { notification.getNOTIFICATION_ID() };
        String sortOrder = DatabaseModel.NOTIFICATIONS.NOTIFICATION_ID + " DESC";
        Cursor cursor = db.query(
                DatabaseModel.NOTIFICATIONS.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
        Log.i("CURSOR COUNT",String.valueOf(cursor.getCount()));
        if (cursor.getCount() < 1) {
            ContentValues values = new ContentValues();
            values.put(DatabaseModel.NOTIFICATIONS.NOTIFICATION_ID, notification.getNOTIFICATION_ID());
            values.put(DatabaseModel.NOTIFICATIONS.STORY_NAME, notification.getSTORY_NAME());
            values.put(DatabaseModel.NOTIFICATIONS.NOTIFICATION_TYPE, notification.getNOTIFICATION_TYPE());
            values.put(DatabaseModel.NOTIFICATIONS.NOTIFICATION_TEXT, notification.getNOTIFICATION_TEXT());
            values.put(DatabaseModel.NOTIFICATIONS.NOTIFICATION_STATUS, notification.getNOTIFICATION_STATUS());
            values.put(DatabaseModel.NOTIFICATIONS.DATE_CREATED, notification.getDATE_CREATED());
            db.insert(DatabaseModel.NOTIFICATIONS.TABLE_NAME, null, values);
        }
        cursor.close();
    }

    private void showNotification(Notification notification) {
        int randomNumber = random.nextInt(1000);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.launcher)
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
