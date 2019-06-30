package com.example.stohre.view_models;

import androidx.databinding.ObservableInt;

import com.example.stohre.R;
import com.example.stohre.objects.Story;

public class StoriesViewModel {

    public final String storyName;
    public final ObservableInt backgroundColor = new ObservableInt(R.color.primaryDarkColor);
    public final ObservableInt textColor = new ObservableInt(R.color.primaryTextColor);

    public StoriesViewModel(Story story) {
        this.storyName = story.getSTORY_NAME();
    }
}