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
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.selection.Selection;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;

import com.example.stohre.R;
import com.example.stohre.adapters.FriendsAdapter;
import com.example.stohre.api.APICalls;
import com.example.stohre.api.APIInstance;
import com.example.stohre.api.GenericPOSTResponse;
import com.example.stohre.databinding.FragmentFriendsBinding;
import com.example.stohre.dialogs.SearchUsersDialog;
import com.example.stohre.objects.Story;
import com.example.stohre.objects.StoryGroup;
import com.example.stohre.objects.StoryGroups;
import com.example.stohre.objects.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class FriendsFragment extends Fragment implements SearchView.OnQueryTextListener, View.OnClickListener {

    private APICalls apiCalls;
    private SharedPreferences sharedPreferences;
    private ProgressBar progressBar;
    private FragmentFriendsBinding fragmentFriendsBinding;
    private ActionMode actionMode;
    private SearchView searchView;
    private FriendsAdapter friendsAdapter;
    private ArrayList<User> friends;
    private ArrayList<StoryGroup> existingFriends;
    private SelectionTracker<Long> selectionTracker;
    private User user;
    private Story story;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            story = (Story) getArguments().getSerializable("Story");
        }
        sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("com.example.stohre", MODE_PRIVATE);
        progressBar = getActivity().findViewById(R.id.progress_bar_horizontal_activity_main);
        setHasOptionsMenu(true);
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable("Story", story);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState!=null) {
            story = (Story) savedInstanceState.getSerializable("Story");
            if (story != null) {
                readExistingMembers(story.getSTORY_ID());
            }
            if (selectionTracker != null) {
                selectionTracker.onRestoreInstanceState(savedInstanceState);
            }
        }
        else {
            if (getArguments() != null) {
                story = (Story) getArguments().getSerializable("Story");
                if (story != null) {
                    readExistingMembers(story.getSTORY_ID());
                }
            }
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fragment_friends_add_button) {
            SearchUsersDialog searchUsersDialog = new SearchUsersDialog(Objects.requireNonNull(getContext()),getView(),user,friendsAdapter,friends,progressBar);
            Objects.requireNonNull(searchUsersDialog.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            searchUsersDialog.show();
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentFriendsBinding = FragmentFriendsBinding.inflate(inflater, container, false);
        fragmentFriendsBinding.fragmentFriendsAddButton.setOnClickListener(this);
        if (!sharedPreferences.getString("user", "").isEmpty()) {
            Gson gson = new Gson();
            String json = sharedPreferences.getString("user", "");
            user = gson.fromJson(json, User.class);
        }
        if (user != null) {
            progressBar.setVisibility(View.VISIBLE);
            if (!sharedPreferences.getString("friends", "").isEmpty()) {
                Gson gson = new Gson();
                String json = sharedPreferences.getString("friends", "");
                Type type = new TypeToken<ArrayList<User>>(){}.getType();
                friends = gson.fromJson(json, type);
            }
            else {
                friends = new ArrayList<>();
            }
            configureRecyclerView(friends);
        }
        return fragmentFriendsBinding.getRoot();
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
            inflater.inflate(R.menu.menu_add_friends, menu);
            return true;
        }
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.action_add_friends) {
                addFriendsToStory();
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

    private void configureRecyclerView(final ArrayList<User> friends) {
        friendsAdapter = new FriendsAdapter(friends);
        fragmentFriendsBinding.fragmentFriendsRecyclerView.setAdapter(friendsAdapter);
        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.fall_down_animation);
        fragmentFriendsBinding.fragmentFriendsRecyclerView.setLayoutAnimation(animationController);
        selectionTracker = new SelectionTracker.Builder<>("my_selection", fragmentFriendsBinding.fragmentFriendsRecyclerView,
                new FriendsAdapter.KeyProvider(fragmentFriendsBinding.fragmentFriendsRecyclerView.getAdapter()),
                new FriendsAdapter.DetailsLookup(fragmentFriendsBinding.fragmentFriendsRecyclerView),
                StorageStrategy.createLongStorage()).withSelectionPredicate(new FriendsAdapter.Predicate()).build();
        friendsAdapter.setSelectionTracker(selectionTracker);
        if (story != null) {
            selectionTracker.addObserver(new SelectionTracker.SelectionObserver() {
                @Override
                public void onSelectionChanged() {
                    super.onSelectionChanged();
                    if (selectionTracker.hasSelection() && actionMode == null) {
                        actionMode = ((AppCompatActivity) Objects.requireNonNull(getActivity())).startSupportActionMode(actionModeCallbacks);
                    } else if (!selectionTracker.hasSelection() && actionMode != null) {
                        actionMode.finish();
                        actionMode = null;
                    }
                }
            });
        }
        progressBar.setVisibility(View.GONE);
    }

    private void addFriendsToStory() {
        progressBar.setVisibility(View.VISIBLE);
        Selection<Long> selection = selectionTracker.getSelection();
        Iterator<Long> iterator = selection.iterator();
        ArrayList<User> selectedFriends = new ArrayList<>();
        while (iterator.hasNext()) {
            Long selectionId = iterator.next();
            User friend = friends.get(selectionId.intValue());
            selectedFriends.add(friend);
        }
        boolean friendAlreadyExists;
        ArrayList<User> members = new ArrayList<>();
        for (User selectedFriend: selectedFriends) {
            friendAlreadyExists = false;
            for (StoryGroup existingFriend:existingFriends) {
                if (selectedFriend.getUSER_NAME().equals(existingFriend.getUSER_NAME())) {
                    friendAlreadyExists = true;
                }
            }
            if (!friendAlreadyExists) {
                addMemberToStoryGroup(story.getSTORY_ID(),selectedFriend.getUSER_ID());
                members.add(selectedFriend);
            }
            else {
                Snackbar.make(fragmentFriendsBinding.getRoot(), selectedFriend.getUSER_NAME() + " has already been added" , Snackbar.LENGTH_SHORT).show();
            }
        }
        story.setMEMBERS(members);
        progressBar.setVisibility(View.GONE);
        navigate();
    }

    private void addMemberToStoryGroup(final String STORY_ID, final String USER_ID) {
        Log.v("add member to story group","called");
        StoryGroup storyGroup = new StoryGroup(STORY_ID,USER_ID);
        apiCalls = APIInstance.getRetrofitInstance().create(APICalls.class);
        Call<GenericPOSTResponse> call = apiCalls.addUserToStory(storyGroup);
        call.enqueue(new Callback<GenericPOSTResponse>() {
            @Override
            public void onResponse(Call<GenericPOSTResponse> call, Response<GenericPOSTResponse> response) {
            }
            @Override
            public void onFailure(Call<GenericPOSTResponse> call, Throwable t) {
                Snackbar.make(fragmentFriendsBinding.getRoot(), "failure" , Snackbar.LENGTH_SHORT).show();
                Log.d("call",call.toString());
                Log.d("throwable",t.toString());
            }
        });
    }

    private void readExistingMembers(final String STORY_ID) {
        apiCalls = APIInstance.getRetrofitInstance().create(APICalls.class);
        Call<StoryGroups> call = apiCalls.readStoryGroupByStoryId(STORY_ID);
        call.enqueue(new Callback<StoryGroups>() {
            @Override
            public void onResponse(Call<StoryGroups> call, Response<StoryGroups> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        existingFriends = response.body().getStoryGroups();
                    }
                    for (StoryGroup existingFriend:existingFriends) {
                        Log.v("existing friend",existingFriend.getUSER_NAME());
                    }
                }
                else {
                    existingFriends = null;
                }
            }
            @Override
            public void onFailure(Call<StoryGroups> call, Throwable t) {
                Snackbar.make(fragmentFriendsBinding.getRoot(), "failure" , Snackbar.LENGTH_SHORT).show();
                Log.d("call",call.toString());
                Log.d("throwable",t.toString());
            }
        });
    }

    private void navigate() {
        Bundle storyBundle = new Bundle();
        storyBundle.putSerializable("Story", story);
        Navigation.findNavController(fragmentFriendsBinding.getRoot()).navigate(R.id.action_fragment_friends_edit_story_to_fragment_members,storyBundle);
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

}