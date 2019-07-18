package com.example.stohre.objects;

import java.io.Serializable;

public class User implements Serializable {

    private String USER_ID;
    private String USER_NAME;
    private String DATE_CREATED;
    private String PHOTO_URI;
    private String MODERATOR;

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

    public String getMODERATOR() {
        return MODERATOR;
    }

    public void setMODERATOR(String MODERATOR) {
        this.MODERATOR = MODERATOR;
    }
}