package com.example.stohre.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.selection.Selection;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stohre.R;
import com.example.stohre.adapters.ContactsAdapter;
import com.example.stohre.databinding.FragmentContactsBinding;
import com.example.stohre.objects.Contact;
import com.example.stohre.utilities.ContactsReceiver;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class ContactsFragment extends Fragment implements SearchView.OnQueryTextListener {

    private RecyclerView contactsRecyclerView;
    private SearchView searchView;
    private ContactsAdapter contactsAdapter;
    private ArrayList<Contact> contacts;
    private FragmentContactsBinding fragmentContactsBinding;
    private SelectionTracker<Long> selectionTracker;
    private ActionMode actionMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            selectionTracker.onRestoreInstanceState(savedInstanceState);
        }
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        fragmentContactsBinding = FragmentContactsBinding.inflate(inflater, container, false);
        contacts = new ContactsReceiver(getActivity()).getAllContacts();
        configureRecyclerView(contacts);
        //set variables in Binding
        return fragmentContactsBinding.getRoot();

    }

    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        selectionTracker.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        super.onCreateOptionsMenu(menu,inflater);
    }

    private ActionMode.Callback actionModeCallbacks = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_generic, menu);
            return true;
        }
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.action_send) {
                Toast toast = Toast.makeText(getActivity(), String.valueOf(selectionTracker.getSelection().size()), Toast.LENGTH_SHORT);
                toast.show();
                mode.finish();
                return true;
            }
            return false;
        }
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
        }
    };

    private void configureRecyclerView(ArrayList<Contact> contacts) {
        contactsRecyclerView = Objects.requireNonNull(getActivity()).findViewById(R.id.contactsRecyclerView);
        contactsAdapter = new ContactsAdapter(contacts);
        fragmentContactsBinding.contactsRecyclerView.setAdapter(contactsAdapter);
        selectionTracker = new SelectionTracker.Builder<>("my_selection", fragmentContactsBinding.contactsRecyclerView,
                new ContactsAdapter.KeyProvider(fragmentContactsBinding.contactsRecyclerView.getAdapter()),
                new ContactsAdapter.DetailsLookup(fragmentContactsBinding.contactsRecyclerView),
                StorageStrategy.createLongStorage()).withSelectionPredicate(new ContactsAdapter.Predicate()).build();
        contactsAdapter.setSelectionTracker(selectionTracker);
        selectionTracker.addObserver(new SelectionTracker.SelectionObserver() {
            @Override
            public void onSelectionChanged() {
                super.onSelectionChanged();
                if (selectionTracker.hasSelection() && actionMode == null) {
                    actionMode = Objects.requireNonNull(getActivity()).startActionMode(actionModeCallbacks);
                } else if (!selectionTracker.hasSelection() && actionMode != null) {
                    actionMode.finish();
                    actionMode = null;
                }
            }
        });
    }

    private ArrayList<Contact> getSelectedContacts() {
        Selection<Long> settingsSelection = selectionTracker.getSelection();
        Iterator<Long> settingSelectionIterator = settingsSelection.iterator();
        ArrayList<Contact> settingNamesSelected = new ArrayList<>();
        while (settingSelectionIterator.hasNext()) {
            Long settingSelectionId = settingSelectionIterator.next();
            Contact contact = contacts.get(settingSelectionId.intValue());
            Log.i("CONTACT",contact.name);
        }
        return settingNamesSelected;
    }


    @Override
    public boolean onQueryTextSubmit(String text) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String text) {
        contactsAdapter.getFilter().filter(text);
        return true;
    }

}
