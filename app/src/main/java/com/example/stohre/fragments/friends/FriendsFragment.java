package com.example.stohre.fragments.friends;

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
import androidx.fragment.app.Fragment;

import com.example.stohre.R;
import com.example.stohre.adapters.FriendsAdapter;
import com.example.stohre.api.APICalls;
import com.example.stohre.api.APIInstance;
import com.example.stohre.databinding.FragmentFriendsBinding;
import com.example.stohre.dialogs.FriendAcceptDialog;
import com.example.stohre.dialogs.FriendRequestDialog;
import com.example.stohre.objects.Friend;
import com.example.stohre.objects.Friends;
import com.example.stohre.objects.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class FriendsFragment extends Fragment {

    private APICalls apiCalls;
    private SharedPreferences sharedPreferences;
    private ProgressBar progressBar;
    private FragmentFriendsBinding fragmentFriendsBinding;
    private FriendsAdapter friendsAdapter;
    private ArrayList<Friend> friends;
    private User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        progressBar = getActivity().findViewById(R.id.progress_bar_horizontal_activity_main);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentFriendsBinding = FragmentFriendsBinding.inflate(inflater, container, false);
        sharedPreferences = getActivity().getSharedPreferences("com.example.stohre", MODE_PRIVATE);
        if (!sharedPreferences.getString("user", "").isEmpty()) {
            Gson gson = new Gson();
            String json = sharedPreferences.getString("user", "");
            user = gson.fromJson(json, User.class);
            if (user != null) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }
        readFriends();
        return fragmentFriendsBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_friends, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_send_friend_request) {
            showFriendRequestDialog();
            return(true);
        }
        return(super.onOptionsItemSelected(item));
    }

    private void configureRecyclerView() {
        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.fall_down_animation);
        friendsAdapter = new FriendsAdapter(friends,user, friend -> {
            //TODO on-click
        });
        fragmentFriendsBinding.fragmentFriendsRecyclerView.setAdapter(friendsAdapter);
        fragmentFriendsBinding.fragmentFriendsRecyclerView.setLayoutAnimation(animationController);
        progressBar.setVisibility(View.GONE);
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
                    for (Friend friend: friends) {
                        if (friend.getCONFIRMED().equals("0") && friend.getLIFECYCLE().equals("SENT")) {
                            showFriendAcceptDialog(friend);
                        }
                    }
                    configureRecyclerView();
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

    private void showFriendRequestDialog() {
        FriendRequestDialog friendRequestDialog = new FriendRequestDialog(Objects.requireNonNull(getContext()),getView(),user,friendsAdapter,friends,progressBar);
        Objects.requireNonNull(friendRequestDialog.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        friendRequestDialog.show();
    }

    private void showFriendAcceptDialog(Friend friend) {
        FriendAcceptDialog friendAcceptDialog = new FriendAcceptDialog(Objects.requireNonNull(getContext()),getView(),user,friend,progressBar);
        friendAcceptDialog.show();
    }

}