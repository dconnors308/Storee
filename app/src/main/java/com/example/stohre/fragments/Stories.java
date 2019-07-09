package com.example.stohre.fragments;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.selection.Selection;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stohre.R;
import com.example.stohre.adapters.StoriesAdapter;
import com.example.stohre.api.APICalls;
import com.example.stohre.api.APIInstance;
import com.example.stohre.databinding.FragmentStoriesBinding;
import com.example.stohre.objects.Story;
import com.example.stohre.objects.User;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Iterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class Stories extends Fragment implements SearchView.OnQueryTextListener {

    private ProgressDialog progressDialog;
    private ProgressBar progressBar;
    private APICalls service;
    private SharedPreferences sharedPreferences;
    private RecyclerView storiesRecyclerView;
    private SearchView searchView;
    private StoriesAdapter storiesAdapter;
    private ArrayList<Story> stories;
    private FragmentStoriesBinding fragmentStoriesBinding;
    private SelectionTracker<Long> selectionTracker;
    private ActionMode actionMode;
    private User user;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            if (selectionTracker != null) {
                selectionTracker.onRestoreInstanceState(savedInstanceState);
            }
        }
        setHasOptionsMenu(true);
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //user = (User) getArguments().getSerializable(USER_ARG_KEY);
        sharedPreferences = getActivity().getSharedPreferences("com.example.Stohre", MODE_PRIVATE);
        progressDialog = new ProgressDialog(getActivity(),R.style.AppTheme_ProgressStyle);
        fragmentStoriesBinding = FragmentStoriesBinding.inflate(inflater, container, false);
        if (!sharedPreferences.getString("user", "").isEmpty()) {
            Gson gson = new Gson();
            String json = sharedPreferences.getString("user", "");
            user = gson.fromJson(json, User.class);
        }
        if (user != null) {
            progressDialog.setMessage("loading stories...");
            progressDialog.show();
            readStoriesByUserId(user);
        }
        return fragmentStoriesBinding.getRoot();
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (selectionTracker != null) {
            selectionTracker.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        super.onCreateOptionsMenu(menu,inflater);
    }

    private ActionMode.Callback actionModeCallbacks = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.search_menu_action_mode_add, menu);
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
        service = APIInstance.getRetrofitInstance().create(APICalls.class);
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
                    progressDialog.hide();
                }
                else {
                    progressDialog.hide();
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
        progressDialog.hide();
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

}
