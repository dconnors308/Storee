package com.example.stohre.utilities;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.example.stohre.objects.Contact;

import java.util.ArrayList;

public class ContactsReceiver {
    private ArrayList<Contact> contacts;
    private Context context;

    public ContactsReceiver(Context context) {
        this.context = context;
    }

    public ArrayList<Contact> getAllContacts() {
        contacts = new ArrayList();
        Contact contact;
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    contact = new Contact(context);
                    contact.setId(id);
                    contact.setName(name);
                    Cursor additionalDetailsCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id},
                            null);
                    if (additionalDetailsCursor.moveToNext()) {
                        String phoneNumber = additionalDetailsCursor.getString(additionalDetailsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        String photoPath = additionalDetailsCursor.getString(additionalDetailsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO_THUMBNAIL_URI));
                        contact.setNumber(phoneNumber);
                        contact.setPhotoPath(photoPath);
                    }
                    additionalDetailsCursor.close();
                    contacts.add(contact);
                }
            }
        }
        return contacts;
    }
}
