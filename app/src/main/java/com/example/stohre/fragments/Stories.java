package com.example.stohre.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.selection.Selection;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;

import com.example.stohre.MainActivity;
import com.example.stohre.R;
import com.example.stohre.adapters.StoriesAdapter;
import com.example.stohre.api.GetDataService;
import com.example.stohre.api.RetrofitClientInstance;
import com.example.stohre.databinding.FragmentStoriesBinding;
import com.example.stohre.objects.Story;
import com.example.stohre.objects.User;

import java.util.ArrayList;
import java.util.Iterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Stories extends Fragment implements SearchView.OnQueryTextListener {

    private static final String USER_ARG_KEY = "USER_ARG_KEY";
    private RecyclerView storiesRecyclerView;
    private SearchView searchView;
    private StoriesAdapter storiesAdapter;
    private ArrayList<Story> stories;
    private FragmentStoriesBinding fragmentStoriesBinding;
    private SelectionTracker<Long> selectionTracker;
    private ActionMode actionMode;
    private User user;
    public ProgressDialog progressDialog;
    public GetDataService service;

    public static Stories newInstance(User user) {
        Stories storiesFragment = new Stories();
        Bundle bundle = new Bundle();
        bundle.putSerializable(USER_ARG_KEY, user);
        storiesFragment.setArguments(bundle);

        return storiesFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            selectionTracker.onRestoreInstanceState(savedInstanceState);
        }
        setHasOptionsMenu(true);
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        user = (User) getArguments().getSerializable(USER_ARG_KEY);
        fragmentStoriesBinding = FragmentStoriesBinding.inflate(inflater, container, false);
        if (user != null) {
            readStoriesByUserId(user);
        }
        FloatingActionButton fab = fragmentStoriesBinding.getRoot().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).openCreateStoryFragment(user);
                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        return fragmentStoriesBinding.getRoot();

    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        selectionTracker.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.contact_menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        super.onCreateOptionsMenu(menu,inflater);
    }

    private ActionMode.Callback actionModeCallbacks = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.contact_menu_add_to_group, menu);
            return true;
        }
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_send:
                    Toast toast=Toast.makeText(getActivity(),String.valueOf(selectionTracker.getSelection().size()),Toast.LENGTH_SHORT);
                    toast.show();
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
        }
    };

    public void readStoriesByUserId(User user) {
        service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<com.example.stohre.objects.Stories> call = service.readStoriesByUserId(user.getUSER_ID());
        call.enqueue(new Callback<com.example.stohre.objects.Stories>() {
            @Override
            public void onResponse(Call<com.example.stohre.objects.Stories> call, Response<com.example.stohre.objects.Stories> response) {
                Log.v("READ ALL STORIES BY USER ID", "SUCCESSFUL");
                Log.v("RESPONSE_CODE", String.valueOf(response.code()));
                Log.v("BODY", String.valueOf(response.body()));
                if (response.body() != null) {
                    stories = response.body().getStories();
                    configureRecyclerView(stories);
                    for (Story story: stories) {
                        Log.v("RESPONSE_BODY", "response:" + story.getSTORY_NAME());
                    }
                }
            }
            @Override
            public void onFailure(Call<com.example.stohre.objects.Stories> call, Throwable t) {
                Log.d("call",call.toString());
                Log.d("throwable",t.toString());
                Toast.makeText(getActivity(), "READ ALL STORIES API FAILURE", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configureRecyclerView(ArrayList<Story> stories) {
        storiesRecyclerView = getActivity().findViewById(R.id.contactsRecyclerView);
        storiesAdapter = new StoriesAdapter(stories);
        fragmentStoriesBinding.storiesRecyclerView.setAdapter(storiesAdapter);
        selectionTracker = new SelectionTracker.Builder<>("my_selection", fragmentStoriesBinding.storiesRecyclerView,
                new StoriesAdapter.KeyProvider(fragmentStoriesBinding.storiesRecyclerView.getAdapter()),
                new StoriesAdapter.DetailsLookup(fragmentStoriesBinding.storiesRecyclerView),
                StorageStrategy.createLongStorage()).withSelectionPredicate(new StoriesAdapter.Predicate()).build();
        storiesAdapter.setSelectionTracker(selectionTracker);
        selectionTracker.addObserver(new SelectionTracker.SelectionObserver() {
            @Override
            public void onSelectionChanged() {
                super.onSelectionChanged();
                if (selectionTracker.hasSelection() && actionMode == null) {
                    actionMode = getActivity().startActionMode(actionModeCallbacks);
                } else if (!selectionTracker.hasSelection() && actionMode != null) {
                    actionMode.finish();
                    actionMode = null;
                }
            }
        });
    }

    private ArrayList<Story> getSelectedStories() {
        Selection<Long> settingsSelection = selectionTracker.getSelection();
        Iterator<Long> settingSelectionIterator = settingsSelection.iterator();
        ArrayList<Story> settingNamesSelected = new ArrayList<>();
        while (settingSelectionIterator.hasNext()) {
            Long settingSelectionId = settingSelectionIterator.next();
            Story story = stories.get(settingSelectionId.intValue());
            Log.i("STORY",story.STORY_NAME);
        }
        return settingNamesSelected;
    }

    @Override
    public boolean onQueryTextSubmit(String text) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String text) {
        storiesAdapter.getFilter().filter(text);
        return true;
    }

}
