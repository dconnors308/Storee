package com.example.stohre.fragments.story_builder;

import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.selection.Selection;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;

import com.daasuu.ei.Ease;
import com.daasuu.ei.EasingInterpolator;
import com.example.stohre.R;
import com.example.stohre.adapters.FriendsAdapter;
import com.example.stohre.databinding.FragmentFriendsBinding;
import com.example.stohre.dialogs.SearchUsersDialog;
import com.example.stohre.objects.Member;
import com.example.stohre.objects.Story;
import com.example.stohre.objects.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class FriendsFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private ProgressBar progressBar;
    private FragmentFriendsBinding fragmentFriendsBinding;
    private MenuItem menuItemNextButton,menuItemSearchButton;
    private FriendsAdapter friendsAdapter;
    private ArrayList<User> friends;
    private SelectionTracker<Long> selectionTracker;
    private User user;
    private Story story;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("com.example.stohre", MODE_PRIVATE);
        progressBar = getActivity().findViewById(R.id.progress_bar_horizontal_activity_main);
        if (savedInstanceState == null) {
            if (getArguments() != null) {
                story = (Story) getArguments().getSerializable("Story");
            }
        }
        else {
            selectionTracker.onRestoreInstanceState(savedInstanceState);
        }
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        processData();
        savedInstanceState.putSerializable("Story", story);
        selectionTracker.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState!=null) {
            story = (Story) savedInstanceState.getSerializable("Story");
            if (selectionTracker != null) {
                selectionTracker.onRestoreInstanceState(savedInstanceState);
            }
        }
        else {
            if (getArguments() != null) {
                story = (Story) getArguments().getSerializable("Story");
            }
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentFriendsBinding = FragmentFriendsBinding.inflate(inflater, container, false);
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
        }
        return fragmentFriendsBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_new_story, menu);
        menuItemNextButton = menu.findItem(R.id.action_next);
        menuItemSearchButton = menu.findItem(R.id.action_search);
        menuItemSearchButton.setVisible(true);
        configureMenu(menu);
        configureRecyclerView(friends);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            showSearchDialog();
            return(true);
        }
        return(super.onOptionsItemSelected(item));
    }


    private void configureMenu(Menu menu) {
        if (story != null) {
            if (story.getMEMBERS() != null) {
                menuItemNextButton.setVisible(true);
            }
        }
        Button nextButton;
        nextButton = (Button) menu.findItem(R.id.action_next).getActionView();
        nextButton.setTextSize(20);
        nextButton.setTextColor(getResources().getColor(R.color.primaryTextColor));
        nextButton.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        nextButton.setText("NEXT");
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processData();
                navigate();
            }
        });
        doBounceAnimation(nextButton);
    }

    private void doBounceAnimation(View targetView) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(targetView, "translationX", 0, 25, 0);
        animator.setInterpolator(new EasingInterpolator(Ease.ELASTIC_IN_OUT));
        animator.setStartDelay(500);
        animator.setDuration(1500);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setRepeatMode(ObjectAnimator.REVERSE);
        animator.start();
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
        selectionTracker.addObserver(new SelectionTracker.SelectionObserver() {
    @Override
    public void onSelectionChanged() {
        super.onSelectionChanged();
        if (selectionTracker.hasSelection()) {
            menuItemNextButton.setVisible(true);
        }
        else if (!selectionTracker.hasSelection()) {
            menuItemNextButton.setVisible(false);
        }
            }
        });
        ArrayList<Long> existingSelections = new ArrayList<>();
        Long position = Long.valueOf(0);
        if (story != null) {
            if (story.getMEMBERS() != null) {
                for (User friend:friends) {
                    for (Member member: story.getMEMBERS()) {
                        if (friend.getUSER_NAME().equals(member.getUSER_NAME())) {
                            existingSelections.add(position);
                        }
                    }
                    position++;
                }
                selectionTracker.setItemsSelected(existingSelections, true);
            }
        }
        friendsAdapter.setSelectionTracker(selectionTracker);
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
        ArrayList<Member> members = new ArrayList<>();
        Member moderator = new Member(user.getUSER_NAME());
        moderator.setMODERATOR("1");
        members.add(moderator);
        int userCount = 1;
        for (User selectedFriend: selectedFriends) {
            Member newMember = new Member(selectedFriend.getUSER_NAME());
            newMember.setUSER_NAME(selectedFriend.getUSER_NAME());
            newMember.setMODERATOR("0");
            members.add(newMember);
            userCount++;
        }
        story.setUSER_COUNT(String.valueOf(userCount));
        story.setMEMBERS(members);
    }

    private void navigate() {
        Bundle storyBundle = new Bundle();
        storyBundle.putSerializable("Story", story);
        Navigation.findNavController(fragmentFriendsBinding.getRoot()).navigate(R.id.action_fragment_friends_edit_story_to_fragment_editing_order,storyBundle);
    }


}