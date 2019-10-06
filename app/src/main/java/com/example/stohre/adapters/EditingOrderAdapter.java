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

public class EditingOrderAdapter extends RecyclerView.Adapter<EditingOrderAdapter.ViewHolder> {

    private ArrayList<Member> members;
    private ViewGroup parent;
    private View editingOrderCardView;

    public EditingOrderAdapter(ArrayList<Member> members) {
        this.members = members;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView usernameTextView;
        private TextView editingOrderTextView;

        private ViewHolder(View view) {
            super(view);
            usernameTextView = view.findViewById(R.id.card_view_editing_order_user_name_text_view);
            editingOrderTextView = view.findViewById(R.id.card_view_editing_order_editing_order_text_view);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent = parent;
        editingOrderCardView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_editing_order,parent, false);
        return new ViewHolder(editingOrderCardView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Member member = members.get(position);
        viewHolder.usernameTextView.setText(member.getUSER_NAME());
        viewHolder.editingOrderTextView.setText(String.valueOf(position + 1));
    }

    public void updateEditingOrder(ViewHolder viewHolder,ViewHolder targetHolder) {
        viewHolder.editingOrderTextView.setText(String.valueOf(viewHolder.getAdapterPosition() + 1));
        targetHolder.editingOrderTextView.setText(String.valueOf(targetHolder.getAdapterPosition() + 1));
    }

    @Override
    public int getItemCount() {
        return members.size();
    }
}