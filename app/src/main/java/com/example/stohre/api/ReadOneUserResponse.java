package com.example.stohre.api;

public class ReadOneUserResponse {
    private String USER_ID;
    private String USER_NAME;
    private String DATE_CREATED;

    public String getUSER_ID() {
        return USER_ID;
    }

    public String getUSER_NAME() {
        return USER_NAME;
    }

    public String getDATE_CREATED() {
        return DATE_CREATED;
    }

    public void setUSER_ID(String USER_ID) {
        this.USER_ID = USER_ID;
    }

    public void setUSER_NAME(String USER_NAME) {
        this.USER_NAME = USER_NAME;
    }

    public void setDATE_CREATED(String DATE_CREATED) {
        this.DATE_CREATED = DATE_CREATED;
    }
}