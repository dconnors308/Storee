package com.example.stohre.objects;

import java.io.Serializable;

public class Friend implements Serializable {
    String FRIEND_ID;
    String REQUESTER_USER_ID;
    String REQUESTER_USER_NAME;
    String ACCEPTER_USER_ID;
    String ACCEPTER_USER_NAME;
    String CONFIRMED;
    String LIFECYCLE;
    String DATE_CREATED;
    String DATE_UPDATED;

    public Friend(String REQUESTER_USER_ID, String ACCEPTER_USER_ID) {
        this.REQUESTER_USER_ID = REQUESTER_USER_ID;
        this.ACCEPTER_USER_ID = ACCEPTER_USER_ID;
    }

    public String getFRIEND_ID() {
        return FRIEND_ID;
    }

    public String getREQUESTER_USER_ID() {
        return REQUESTER_USER_ID;
    }

    public void setREQUESTER_USER_ID(String REQUESTER_USER_ID) {
        this.REQUESTER_USER_ID = REQUESTER_USER_ID;
    }

    public String getREQUESTER_USER_NAME() {
        return REQUESTER_USER_NAME;
    }

    public void setREQUESTER_USER_NAME(String REQUESTER_USER_NAME) {
        this.REQUESTER_USER_NAME = REQUESTER_USER_NAME;
    }

    public String getACCEPTER_USER_ID() {
        return ACCEPTER_USER_ID;
    }

    public void setACCEPTER_USER_ID(String ACCEPTER_USER_ID) {
        this.ACCEPTER_USER_ID = ACCEPTER_USER_ID;
    }

    public String getACCEPTER_USER_NAME() {
        return ACCEPTER_USER_NAME;
    }

    public void setACCEPTER_USER_NAME(String ACCEPTER_USER_NAME) {
        this.ACCEPTER_USER_NAME = ACCEPTER_USER_NAME;
    }

    public String getCONFIRMED() {
        return CONFIRMED;
    }

    public void setCONFIRMED(String CONFIRMED) {
        this.CONFIRMED = CONFIRMED;
    }

    public String getLIFECYCLE() {
        return LIFECYCLE;
    }

    public void setLIFECYCLE(String LIFECYCLE) {
        this.LIFECYCLE = LIFECYCLE;
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
