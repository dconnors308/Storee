package com.example.stohre.database;

import android.provider.BaseColumns;

public final class DatabaseModel {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DatabaseModel() {}

    /* Inner class that defines the table contents */
    public static class NOTIFICATIONS implements BaseColumns {
        public static final String TABLE_NAME = "NOTIFICATIONS";
        public static final String NOTIFICATION_ID = "NOTIFICATION_ID";
        public static final String STORY_NAME = "STORY_NAME";
        public static final String NOTIFICATION_TYPE = "NOTIFICATION_TYPE";
        public static final String NOTIFICATION_TEXT = "NOTIFICATION_TEXT";
        public static final String NOTIFICATION_STATUS = "NOTIFICATION_STATUS";
        public static final String DATE_CREATED = "DATE_CREATED";
    }
}