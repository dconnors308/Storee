package com.example.stohre.objects;

import java.io.Serializable;
import java.util.ArrayList;

public class Story implements Serializable {

    private String STORY_ID;
    private String STORY_NAME;
    private String STORY_TEXT;
    private String ACTIVE_EDITOR_NUM;
    private String USER_COUNT;
    private String DATE_CREATED;
    private String DATE_UPDATED;
    private ArrayList<Member> MEMBERS;
    private ArrayList<StoryEdit> EDITS;

    public Story(String STORY_NAME) {
        this.STORY_NAME = STORY_NAME;
    }

    public String getSTORY_ID() {
        return STORY_ID;
    }

    public void setSTORY_ID(String STORY_ID) {
        this.STORY_ID = STORY_ID;
    }

    public String getSTORY_NAME() {
        return STORY_NAME;
    }

    public void setSTORY_NAME(String STORY_NAME) {
        this.STORY_NAME = STORY_NAME;
    }

    public String getSTORY_TEXT() {
        return STORY_TEXT;
    }

    public void setSTORY_TEXT(String STORY_TEXT) {
        this.STORY_TEXT = STORY_TEXT;
    }

    public String getACTIVE_EDITOR_NUM() {
        return ACTIVE_EDITOR_NUM;
    }

    public void setACTIVE_EDITOR_NUM(String ACTIVE_EDITOR_NUM) {
        this.ACTIVE_EDITOR_NUM = ACTIVE_EDITOR_NUM;
    }

    public String getUSER_COUNT() {
        return USER_COUNT;
    }

    public void setUSER_COUNT(String USER_COUNT) {
        this.USER_COUNT = USER_COUNT;
    }

    public String getDATE_CREATED() {
        return DATE_CREATED;
    }

    public void setDATE_CREATED(String DATE_CREATED) {
        this.DATE_CREATED = DATE_CREATED;
    }

    public String getDATE_UPDATED() {
        return DATE_UPDATED;
    }

    public void setDATE_UPDATED(String DATE_UPDATED) {
        this.DATE_UPDATED = DATE_UPDATED;
    }

    public ArrayList<Member> getMEMBERS() {
        return MEMBERS;
    }

    public void setMEMBERS(ArrayList<Member> MEMBERS) {
        this.MEMBERS = MEMBERS;
    }

    public ArrayList<StoryEdit> getEDITS() {
        return EDITS;
    }

    public void setEDITS(ArrayList<StoryEdit> EDITS) {
        this.EDITS = EDITS;
    }
}
