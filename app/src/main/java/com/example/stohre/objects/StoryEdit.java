package com.example.stohre.objects;

public class StoryEdit {

    private String STORY_EDIT_ID;
    private String STORY_ID;
    private String USER_ID;
    private String USER_NAME;
    private String STORY_TEXT;
    private String DATE_CREATED;
    private String DATE_UPDATED;

    public StoryEdit(String STORY_ID, String USER_ID, String STORY_TEXT) {
        this.STORY_ID = STORY_ID;
        this.USER_ID = USER_ID;
        this.STORY_TEXT = STORY_TEXT;
    }

    public String getSTORY_EDIT_ID() {
        return STORY_EDIT_ID;
    }

    public void setSTORY_EDIT_ID(String STORY_EDIT_ID) {
        this.STORY_EDIT_ID = STORY_EDIT_ID;
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

    public String getUSER_NAME() {
        return USER_NAME;
    }

    public void setUSER_NAME(String USER_NAME) {
        this.USER_NAME = USER_NAME;
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
}
