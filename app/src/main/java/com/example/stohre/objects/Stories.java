package com.example.stohre.objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Stories implements Serializable {

    @SerializedName("STORIES")
    @Expose
    private ArrayList<Story> stories = null;

    public ArrayList<Story> getStories() {
        return stories;
    }

    public void setStories(ArrayList<Story> stories) {
        this.stories = stories;
    }

}
