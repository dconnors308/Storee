package com.example.stohre.objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class StoryGroups {

    @SerializedName("records")
    @Expose
    private ArrayList<StoryGroup> storyGroups = null;

    public ArrayList<StoryGroup> getStoryGroups() {
        return storyGroups;
    }

    public void setStoryGroups(ArrayList<StoryGroup> storyGroups) {
        this.storyGroups = storyGroups;
    }
}
