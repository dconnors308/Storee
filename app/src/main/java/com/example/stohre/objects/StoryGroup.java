package com.example.stohre.objects;

public class StoryGroup {

    private String STORY_GROUP_ID;
    private String STORY_ID;
    private String USER_ID;
    private String USER_NAME;
    private String MODERATOR;

    public StoryGroup(String STORY_ID, String USER_ID) {
        this.STORY_ID = STORY_ID;
        this.USER_ID = USER_ID;
    }

    public String getSTORY_GROUP_ID() {
        return STORY_GROUP_ID;
    }

    public void setSTORY_GROUP_ID(String STORY_GROUP_ID) {
        this.STORY_GROUP_ID = STORY_GROUP_ID;
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

    public String getMODERATOR() {
        return MODERATOR;
    }

    public void setMODERATOR(String MODERATOR) {
        this.MODERATOR = MODERATOR;
    }
}
