package com.example.stohre.objects;

public class Notification {

    private String NOTIFICATION_ID;
    private String USER_ID;
    private String USER_NAME;
    private String STORY_NAME;
    private String NOTIFICATION_TYPE;
    private String NOTIFICATION_TEXT;
    private String NOTIFICATION_STATUS;
    private String DATE_CREATED;
    private String DATE_LAST_SENT;

    public String getNOTIFICATION_ID() {
        return NOTIFICATION_ID;
    }

    public void setNOTIFICATION_ID(String NOTIFICATION_ID) {
        this.NOTIFICATION_ID = NOTIFICATION_ID;
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

    public String getSTORY_NAME() {
        return STORY_NAME;
    }

    public void setSTORY_NAME(String STORY_NAME) {
        this.STORY_NAME = STORY_NAME;
    }

    public String getNOTIFICATION_TYPE() {
        return NOTIFICATION_TYPE;
    }

    public void setNOTIFICATION_TYPE(String NOTIFICATION_TYPE) {
        this.NOTIFICATION_TYPE = NOTIFICATION_TYPE;
    }

    public String getNOTIFICATION_TEXT() {
        return NOTIFICATION_TEXT;
    }

    public void setNOTIFICATION_TEXT(String NOTIFICATION_TEXT) {
        this.NOTIFICATION_TEXT = NOTIFICATION_TEXT;
    }

    public String getNOTIFICATION_STATUS() {
        return NOTIFICATION_STATUS;
    }

    public void setNOTIFICATION_STATUS(String NOTIFICATION_STATUS) {
        this.NOTIFICATION_STATUS = NOTIFICATION_STATUS;
    }

    public String getDATE_CREATED() {
        return DATE_CREATED;
    }

    public void setDATE_CREATED(String DATE_CREATED) {
        this.DATE_CREATED = DATE_CREATED;
    }

    public String getDATE_LAST_SENT() {
        return DATE_LAST_SENT;
    }

    public void setDATE_LAST_SENT(String DATE_LAST_SENT) {
        this.DATE_LAST_SENT = DATE_LAST_SENT;
    }
}
