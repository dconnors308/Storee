package com.example.stohre.api;

public class CreateStoryRequest {
    private String USER_ID;
    private String STORY_NAME;

    public CreateStoryRequest(String USER_ID, String STORY_NAME) {
        this.USER_ID = USER_ID;
        this.STORY_NAME = STORY_NAME;
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
}
