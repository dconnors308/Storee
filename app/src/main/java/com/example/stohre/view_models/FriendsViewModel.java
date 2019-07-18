package com.example.stohre.view_models;

import androidx.databinding.ObservableInt;

import com.example.stohre.R;

public class FriendsViewModel {


    public final ObservableInt backgroundColor = new ObservableInt(R.color.primaryDarkColor);
    public final ObservableInt textColor = new ObservableInt(R.color.primaryTextColor);

    private String USER_NAME;

    public FriendsViewModel(String USER_NAME) {
        this.USER_NAME = USER_NAME;
    }

    public String getUSER_NAME() {
        return USER_NAME;
    }

    public void setUSER_NAME(String USER_NAME) {
        this.USER_NAME = USER_NAME;
    }
}