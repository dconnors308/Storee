package com.example.stohre.objects;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;

import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.BaseObservable;

import com.example.stohre.R;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;

import static android.media.ThumbnailUtils.extractThumbnail;

public class Contact extends BaseObservable {

    public String id;
    public String name;
    public String number;
    public String photoPath;
    public Bitmap photoBitmap;
    public Context context;

    public Contact(Context context) {
        this.context = context;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
        if (!TextUtils.isEmpty(photoPath)) {
            this.photoBitmap = getBitmap(photoPath);
        }
        else {
            this.photoBitmap = drawableToBitmap(ResourcesCompat.getDrawable(context.getResources(), R.drawable.account_circle_grey_108x108,null));
        }
    }

    public Bitmap getPhotoBitmap() {
        return photoBitmap;
    }

    public void setPhotoBitmap(Bitmap photoBitmap) {
        this.photoBitmap = photoBitmap;
    }


    protected Bitmap getBitmap (String path ) {
        int width = 50; int height = 50;
        Bitmap toRecycle = decodeBitmap(path);
        Bitmap newThumbnail = extractThumbnail ( toRecycle, width, height );
        return newThumbnail;
    }
    protected Bitmap decodeBitmap ( String path ) {
        AssetFileDescriptor afd = null;
        Uri thumnailUri = Uri.parse(path);
        try {
            afd = context.getContentResolver().openAssetFileDescriptor(thumnailUri, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        FileDescriptor fileDescriptor = afd.getFileDescriptor();
        if (fileDescriptor != null) {
            return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, null);
        }
        return null;
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}