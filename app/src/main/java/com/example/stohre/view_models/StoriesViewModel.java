package com.example.stohre.view_models;

import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.View;

import androidx.databinding.ObservableInt;

import com.example.stohre.R;
import com.example.stohre.objects.Member;
import com.example.stohre.objects.Story;
import com.example.stohre.objects.StoryEdit;
import com.example.stohre.objects.User;

import java.util.ArrayList;

public class StoriesViewModel {

    public String storyName;
    public String lastEdit;
    public ArrayList<StoryEdit> edits;
    public ArrayList<Member> members;
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
        members = story.getMEMBERS();
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        Member member;
        int stringTotalLength,segmentStart,segmentEnd;
        stringTotalLength = 0;
        for(int i = 0; i < members.size(); i++) {
            member = members.get(i);
            stringTotalLength += member.getUSER_NAME().length();
            if (member.getEDITING_ORDER().equals(story.getACTIVE_EDITOR_NUM())) { //user is active editor
                spannableStringBuilder.append(member.getUSER_NAME());
                segmentStart = stringTotalLength - member.getUSER_NAME().length();
                segmentEnd = stringTotalLength;
                spannableStringBuilder.setSpan(new StyleSpan(Typeface.BOLD), segmentStart, segmentEnd, 0);  //bold active user
            }
            else {
                spannableStringBuilder.append(member.getUSER_NAME());
            }
            if ((i + 1 < members.size())) {
                spannableStringBuilder.append(", ");
                stringTotalLength += 2;
            }
            this.storyMembers = spannableStringBuilder;
            edits = story.getEDITS();
            for (StoryEdit edit: edits) {
                lastEdit = '"' + edit.getSTORY_TEXT() + '"' + " - " + edit.getUSER_NAME();
            }
        }
    }

}