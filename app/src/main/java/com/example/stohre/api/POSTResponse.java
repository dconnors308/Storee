package com.example.stohre.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class POSTResponse implements Serializable {

    @SerializedName("message")
    @Expose
    private String message;

}