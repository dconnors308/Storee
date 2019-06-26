package com.example.stohre.objects;

import java.util.ArrayList;

public class Group {
    private int groupId;
    private ArrayList<Contact> contactsArrayList;

    public Group(int groupId, ArrayList<Contact> contactsArrayList) {
        this.groupId = groupId;
        this.contactsArrayList = contactsArrayList;
    }
}
