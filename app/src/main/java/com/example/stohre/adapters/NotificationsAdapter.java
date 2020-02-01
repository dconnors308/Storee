package com.example.stohre.adapters;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stohre.R;
import com.example.stohre.objects.Notification;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    private ArrayList<Notification> notifications;
    private View notificationsCardView;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Notification notification);
    }

    public NotificationsAdapter(ArrayList<Notification> notifications, OnItemClickListener listener) {
        this.notifications = notifications;
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView notificationTextView, notificationTimeElapsedTextView;
        private ImageView notificationImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            notificationTextView = itemView.findViewById(R.id.card_view_notification_text_view);
            notificationImageView = itemView.findViewById(R.id.card_view_notification_image_view);
            notificationTimeElapsedTextView = itemView.findViewById(R.id.card_view_notification_elapsed_time_text_view);
        }

        public void bind(final Notification notification, final OnItemClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(notification));
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        notificationsCardView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_notifications,parent, false);
        return new ViewHolder(notificationsCardView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.bind(notifications.get(position), listener);
        Notification notification = notifications.get(position);
        viewHolder.notificationTextView.setText(notification.getNOTIFICATION_TEXT());
        if (notification.getNOTIFICATION_TYPE().equals("FRIEND_REQUEST") || notification.getNOTIFICATION_TYPE().equals("FRIEND_ACCEPT")) {
            viewHolder.notificationImageView.setImageResource(R.drawable.ic_person_grey_600_48dp);
        }
        else if (notification.getNOTIFICATION_TYPE().equals("NEW_STORY_MEMBER")) {
            viewHolder.notificationImageView.setImageResource(R.drawable.ic_group_add_grey_600_48dp);
        }
        else if (notification.getNOTIFICATION_TYPE().equals("PENDING_STORY_EDIT")) {
            viewHolder.notificationImageView.setImageResource(R.drawable.ic_library_books_grey_600_48dp);
        }
        viewHolder.notificationTimeElapsedTextView.setText(getElapsedTime(notification.getDATE_CREATED()));
    }

    public void removeItem(int position) {
        notifications.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Notification notification, int position) {
        notifications.add(position, notification);
        notifyItemInserted(position);
    }

    public ArrayList<Notification> getData() {
        return notifications;
    }


    @Override
    public int getItemCount() {
        return notifications.size();
    }

    private String getElapsedTime(String dateIn) {
        Log.v("DATE_CREATED",dateIn);
        Date dateCreated = null;
        Date today = null;
        SimpleDateFormat sdfMySql = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        OffsetDateTime now = OffsetDateTime.now( ZoneOffset.UTC );
        Log.v("NOW",now.toString());
        try {
            dateCreated = sdfMySql.parse(dateIn);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            today = sdfNow.parse(now.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdfMySql.format(dateCreated);
        Duration diff = Duration.between(dateCreated.toInstant(), today.toInstant());
        long days = diff.toDays();
        diff = diff.minusDays(days);
        long hours = diff.toHours();
        diff = diff.minusHours(hours);
        long minutes = diff.toMinutes();
        diff = diff.minusMinutes(minutes);
        long seconds = diff.toMillis();
        String elapsedTime = "";
        if (days != 0) {
            if(days == 1){
                elapsedTime = days + " day ago";
            }
            else {
                elapsedTime = days + " days ago";
            }
        }
        if (elapsedTime.equals("")) {
            if (hours!=0) {
                if(hours == 1){
                    elapsedTime = hours + " hour ago";
                }
                else{
                    elapsedTime = hours + " hours ago";
                }
            }
        }
        if (elapsedTime.equals("")) {
            if (minutes != 0) {
                if (minutes == 1) {
                    elapsedTime = minutes + " minute ago";
                }
                else {
                    elapsedTime = minutes + " minutes ago";
                }
            }
        }
        if (elapsedTime.equals("")) {
            if (seconds != 0) {
                if (minutes == 1) {
                    elapsedTime = seconds + " second ago";
                }
                else {
                    elapsedTime = seconds + " seconds ago";
                }
            }
        }
        return elapsedTime;
    }
}