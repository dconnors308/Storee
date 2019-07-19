package com.example.stohre.adapters;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stohre.R;
import com.example.stohre.databinding.CardViewMembersBinding;
import com.example.stohre.objects.User;
import com.example.stohre.view_models.MembersViewModel;

import java.util.List;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MyViewHolder> {

    private List<User> users;
    private SelectionTracker<Long> selectionTracker;

    public MembersAdapter(List<User> users) {
        this.users = users;
    }

    public void setSelectionTracker(SelectionTracker<Long> selectionTracker) {
        this.selectionTracker = selectionTracker;
    }

    public static class Details extends ItemDetailsLookup.ItemDetails<Long> {

        long position;

        Details() {
        }

        @Override
        public int getPosition() {
            return (int) position;
        }

        @Nullable
        @Override
        public Long getSelectionKey() {
            return position;
        }

        @Override
        public boolean inSelectionHotspot(@NonNull MotionEvent e) {
            return true;
        }
    }

    public static class KeyProvider extends ItemKeyProvider<Long> {

        public KeyProvider(RecyclerView.Adapter adapter) {
            super(ItemKeyProvider.SCOPE_MAPPED);
        }

        @Nullable
        @Override
        public Long getKey(int position) {
            return (long) position;
        }

        @Override
        public int getPosition(@NonNull Long key) {
            long value = key;
            return (int) value;
        }
    }

    public static class DetailsLookup extends ItemDetailsLookup<Long> {

        private RecyclerView recyclerView;

        public DetailsLookup(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
        }

        @Nullable
        @Override
        public ItemDetails<Long> getItemDetails(@NonNull MotionEvent e) {
            View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
            if (view != null) {
                RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(view);
                if (viewHolder instanceof MyViewHolder) {
                    return ((MyViewHolder) viewHolder).getItemDetails();
                }
            }
            return null;
        }
    }

    public static class Predicate extends SelectionTracker.SelectionPredicate<Long> {

        @Override
        public boolean canSetStateForKey(@NonNull Long key, boolean nextState) {
            return true;
        }

        @Override
        public boolean canSetStateAtPosition(int position, boolean nextState) {
            return true;
        }

        @Override
        public boolean canSelectMultiple() {
            return true;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CardViewMembersBinding binding;
        private Details details;

        MyViewHolder(@NonNull CardViewMembersBinding itemRecyclerBinding) {
            super(itemRecyclerBinding.getRoot());
            binding = itemRecyclerBinding;
            details = new Details();
        }

        void bind(MembersViewModel viewModel, int position) {
            details.position = position;
            binding.setMembersViewModel(viewModel);
            binding.executePendingBindings();
            if (selectionTracker != null) {
                if (MembersAdapter.this.selectionTracker.isSelected(details.getSelectionKey())) {
                    binding.getMembersViewModel().backgroundColor.set(binding.getRoot().getContext().getColor(R.color.secondaryDarkColor));
                    binding.getRoot().setActivated(true);
                } else {
                    binding.getMembersViewModel().backgroundColor.set(binding.getRoot().getContext().getColor(R.color.secondaryColor));
                    binding.getRoot().setActivated(false);
                }
            }
        }

        Details getItemDetails() {
            return details;
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(CardViewMembersBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.bind(new MembersViewModel(users.get(i).getUSER_NAME()), i);
    }

    @Override
    public int getItemCount() {
        if (users == null) {
            return 0;
        }
        return users.size();
    }


}