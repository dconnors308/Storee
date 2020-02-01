package com.example.stohre.fragments.story_builder;

import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.stohre.adapters.MembersAdapter;
import com.example.stohre.api.APICalls;
import com.example.stohre.api.APIInstance;
import com.example.stohre.databinding.FragmentMembersBinding;
import com.example.stohre.objects.Friend;
import com.example.stohre.objects.Friends;
import com.example.stohre.objects.Member;
import com.example.stohre.objects.Story;
import com.example.stohre.objects.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class MembersFragment extends Fragment {

    private APICalls apiCalls;
    private SharedPreferences sharedPreferences;
    private ProgressBar progressBar;
    private FragmentMembersBinding fragmentMembersBinding;
    private MenuItem menuItemNextButton;
    private MembersAdapter membersAdapter;
    private ArrayList<Friend> friends;
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
        fragmentMembersBinding = FragmentMembersBinding.inflate(inflater, container, false);
        if (!sharedPreferences.getString("user", "").isEmpty()) {
            Gson gson = new Gson();
            String json = sharedPreferences.getString("user", "");
            user = gson.fromJson(json, User.class);
        }
        return fragmentMembersBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_new_story, menu);
        menuItemNextButton = menu.findItem(R.id.action_next);
        configureMenu(menu);
        readFriends();
        super.onCreateOptionsMenu(menu,inflater);
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
        nextButton.setOnClickListener(v -> {
            processData();
            navigate();
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

    private void readFriends() {
        progressBar.setVisibility(View.VISIBLE);
        apiCalls = APIInstance.getRetrofitInstance().create(APICalls.class);
        Call<Friends> call = apiCalls.readFriends(user.getUSER_ID());
        call.enqueue(new Callback<Friends>() {
            @Override
            public void onResponse(Call<Friends> call, Response<Friends> response) {
                if (response.isSuccessful()) {
                    friends = response.body().getFriends();
                    configureRecyclerView(friends);
                }
                else {
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(getActivity().findViewById(R.id.main_content), "you have no friends :/", Snackbar.LENGTH_SHORT).show();
                    Log.d("response",response.toString());
                }
            }
            @Override
            public void onFailure(Call<Friends> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Snackbar.make(getActivity().findViewById(R.id.main_content), "failure" , Snackbar.LENGTH_SHORT).show();
                Log.d("call",call.toString());
                Log.d("throwable",t.toString());
            }
        });
    }

    private void configureRecyclerView(final ArrayList<Friend> friends) {
        membersAdapter = new MembersAdapter(friends,user);
        fragmentMembersBinding.fragmentMembersRecyclerView.setAdapter(membersAdapter);
        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.fall_down_animation);
        fragmentMembersBinding.fragmentMembersRecyclerView.setLayoutAnimation(animationController);
        selectionTracker = new SelectionTracker.Builder<>("my_selection", fragmentMembersBinding.fragmentMembersRecyclerView,
                new MembersAdapter.KeyProvider(fragmentMembersBinding.fragmentMembersRecyclerView.getAdapter()),
                new MembersAdapter.DetailsLookup(fragmentMembersBinding.fragmentMembersRecyclerView),
                StorageStrategy.createLongStorage()).withSelectionPredicate(new MembersAdapter.Predicate()).build();
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
                for (Friend friend:friends) {
                    for (Member member: story.getMEMBERS()) {
                        String username = "";
                        if (friend.getREQUESTER_USER_NAME().equals(user.getUSER_NAME())) {
                            username = friend.getACCEPTER_USER_NAME();
                        }
                        else if (friend.getACCEPTER_USER_NAME().equals(user.getUSER_NAME())) {
                            username = friend.getREQUESTER_USER_NAME();
                        }
                        if (username.equals(member.getUSER_NAME())) {
                            existingSelections.add(position);
                        }
                    }
                    position++;
                }
                selectionTracker.setItemsSelected(existingSelections, true);
            }
        }
        membersAdapter.setSelectionTracker(selectionTracker);
        progressBar.setVisibility(View.GONE);
    }

    private void processData() {
        Selection<Long> selection = selectionTracker.getSelection();
        Iterator<Long> iterator = selection.iterator();
        ArrayList<Friend> selectedFriends = new ArrayList<>();
        while (iterator.hasNext()) {
            Long selectionId = iterator.next();
            Friend friend = friends.get(selectionId.intValue());
            selectedFriends.add(friend);
        }
        ArrayList<Member> members = new ArrayList<>();
        Member moderator = new Member(user.getUSER_NAME());
        moderator.setMODERATOR("1");
        members.add(moderator);
        int userCount = 1;
        for (Friend selectedFriend: selectedFriends) {
            String username = "";
            if (selectedFriend.getREQUESTER_USER_NAME().equals(user.getUSER_NAME())) {
                username = selectedFriend.getACCEPTER_USER_NAME();
            }
            else if (selectedFriend.getACCEPTER_USER_NAME().equals(user.getUSER_NAME())) {
                username = selectedFriend.getREQUESTER_USER_NAME();
            }
            Member newMember = new Member(username);
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
        Navigation.findNavController(fragmentMembersBinding.getRoot()).navigate(R.id.action_fragment_friends_edit_story_to_fragment_editing_order,storyBundle);
    }


}