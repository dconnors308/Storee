package com.example.stohre.fragments.notifications;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stohre.MainActivity;
import com.example.stohre.R;
import com.example.stohre.adapters.NotificationsAdapter;
import com.example.stohre.callbacks.CustomItemTouchHelper;
import com.example.stohre.database.DatabaseHelper;
import com.example.stohre.database.DatabaseModel;
import com.example.stohre.databinding.FragmentNotificationsBinding;
import com.example.stohre.objects.Notification;
import com.example.stohre.objects.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class NotificationsFragment extends Fragment {

    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private ProgressBar progressBar;
    private FragmentNotificationsBinding fragmentNotificationsBinding;
    private NotificationsAdapter notificationsAdapter;
    private ArrayList<Notification> notifications;
    private User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        progressBar = getActivity().findViewById(R.id.progress_bar_horizontal_activity_main);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentNotificationsBinding = FragmentNotificationsBinding.inflate(inflater, container, false);
        sharedPreferences = getActivity().getSharedPreferences("com.example.stohre", MODE_PRIVATE);
        if (!sharedPreferences.getString("user", "").isEmpty()) {
            Gson gson = new Gson();
            String json = sharedPreferences.getString("user", "");
            user = gson.fromJson(json, User.class);
            if (user != null) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }
        configureRecyclerView();
        return fragmentNotificationsBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_notifications, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_mark_all_as_read) {
            for (Notification notification: notifications) {
                markAsReadInLocalDb(notification);
                notifications.remove(notification);
                notificationsAdapter.notifyDataSetChanged();
            }
        }
        return(super.onOptionsItemSelected(item));
    }

    private void configureRecyclerView() {
        MainActivity mainActivity = (MainActivity) getActivity();
        readFromLocalDb();
        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.fall_down_animation);
        notificationsAdapter = new NotificationsAdapter(notifications, notification -> {
            if (notification.getNOTIFICATION_TYPE().equals("FRIEND_REQUEST") || notification.getNOTIFICATION_TYPE().equals("FRIEND_ACCEPT")) {
                Objects.requireNonNull(mainActivity).navController.navigate(R.id.action_fragment_notifications_to_fragment_friends);
            }
            else if (notification.getNOTIFICATION_TYPE().equals("NEW_STORY_MEMBER")) {
            }
            else if (notification.getNOTIFICATION_TYPE().equals("PENDING_STORY_EDIT")) {
            }
        });
        fragmentNotificationsBinding.fragmentNotificationsRecyclerView.setAdapter(notificationsAdapter);
        fragmentNotificationsBinding.fragmentNotificationsRecyclerView.setLayoutAnimation(animationController);
        enableSwipeToDeleteAndUndo();
        progressBar.setVisibility(View.GONE);
    }

    private void enableSwipeToDeleteAndUndo() {
        CustomItemTouchHelper swipeToDeleteCallback = new CustomItemTouchHelper(getActivity()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                int position = viewHolder.getAdapterPosition();
                Notification notification = notificationsAdapter.getData().get(position);
                markAsReadInLocalDb(notification);
                notificationsAdapter.removeItem(position);
                Snackbar snackbar = Snackbar.make(getView(), "notification was marked as read", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", view -> {
                    markAsUnreadInLocalDb(notification);
                    notificationsAdapter.restoreItem(notification, position);
                    fragmentNotificationsBinding.fragmentNotificationsRecyclerView.scrollToPosition(position);
                });
                snackbar.setActionTextColor(getActivity().getColor(R.color.primaryDarkColor));
                snackbar.show();
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(fragmentNotificationsBinding.fragmentNotificationsRecyclerView);
    }

    private void readFromLocalDb() {
        databaseHelper = new DatabaseHelper(getActivity());
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String[] projection = {
                DatabaseModel.NOTIFICATIONS.NOTIFICATION_ID,
                DatabaseModel.NOTIFICATIONS.STORY_NAME,
                DatabaseModel.NOTIFICATIONS.NOTIFICATION_TYPE,
                DatabaseModel.NOTIFICATIONS.NOTIFICATION_TEXT,
                DatabaseModel.NOTIFICATIONS.NOTIFICATION_STATUS,
                DatabaseModel.NOTIFICATIONS.DATE_CREATED
        };
        String selection = DatabaseModel.NOTIFICATIONS.NOTIFICATION_STATUS + " =?";
        String[] selectionArgs = { "UNREAD" };
        String sortOrder = DatabaseModel.NOTIFICATIONS.NOTIFICATION_ID + " ASC";
        Cursor cursor = db.query(
                DatabaseModel.NOTIFICATIONS.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
        notifications = new ArrayList<>();
        while(cursor.moveToNext()) {
            Notification notification = new Notification();
            notification.setNOTIFICATION_ID(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseModel.NOTIFICATIONS.NOTIFICATION_ID)));
            notification.setSTORY_NAME(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseModel.NOTIFICATIONS.STORY_NAME)));
            notification.setNOTIFICATION_TYPE(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseModel.NOTIFICATIONS.NOTIFICATION_TYPE)));
            notification.setNOTIFICATION_TEXT(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseModel.NOTIFICATIONS.NOTIFICATION_TEXT)));
            notification.setNOTIFICATION_STATUS(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseModel.NOTIFICATIONS.NOTIFICATION_STATUS)));
            notification.setDATE_CREATED(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseModel.NOTIFICATIONS.DATE_CREATED)));
            notifications.add(notification);
        }
        cursor.close();
        databaseHelper.close();
    }

    private void markAsReadInLocalDb(Notification notification) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        String value = "READ";
        ContentValues values = new ContentValues();
        values.put(DatabaseModel.NOTIFICATIONS.NOTIFICATION_STATUS, value);
        String selection = DatabaseModel.NOTIFICATIONS.NOTIFICATION_ID + "= ?";
        String[] selectionArgs = { notification.getNOTIFICATION_ID() };
        db.update(DatabaseModel.NOTIFICATIONS.TABLE_NAME, values, selection, selectionArgs);
    }

    private void markAsUnreadInLocalDb(Notification notification) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        String value = "UNREAD";
        ContentValues values = new ContentValues();
        values.put(DatabaseModel.NOTIFICATIONS.NOTIFICATION_STATUS, value);
        String selection = DatabaseModel.NOTIFICATIONS.NOTIFICATION_ID + "= ?";
        String[] selectionArgs = { notification.getNOTIFICATION_ID() };
        db.update(DatabaseModel.NOTIFICATIONS.TABLE_NAME, values, selection, selectionArgs);
    }



}