package com.example.stohre.view_models;

import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StyleSpan;

import androidx.databinding.ObservableInt;

import com.example.stohre.R;
import com.example.stohre.objects.Story;
import com.example.stohre.objects.User;

import java.util.ArrayList;

public class StoriesViewModel {

    public String storyName;
    public String creatorName;
    public ArrayList<User> members;
    public SpannableStringBuilder storyMembers;
    public final ObservableInt backgroundColor = new ObservableInt(R.color.secondaryColor);
    public final ObservableInt textColor = new ObservableInt(R.color.primaryTextColor);

    public StoriesViewModel(Story story) {
        this.storyName = story.getSTORY_NAME();
        if (story.getMEMBERS() != null) {
            members = story.getMEMBERS();
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            User member;
            int stringTotalLength;
            stringTotalLength = 0;
            for(int i = 0; i < members.size(); i++) {
                member = members.get(i);
                if (member.getMODERATOR().equals("1")) {
                    this.creatorName = member.getUSER_NAME();
                }
                if (!TextUtils.isEmpty(member.getEDITING_ORDER())) {
                    if (member.getEDITING_ORDER().equals(story.getACTIVE_EDITOR_NUM())) {
                        spannableStringBuilder.append(member.getUSER_NAME());
                        spannableStringBuilder.setSpan(new StyleSpan(Typeface.BOLD), stringTotalLength,stringTotalLength + member.getUSER_NAME().length(), 0);
                    }
                    else {
                        spannableStringBuilder.append(member.getUSER_NAME());
                    }
                }
                else {
                    spannableStringBuilder.append(member.getUSER_NAME());
                }
                stringTotalLength += member.getUSER_NAME().length() + 2;
                if((i + 1 < members.size())) {
                    spannableStringBuilder.append(", ");
                }
            }
            this.storyMembers = spannableStringBuilder;
        }
        else {
            this.storyMembers = null;
        }
    }

}