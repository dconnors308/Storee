package com.example.stohre.objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Notifications {

    @SerializedName("records")
    @Expose
    private ArrayList<Notification> Notifications = null;

    public ArrayList<Notification> getNotifications() {
        return Notifications;
    }

    public void setNotifications(ArrayList<Notification> Notifications) {
        this.Notifications = Notifications;
    }
}
