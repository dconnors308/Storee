package com.example.stohre.view_models;

import androidx.databinding.ObservableInt;

import com.example.stohre.R;

public class EditingOrderViewModel {

    private final String username;
    public final ObservableInt backgroundColor = new ObservableInt(R.color.secondaryColor);
    public final ObservableInt textColor = new ObservableInt(R.color.primaryDarkColor);

    public EditingOrderViewModel(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
