package com.example.stohre.view_models;

import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.View;

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
    public ObservableInt backgroundColor = new ObservableInt(R.color.transparent_white_50);
    public ObservableInt textColor = new ObservableInt(R.color.primaryTextColor);
    public int activeEditorNotificationVisibility = View.GONE;
    public int partiallyConfiguredNotificationVisibility = View.GONE;
    public boolean partiallyConfigured;
    public User user;

    public StoriesViewModel(Story story, User user) {
        this.user = user;
        this.storyName = story.getSTORY_NAME();
        partiallyConfigured = false;
        if (story.getMEMBERS() != null) {
            members = story.getMEMBERS();
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            User member;
            int stringTotalLength;
            stringTotalLength = 0;
            int segmentStart,segmentEnd;
            for(int i = 0; i < members.size(); i++) {
                member = members.get(i);
                if (member.getMODERATOR().equals("1")) { //get creator
                    this.creatorName = member.getUSER_NAME();
                }
                if (!TextUtils.isEmpty(member.getEDITING_ORDER())) {
                    stringTotalLength += member.getUSER_NAME().length();
                    if (member.getEDITING_ORDER().equals(story.getACTIVE_EDITOR_NUM())) { //user is active editor
                        if (member.getUSER_ID().equals(user.getUSER_ID())) {
                            activeEditorNotificationVisibility = View.VISIBLE; //show notification img when user is active
                        }
                        spannableStringBuilder.append(member.getUSER_NAME());
                        segmentStart = stringTotalLength - member.getUSER_NAME().length();
                        segmentEnd = stringTotalLength;
                        spannableStringBuilder.setSpan(new StyleSpan(Typeface.BOLD),segmentStart, segmentEnd, 0);  //bold active user
                    }
                    else {
                        spannableStringBuilder.append(member.getUSER_NAME());
                    }
                }
                else {
                    partiallyConfigured = true;
                    stringTotalLength += member.getUSER_NAME().length();
                    spannableStringBuilder.append(member.getUSER_NAME());
                }
                if((i + 1 < members.size())) {
                    spannableStringBuilder.append(", ");
                    stringTotalLength += 2;
                }
            }
            if (partiallyConfigured) {
                activeEditorNotificationVisibility = View.GONE;
                partiallyConfiguredNotificationVisibility = View.VISIBLE;
                spannableStringBuilder.clear();
                spannableStringBuilder.append("add some peeps!");
            }
            this.storyMembers = spannableStringBuilder;
        }
        else {
            partiallyConfigured = true;
            this.storyMembers = null;
        }
    }

}