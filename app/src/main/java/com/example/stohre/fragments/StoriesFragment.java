package com.example.stohre.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.selection.Selection;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;

import com.example.stohre.MainActivity;
import com.example.stohre.R;
import com.example.stohre.adapters.StoriesAdapter;
import com.example.stohre.api.APICalls;
import com.example.stohre.api.APIInstance;
import com.example.stohre.databinding.FragmentStoriesBinding;
import com.example.stohre.objects.Story;
import com.example.stohre.objects.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Iterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class StoriesFragment extends Fragment implements SearchView.OnQueryTextListener, View.OnClickListener {

    private APICalls service;
    private SharedPreferences sharedPreferences;
    private ProgressBar progressBar;
    private FragmentStoriesBinding fragmentStoriesBinding;
    private ActionMode actionMode;
    private SearchView searchView;
    private StoriesAdapter storiesAdapter;
    private ArrayList<Story> stories;
    private SelectionTracker<Long> selectionTracker;
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
        sharedPreferences = getActivity().getSharedPreferences("com.example.stohre", MODE_PRIVATE);
        progressBar = getActivity().findViewById(R.id.progress_bar_horizontal_activity_main);
        fragmentStoriesBinding = FragmentStoriesBinding.inflate(inflater, container, false);
        if (!sharedPreferences.getString("user", "").isEmpty()) {
            Gson gson = new Gson();
            String json = sharedPreferences.getString("user", "");
            user = gson.fromJson(json, User.class);
        }
        if (user != null) {
            progressBar.setVisibility(View.VISIBLE);
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
            inflater.inflate(R.menu.action_mode_add, menu);
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
                Log.v("RESPONSE_CODE", String.valueOf(response.code()));
                Log.v("BODY", String.valueOf(response.body()));
                if (response.isSuccessful()) {
                    stories = response.body().getStories();
                    configureRecyclerView(stories);
                    progressBar.setVisibility(View.GONE);
                }
                else {
                    displayEmptyListView();
                    progressBar.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(Call<com.example.stohre.objects.Stories> call, Throwable t) {
                Log.d("call",call.toString());
                Log.d("throwable",t.toString());
                Snackbar.make(fragmentStoriesBinding.getRoot(), "failure" , Snackbar.LENGTH_SHORT).show();
            }
        });
    }



    private void displayEmptyListView() {
        fragmentStoriesBinding.fragmentStoriesRecyclerView.setVisibility(View.GONE);
        fragmentStoriesBinding.fragmentStoriesRecyclerViewEmpty.setVisibility(View.VISIBLE);
        fragmentStoriesBinding.fragmentStoriesAddButton.setOnClickListener(this);
    }

    private void configureRecyclerView(ArrayList<Story> stories) {
        storiesAdapter = new StoriesAdapter(stories);
        fragmentStoriesBinding.fragmentStoriesRecyclerView.setAdapter(storiesAdapter);
        selectionTracker = new SelectionTracker.Builder<>("my_selection", fragmentStoriesBinding.fragmentStoriesRecyclerView,
                new StoriesAdapter.KeyProvider(fragmentStoriesBinding.fragmentStoriesRecyclerView.getAdapter()),
                new StoriesAdapter.DetailsLookup(fragmentStoriesBinding.fragmentStoriesRecyclerView),
                StorageStrategy.createLongStorage()).withSelectionPredicate(new StoriesAdapter.Predicate()).build();
        storiesAdapter.setSelectionTracker(selectionTracker);
        selectionTracker.addObserver(new SelectionTracker.SelectionObserver() {
            @Override
            public void onSelectionChanged() {
                super.onSelectionChanged();
                if (selectionTracker.hasSelection() && actionMode == null) {
                    actionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(actionModeCallbacks);
                } else if (!selectionTracker.hasSelection() && actionMode != null) {
                    actionMode.finish();
                    actionMode = null;
                }
            }
        });
        progressBar.setVisibility(View.GONE);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_stories_add_button:
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.navController.navigate(R.id.new_story);
                break;
        }
    }

}
