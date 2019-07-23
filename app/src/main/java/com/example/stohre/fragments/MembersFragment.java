package com.example.stohre.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stohre.R;
import com.example.stohre.adapters.MembersAdapter;
import com.example.stohre.api.APICalls;
import com.example.stohre.api.APIInstance;
import com.example.stohre.api.GenericPOSTResponse;
import com.example.stohre.objects.Story;
import com.example.stohre.objects.StoryGroup;
import com.example.stohre.objects.User;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class MembersFragment extends Fragment implements View.OnClickListener {

    private APICalls apiCalls;
    private SharedPreferences sharedPreferences;
    private ProgressBar progressBar;
    private RecyclerView membersRecyclerView;
    private MaterialButton okButton;
    private MembersAdapter membersAdapter;
    private ArrayList<User> members;
    private User user;
    private Story story;
    private View fragmentView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            story = (Story) getArguments().getSerializable("Story");
            if (story != null) {
                members = story.getMEMBERS();
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
        }
        else {
            if (getArguments() != null) {
                story = (Story) getArguments().getSerializable("Story");
            }
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_members,container,false);
        membersRecyclerView = fragmentView.findViewById(R.id.fragment_members_recycler_view);
        okButton = fragmentView.findViewById(R.id.fragment_members_ok_button);
        okButton.setOnClickListener(this);
        if (!sharedPreferences.getString("user", "").isEmpty()) {
            Gson gson = new Gson();
            String json = sharedPreferences.getString("user", "");
            user = gson.fromJson(json, User.class);
        }
        if (user != null) {
            progressBar.setVisibility(View.VISIBLE);
            if (members != null) {
                configureRecyclerView();
            }
            progressBar.setVisibility(View.GONE);
        }
        return fragmentView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fragment_members_ok_button) {
            progressBar.setVisibility(View.VISIBLE);
            int editingOrderNumber = 2;
            for (User member: members) {
                StoryGroup storyGroup = new StoryGroup(story.getSTORY_ID(),member.getUSER_ID());
                storyGroup.setEDITING_ORDER(String.valueOf(editingOrderNumber));
                Log.v("STORY ID",story.getSTORY_ID());
                Log.v("MEMBER USER ID",member.getUSER_ID());
                Log.v("ACTION NUMBER",storyGroup.getEDITING_ORDER());
                updateEditingOrder(storyGroup);
                editingOrderNumber += 1;
            }
            progressBar.setVisibility(View.GONE);
            navigate();
        }
    }

    private void configureRecyclerView() {
        membersAdapter = new MembersAdapter(members);
        membersRecyclerView.setAdapter(membersAdapter);
        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.fall_down_animation);
        membersRecyclerView.setLayoutAnimation(animationController);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(RecyclerView.VERTICAL);
        membersRecyclerView.setLayoutManager(llm);
        ItemTouchHelper.Callback itemTouchHelperCallback = new ItemTouchHelper.Callback() {
            public boolean onMove(@NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Collections.swap(members, viewHolder.getAdapterPosition(), target.getAdapterPosition());
                membersAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                //TODO
            }
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, ItemTouchHelper.DOWN | ItemTouchHelper.UP);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(membersRecyclerView);
    }

    private void navigate() {
        Bundle storyBundle = new Bundle();
        storyBundle.putSerializable("Story", story);
        Navigation.findNavController(fragmentView).navigate(R.id.action_fragment_members_to_fragment_edit_story_intro,storyBundle);
    }

    private void updateEditingOrder(StoryGroup storyGroup) {
        apiCalls = APIInstance.getRetrofitInstance().create(APICalls.class);
        Call<GenericPOSTResponse> call = apiCalls.updateMemberEditingOrder(storyGroup);
        call.enqueue(new Callback<GenericPOSTResponse>() {
            @Override
            public void onResponse(Call<GenericPOSTResponse> call, Response<GenericPOSTResponse> response) { }
            @Override
            public void onFailure(Call<GenericPOSTResponse> call, Throwable t) {
                Log.d("call",call.toString());
                Log.d("throwable",t.toString());
            }
        });
    }

}
