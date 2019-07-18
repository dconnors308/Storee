package com.example.stohre.objects;

public class Member extends User {

    private String MODERATOR;

    public Member(String MODERATOR) {
        this.MODERATOR = MODERATOR;
    }

    @Override
    public String getMODERATOR() {
        return MODERATOR;
    }

    @Override
    public void setMODERATOR(String MODERATOR) {
        this.MODERATOR = MODERATOR;
    }
}
