package com.example.stohre.api;

public class RequestAddUserToStory {
    private String USER_ID;
    private String STORY_ID;

    public RequestAddUserToStory(String USER_ID, String STORY_ID) {
        this.USER_ID = USER_ID;
        this.STORY_ID = STORY_ID;
    }

    public String getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(String USER_ID) {
        this.USER_ID = USER_ID;
    }

    public String getSTORY_ID() {
        return STORY_ID;
    }

    public void setSTORY_ID(String STORY_ID) {
        this.STORY_ID = STORY_ID;
    }
}
