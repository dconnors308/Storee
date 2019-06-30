package com.example.stohre.adapters;

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

import com.example.stohre.databinding.RecyclerItemContactsBinding;
import com.example.stohre.objects.Contact;
import com.example.stohre.view_models.ContactViewModel;
import com.example.stohre.R;

import java.util.ArrayList;
import java.util.List;


public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> implements Filterable {

    private List<Contact> contacts;
    private SelectionTracker<Long> selectionTracker;

    public ContactsAdapter(List<Contact> contacts) {
        this.contacts = contacts;
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
        private RecyclerItemContactsBinding binding;
        private Details details;
        MyViewHolder(@NonNull RecyclerItemContactsBinding itemRecyclerBinding) {
            super(itemRecyclerBinding.getRoot());
            binding = itemRecyclerBinding;
            details = new Details();
        }
        void bind(ContactViewModel viewModel, int position) {
            details.position = position;
            binding.setContactViewModel(viewModel);
            binding.executePendingBindings();
            if (selectionTracker != null) {
                if (ContactsAdapter.this.selectionTracker.isSelected(details.getSelectionKey())) {
                    binding.getContactViewModel().backgroundColor.set(binding.getRoot().getContext().getColor(R.color.secondaryColor));
                    binding.getRoot().setActivated(true);
                } else {
                    binding.getContactViewModel().backgroundColor.set(binding.getRoot().getContext().getColor(R.color.secondaryLightColor));
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
        return new MyViewHolder(
                RecyclerItemContactsBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.bind(new ContactViewModel(contacts.get(i)), i);
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                contacts = (ArrayList<Contact>) results.values;
                notifyDataSetChanged();
            }
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Contact> filteredResults;
                if (constraint.length() == 0) {
                    filteredResults = contacts;
                } else {
                    filteredResults = getFilteredResults(constraint.toString().toLowerCase());
                }
                FilterResults results = new FilterResults();
                results.values = filteredResults;
                return results;
            }
        };
    }

    protected List<Contact> getFilteredResults(String constraint) {
        List<Contact> results = new ArrayList<>();
        for (Contact item : contacts) {
            if (item.getName().toLowerCase().contains(constraint)) {
                results.add(item);
            }
        }
        return results;
    }
}