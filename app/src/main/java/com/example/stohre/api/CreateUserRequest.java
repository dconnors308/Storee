package com.example.stohre.api;

import java.math.BigInteger;

public class CreateUserRequest {
    private String USER_ID;
    private String USER_NAME;

    public CreateUserRequest(String USER_ID, String USER_NAME) {
        this.USER_ID = USER_ID;
        this.USER_NAME = USER_NAME;
    }

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
}
