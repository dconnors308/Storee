package com.example.stohre.view_models;

import android.text.TextUtils;

import androidx.databinding.ObservableInt;

import com.example.stohre.R;
import com.example.stohre.objects.Story;
import com.example.stohre.objects.User;

import java.util.ArrayList;

public class StoriesViewModel {

    public final String storyName;
    public ArrayList<User> members;
    public final String storyMembers;
    public final ObservableInt backgroundColor = new ObservableInt(R.color.primaryDarkColor);
    public final ObservableInt textColor = new ObservableInt(R.color.primaryTextColor);

    public StoriesViewModel(Story story) {
        if (story.getMEMBERS() != null) {
            String membersString = "";
            ArrayList<String> usernames = new ArrayList<>();
            members = story.getMEMBERS();
            for (User user: members) {
                usernames.add(user.getUSER_NAME());
            }

            this.storyMembers = TextUtils.join(", ", usernames);
        }
        else {
            this.storyMembers = "";
        }
        this.storyName = story.getSTORY_NAME();
    }

}