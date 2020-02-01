package com.example.stohre.adapters;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stohre.R;
import com.example.stohre.objects.Friend;
import com.example.stohre.objects.User;

import java.util.ArrayList;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    private ArrayList<Friend> friends;
    private View friendsCardView;
    private User user;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Friend friend);
    }

    public FriendsAdapter(ArrayList<Friend> friends, User user, OnItemClickListener listener) {
        this.friends = friends;
        this.user = user;
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView usernameTextView;

        private ViewHolder(View view) {
            super(view);
            usernameTextView = view.findViewById(R.id.card_view_friends_user_name_text_view);
        }
        public void bind(final Friend friend, final OnItemClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(friend));
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        friendsCardView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_friends,parent, false);
        return new ViewHolder(friendsCardView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.bind(friends.get(position), listener);
        Friend friend = friends.get(position);
        if (friend.getREQUESTER_USER_NAME().equals(user.getUSER_NAME())) {
            viewHolder.usernameTextView.setText(friend.getACCEPTER_USER_NAME());
        }
        else if (friend.getACCEPTER_USER_NAME().equals(user.getUSER_NAME())) {
            viewHolder.usernameTextView.setText(friend.getREQUESTER_USER_NAME());
        }
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }
}