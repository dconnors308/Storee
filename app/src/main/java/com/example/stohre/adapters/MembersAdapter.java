package com.example.stohre.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stohre.R;
import com.example.stohre.objects.Member;

import java.util.ArrayList;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.ViewHolder> {

    private ArrayList<Member> members;

    public MembersAdapter(ArrayList<Member> members) {
        this.members = members;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView usernameTextView;

        private ViewHolder(View view) {
            super(view);
            usernameTextView = view.findViewById(R.id.card_view_members_text_view);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_members,parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Member member = members.get(position);
        viewHolder.usernameTextView.setText(member.getUSER_NAME());
    }

    @Override
    public int getItemCount() {
        return members.size();
    }


}