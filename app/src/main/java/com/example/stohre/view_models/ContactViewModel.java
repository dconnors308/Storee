package com.example.stohre.view_models;

import android.databinding.BindingAdapter;
import android.databinding.ObservableInt;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.example.stohre.objects.Contact;
import com.example.stohre.R;

public class ContactViewModel {

    public final String name;
    public final Bitmap photoBitmap;
    public final ObservableInt backgroundColor = new ObservableInt(R.color.primaryDarkColor);
    public final ObservableInt textColor = new ObservableInt(R.color.primaryTextColor);

    public ContactViewModel(Contact contact) {
        this.name = contact.name;
        this.photoBitmap = contact.photoBitmap;
    }

    @BindingAdapter("android:src")
    public static void setImageDrawable(ImageView view, Bitmap bitmap) {
        view.setImageBitmap(bitmap);
    }
}