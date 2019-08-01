package com.example.stohre.objects;

public class Member {

    private String STORY_MEMBER_ID;
    private String STORY_ID;
    private String USER_ID;
    private String USER_NAME;
    private String MODERATOR;
    private String EDITING_ORDER;

    public Member(String STORY_ID, String USER_ID) {
        this.STORY_ID = STORY_ID;
        this.USER_ID = USER_ID;
    }

    public String getSTORY_MEMBER_ID() {
        return STORY_MEMBER_ID;
    }

    public void setSTORY_MEMBER_ID(String STORY_MEMBER_ID) {
        this.STORY_MEMBER_ID = STORY_MEMBER_ID;
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

    public String getEDITING_ORDER() {
        return EDITING_ORDER;
    }

    public void setEDITING_ORDER(String EDITING_ORDER) {
        this.EDITING_ORDER = EDITING_ORDER;
    }
}
