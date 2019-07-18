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
import com.example.stohre.adapters.FriendsAdapter;
import com.example.stohre.adapters.StoriesAdapter;
import com.example.stohre.api.APICalls;
import com.example.stohre.api.APIInstance;
import com.example.stohre.databinding.FragmentFriendsBinding;
import com.example.stohre.objects.User;
import com.example.stohre.objects.Users;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Iterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class FriendsFragment extends Fragment implements SearchView.OnQueryTextListener, View.OnClickListener {

    private APICalls service;
    private SharedPreferences sharedPreferences;
    private ProgressBar progressBar;
    private FragmentFriendsBinding fragmentFriendsBinding;
    private ActionMode actionMode;
    private SearchView searchView;
    private FriendsAdapter friendsAdapter;
    private ArrayList<User> friends;
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
        fragmentFriendsBinding = FragmentFriendsBinding.inflate(inflater, container, false);
        if (!sharedPreferences.getString("user", "").isEmpty()) {
            Gson gson = new Gson();
            String json = sharedPreferences.getString("user", "");
            user = gson.fromJson(json, User.class);
        }
        if (user != null) {
            progressBar.setVisibility(View.VISIBLE);
            readFriendsByUserId(user);
        }
        return fragmentFriendsBinding.getRoot();
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

    public void readFriendsByUserId(User user) {
        service = APIInstance.getRetrofitInstance().create(APICalls.class);
        Call<Users> call = service.readFriendsByUserId(user.getUSER_ID());
        call.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                Log.v("RESPONSE_CODE", String.valueOf(response.code()));
                Log.v("BODY", String.valueOf(response.body()));
                if (response.isSuccessful()) {
                    friends = response.body().getUsers();
                    for (User friend: friends) {
                        Log.v("BODY", friend.getUSER_NAME());
                    }
                    configureRecyclerView(friends);
                    progressBar.setVisibility(View.GONE);
                }
                else {
                    displayEmptyListView();
                    progressBar.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                Log.d("call",call.toString());
                Log.d("throwable",t.toString());
                Snackbar.make(fragmentFriendsBinding.getRoot(), "failure" , Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void displayEmptyListView() {
        fragmentFriendsBinding.fragmentFriendsRecyclerView.setVisibility(View.GONE);
        fragmentFriendsBinding.fragmentFriendsRecyclerViewEmpty.setVisibility(View.VISIBLE);
        fragmentFriendsBinding.fragmentFriendsAddButton.setOnClickListener(this);
    }

    private void configureRecyclerView(ArrayList<User> friends) {
        friendsAdapter = new FriendsAdapter(friends);
        fragmentFriendsBinding.fragmentFriendsRecyclerView.setAdapter(friendsAdapter);
        selectionTracker = new SelectionTracker.Builder<>("my_selection", fragmentFriendsBinding.fragmentFriendsRecyclerView,
                new FriendsAdapter.KeyProvider(fragmentFriendsBinding.fragmentFriendsRecyclerView.getAdapter()),
                new FriendsAdapter.DetailsLookup(fragmentFriendsBinding.fragmentFriendsRecyclerView),
                StorageStrategy.createLongStorage()).withSelectionPredicate(new FriendsAdapter.Predicate()).build();
        friendsAdapter.setSelectionTracker(selectionTracker);
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
        friendsAdapter.getFilter().filter(text);
        return true;
    }

    private ArrayList<User> getSelectedFriends() {
        Selection<Long> selection = selectionTracker.getSelection();
        Iterator<Long> iterator = selection.iterator();
        ArrayList<User> selectedFriends = new ArrayList<>();
        while (iterator.hasNext()) {
            Long selectionId = iterator.next();
            User friend = friends.get(selectionId.intValue());
            Log.i("FRIEND",friend.getUSER_NAME());
        }
        return selectedFriends;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_friends_add_button:
                //MainActivity mainActivity = (MainActivity) getActivity();
                //mainActivity.navController.navigate(R.id.new_story);
                break;
        }
    }

}