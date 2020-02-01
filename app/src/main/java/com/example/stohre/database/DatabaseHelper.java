package com.example.stohre.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Storee.db";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DatabaseModel.NOTIFICATIONS.TABLE_NAME + " (" +
                    DatabaseModel.NOTIFICATIONS.NOTIFICATION_ID + " INTEGER PRIMARY KEY," +
                    DatabaseModel.NOTIFICATIONS.STORY_NAME + " TEXT," +
                    DatabaseModel.NOTIFICATIONS.NOTIFICATION_TEXT + " TEXT," +
                    DatabaseModel.NOTIFICATIONS.NOTIFICATION_TYPE + " TEXT," +
                    DatabaseModel.NOTIFICATIONS.NOTIFICATION_STATUS + " TEXT," +
                    DatabaseModel.NOTIFICATIONS.DATE_CREATED + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DatabaseModel.NOTIFICATIONS.TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}