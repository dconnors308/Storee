package com.example.stohre.workers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.stohre.api.APICalls;
import com.example.stohre.api.APIInstance;
import com.example.stohre.api.POSTResponse;
import com.example.stohre.database.DatabaseHelper;
import com.example.stohre.database.DatabaseModel;
import com.example.stohre.objects.Notification;
import com.example.stohre.objects.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationRefresher {

    private APICalls service;
    private DatabaseHelper databaseHelper;
    private Context context;

    public NotificationRefresher(Context context) {
        this.context = context;
    }

    public void readNotificationByUserId(final User user) {
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
        databaseHelper = new DatabaseHelper(context);
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
