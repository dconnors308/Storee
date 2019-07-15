package com.example.stohre.objects;

import android.net.Uri;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Serializable {

    private String USER_ID;
    private String USER_NAME;
    private String DATE_CREATED;
    private String PHOTO_URI;

    public String getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(String USER_ID) {
        this.USER_ID = USER_ID;
    }

    public String getUSER_NAME() {
        return USER_NAME;
    }

    public void setUSER_NAME(String USER_NAME) {
        this.USER_NAME = USER_NAME;
    }

    public String getDATE_CREATED() {
        return DATE_CREATED;
    }

    public void setDATE_CREATED(String DATE_CREATED) {
        this.DATE_CREATED = DATE_CREATED;
    }

    public String getPHOTO_URI() {
        return PHOTO_URI;
    }

    public void setPHOTO_URI(String PHOTO_URI) {
        this.PHOTO_URI = PHOTO_URI;
    }
}