package com.example.stohre.objects;

import java.util.ArrayList;

public class Story {

    public String STORY_ID;
    public String USER_ID;
    public String STORY_NAME;
    public String STORY_TEXT;
    public String DATE_CREATED;
    public String DATE_UPDATED;
    public ArrayList<User> MEMBERS;

    public Story(String STORY_ID, String USER_ID, String STORY_NAME) {
        this.STORY_ID = STORY_ID;
        this.USER_ID = USER_ID;
        this.STORY_NAME = STORY_NAME;
    }

    public String getSTORY_ID() {
        return STORY_ID;
    }

    public void setSTORY_ID(String STORY_ID) {
        this.STORY_ID = STORY_ID;
    }

    public String getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(String USER_ID) {
        this.USER_ID = USER_ID;
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

    public ArrayList<User> getMEMBERS() {
        return MEMBERS;
    }

    public void setMembers(ArrayList<User> MEMBERS) {
        this.MEMBERS = MEMBERS;
    }
}
