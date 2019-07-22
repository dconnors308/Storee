package com.example.stohre.view_models;

import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;

import androidx.databinding.ObservableInt;

import com.example.stohre.R;
import com.example.stohre.objects.Story;
import com.example.stohre.objects.User;

import java.util.ArrayList;

public class StoriesViewModel {

    public final String storyName;
    public ArrayList<User> members;
    public final SpannableStringBuilder storyMembers;
    public final ObservableInt backgroundColor = new ObservableInt(R.color.secondaryColor);
    public final ObservableInt textColor = new ObservableInt(R.color.primaryTextColor);

    public StoriesViewModel(Story story) {
        if (story.getMEMBERS() != null) {
            members = story.getMEMBERS();
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            User member;
            for(int i = 0; i < members.size(); i++) {
                member = members.get(i);
                if (member.getMODERATOR().equals("1")) {
                    spannableStringBuilder.append(member.getUSER_NAME());
                    spannableStringBuilder.setSpan(new StyleSpan(Typeface.BOLD), 0, member.getUSER_NAME().length(), 0);
                }
                else {
                    spannableStringBuilder.append(member.getUSER_NAME());
                }
                if((i + 1 < members.size())) {
                    spannableStringBuilder.append(", ");
                }
            }
            this.storyMembers = spannableStringBuilder;
        }
        else {
            this.storyMembers = null;
        }
        this.storyName = story.getSTORY_NAME();
    }

}