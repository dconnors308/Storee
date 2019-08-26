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
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.selection.Selection;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;

import com.example.stohre.R;
import com.example.stohre.adapters.FriendsAdapter;
import com.example.stohre.api.APICalls;
import com.example.stohre.api.APIInstance;
import com.example.stohre.databinding.FragmentFriendsBinding;
import com.example.stohre.dialogs.SearchUsersDialog;
import com.example.stohre.objects.Member;
import com.example.stohre.objects.Members;
import com.example.stohre.objects.Story;
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

public class FriendsFragment extends Fragment implements View.OnClickListener {

    private APICalls apiCalls;
    private SharedPreferences sharedPreferences;
    private ProgressBar progressBar;
    private FragmentFriendsBinding fragmentFriendsBinding;
    private ActionMode actionMode;
    private FriendsAdapter friendsAdapter;
    private ArrayList<User> friends;
    private ArrayList<Member> members;
    private SelectionTracker<Long> selectionTracker;
    private User user;
    private Story story;
    private String mode = "UNDEFINED";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            story = (Story) getArguments().getSerializable("Story");
            if (!getArguments().getString("Mode").isEmpty()) {
                mode = getArguments().getString("Mode");
            }
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

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentFriendsBinding = FragmentFriendsBinding.inflate(inflater, container, false);
        fragmentFriendsBinding.fragmentFriendsSubmitButton.setOnClickListener(this);
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
                if (friends.isEmpty()) {
                    fragmentFriendsBinding.fragmentFriendsAddButton.show();
                }
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
        inflater.inflate(R.menu.menu_search_users, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search_users) {
            showSearchDialog();
            return true;
        }
        return(super.onOptionsItemSelected(item));
    }

    private ActionMode.Callback actionModeCallbacks = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_next, menu);
            return true;
        }
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.action_next) {
                processData();
                mode.finish();
                navigate();
                return true;
            }
            return false;
        }
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
        }
    };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fragment_friends_submit_button) {
            processData();
            navigate();
        }
    }

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
                        if (mode.equals("CREATE")) {
                            fragmentFriendsBinding.fragmentFriendsSubmitButton.setVisibility(View.VISIBLE);
                        }
                    } else if (!selectionTracker.hasSelection() && actionMode != null) {
                        if (mode.equals("CREATE")) {
                            fragmentFriendsBinding.fragmentFriendsSubmitButton.setVisibility(View.GONE);
                        }
                        actionMode.finish();
                        actionMode = null;
                    }
                }
            });
        }
        progressBar.setVisibility(View.GONE);
    }

    private void showSearchDialog() {
        SearchUsersDialog searchUsersDialog = new SearchUsersDialog(Objects.requireNonNull(getContext()),getView(),user,friendsAdapter,friends,progressBar);
        Objects.requireNonNull(searchUsersDialog.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        searchUsersDialog.show();
    }

    private void processData() {
        Selection<Long> selection = selectionTracker.getSelection();
        Iterator<Long> iterator = selection.iterator();
        ArrayList<User> selectedFriends = new ArrayList<>();
        while (iterator.hasNext()) {
            Long selectionId = iterator.next();
            User friend = friends.get(selectionId.intValue());
            selectedFriends.add(friend);
        }
        boolean membersAlreadyExists;
        ArrayList<Member> members = new ArrayList<>();
        int userCount = 1;
        for (User selectedFriend: selectedFriends) {
            if (this.members != null) {
                membersAlreadyExists = false;
                for (Member existingFriend: this.members) {
                    if (selectedFriend.getUSER_NAME().equals(existingFriend.getUSER_NAME())) {
                        membersAlreadyExists = true;
                    }
                }
                if (!membersAlreadyExists) {
                    Member newMember = new Member(story.getSTORY_ID(),selectedFriend.getUSER_ID());
                    newMember.setUSER_NAME(selectedFriend.getUSER_NAME());
                    newMember.setMODERATOR("0");
                    members.add(newMember);
                    userCount += 1;
                }
                else {
                    Snackbar.make(fragmentFriendsBinding.getRoot(), selectedFriend.getUSER_NAME() + " has already been added" , Snackbar.LENGTH_SHORT).show();
                }
            }
            else {
                Member newMember = new Member(story.getSTORY_ID(),selectedFriend.getUSER_ID());
                newMember.setUSER_NAME(selectedFriend.getUSER_NAME());
                newMember.setMODERATOR("0");
                members.add(newMember);
                userCount += 1;
            }
        }
        story.setUSER_COUNT(String.valueOf(userCount));
        story.setMEMBERS(members);
    }

    private void readExistingMembers(final String STORY_ID) {
        apiCalls = APIInstance.getRetrofitInstance().create(APICalls.class);
        Call<Members> call = apiCalls.readMemberByStoryId(STORY_ID);
        call.enqueue(new Callback<Members>() {
            @Override
            public void onResponse(Call<Members> call, Response<Members> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        members = response.body().getMembers();
                    }
                    for (Member existingFriend: members) {
                        Log.v("existing friend",existingFriend.getUSER_NAME());
                    }
                }
                else {
                    members = null;
                }
            }
            @Override
            public void onFailure(Call<Members> call, Throwable t) {
                Log.d("call",call.toString());
                Log.d("throwable",t.toString());
            }
        });
    }

    private void navigate() {
        Bundle storyBundle = new Bundle();
        storyBundle.putSerializable("Story", story);
        if (mode.equals("CREATE")) {
            storyBundle.putString("Mode","CREATE");
            Navigation.findNavController(fragmentFriendsBinding.getRoot()).navigate(R.id.action_fragment_friends_edit_story_to_fragment_members,storyBundle);
        }
        else if (mode.equals("UPDATE")) {
            Navigation.findNavController(fragmentFriendsBinding.getRoot()).navigateUp();
        }
    }


}