package com.example.stohre.adapters;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stohre.R;
import com.example.stohre.databinding.CardViewStoriesBinding;
import com.example.stohre.objects.Member;
import com.example.stohre.objects.Story;
import com.example.stohre.objects.User;
import com.example.stohre.view_models.StoriesViewModel;

import java.util.ArrayList;
import java.util.List;

public class StoriesAdapter extends RecyclerView.Adapter<StoriesAdapter.MyViewHolder> implements Filterable {

    private List<Story> stories;
    private SelectionTracker<Long> selectionTracker;
    private Context context;
    private User user;

    public StoriesAdapter(List<Story> stories, User user, Context context) {
        this.stories = stories;
        this.user = user;
        this.context = context;
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
            return false;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CardViewStoriesBinding binding;
        private Details details;
        MyViewHolder(@NonNull CardViewStoriesBinding itemRecyclerBinding) {
            super(itemRecyclerBinding.getRoot());
            binding = itemRecyclerBinding;
            details = new Details();
        }
        void bind(StoriesViewModel viewModel, int position) {
            context = binding.getRoot().getContext();
            details.position = position;
            binding.setStoriesViewModel(viewModel);
            binding.executePendingBindings();
            Story story = stories.get(position);
            ArrayList<Member> members = story.getMEMBERS();
            for(Member member: members) {
                if (member.getEDITING_ORDER().equals(story.getACTIVE_EDITOR_NUM())) { //user is active editor
                    if (member.getUSER_ID().equals(user.getUSER_ID())) {
                        manageBlinkEffect(binding.cardViewStoriesCardView);
                    }
                }
            }
        }
        Details getItemDetails() {
            return details;
        }
    }

    private void manageBlinkEffect(View view) {
        ObjectAnimator anim = ObjectAnimator.ofInt(view, "backgroundColor", context.getColor(R.color.off_white), context.getColor(R.color.secondaryColor), context.getColor(R.color.off_white));
        anim.setDuration(1500);
        anim.setEvaluator(new ArgbEvaluator());
        anim.setRepeatMode(ValueAnimator.RESTART);
        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.start();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(CardViewStoriesBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.bind(new StoriesViewModel(stories.get(i),user), i);
    }

    @Override
    public int getItemCount() {
        if (stories != null) {
            return stories.size();
        }
        return 0;
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                stories = (ArrayList<Story>) results.values;
                notifyDataSetChanged();
            }
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Story> filteredResults;
                if (constraint.length() == 0) {
                    filteredResults = stories;
                } else {
                    filteredResults = getFilteredResults(constraint.toString().toLowerCase());
                }
                FilterResults results = new FilterResults();
                results.values = filteredResults;
                return results;
            }
        };
    }

    protected List<Story> getFilteredResults(String constraint) {
        List<Story> results = new ArrayList<>();
        for (Story story : stories) {
            if (story.getSTORY_NAME().toLowerCase().contains(constraint)) {
                results.add(story);
            }
        }
        return results;
    }
}