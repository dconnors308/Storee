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
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daasuu.ei.Ease;
import com.daasuu.ei.EasingInterpolator;
import com.example.stohre.R;
import com.example.stohre.adapters.EditingOrderAdapter;
import com.example.stohre.objects.Member;
import com.example.stohre.objects.Story;
import com.example.stohre.objects.User;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class EditingOrderFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private ProgressBar progressBar;
    private MenuItem menuItemNextButton;
    private RecyclerView editingOrderRecyclerView;
    private EditingOrderAdapter editingOrderAdapter;
    private ArrayList<Member> members;
    private User user;
    private Story story;
    private View fragmentView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("com.example.stohre", MODE_PRIVATE);
        progressBar = getActivity().findViewById(R.id.progress_bar_horizontal_activity_main);
        if (getArguments() != null) {
            story = (Story) getArguments().getSerializable("Story");
            if (story != null) {
                members = story.getMEMBERS();
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable("Story", story);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null) {
            story = (Story) savedInstanceState.getSerializable("Story");
        }
        else if (getArguments() != null) {
            story = (Story) getArguments().getSerializable("Story");
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_editing_order,container,false);
        editingOrderRecyclerView = fragmentView.findViewById(R.id.fragment_editing_order_recycler_view);
        if (!sharedPreferences.getString("user", "").isEmpty()) {
            Gson gson = new Gson();
            String json = sharedPreferences.getString("user", "");
            user = gson.fromJson(json, User.class);
        }
        if (user != null) {
            progressBar.setVisibility(View.VISIBLE);
            if (members != null) {
                processData();
                configureRecyclerView();
            }
            progressBar.setVisibility(View.GONE);
        }
        return fragmentView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_new_story, menu);
        menuItemNextButton = menu.findItem(R.id.action_next);
        menuItemNextButton.setVisible(true);
        configureMenu(menu);
        super.onCreateOptionsMenu(menu,inflater);
    }


    private void configureMenu(Menu menu) {
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


    private void processData() {
        int editingOrderNumber = 1;
        for (Member member: members) {
            member.setEDITING_ORDER(String.valueOf(editingOrderNumber));
            editingOrderNumber += 1;
        }
        boolean moderatorAlreadyAdded = false;
        for (Member member: members) { //add moderator if not already added
            if (member.getUSER_NAME().equals(user.getUSER_NAME())) {
                moderatorAlreadyAdded = true;
            }
        }
        if (!moderatorAlreadyAdded) {
            Member moderator = new Member(user.getUSER_NAME());
            moderator.setEDITING_ORDER("1");
            moderator.setMODERATOR("1");
            members.add(moderator);
        }
    }

    private void configureRecyclerView() {
        editingOrderAdapter = new EditingOrderAdapter(members);
        editingOrderRecyclerView.setAdapter(editingOrderAdapter);
        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.fall_down_animation);
        editingOrderRecyclerView.setLayoutAnimation(animationController);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(RecyclerView.VERTICAL);
        editingOrderRecyclerView.setLayoutManager(llm);
        ItemTouchHelper.Callback itemTouchHelperCallback = new ItemTouchHelper.Callback() {
            public boolean onMove(@NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                if (target.getAdapterPosition() != 0) {
                    Collections.swap(members, viewHolder.getAdapterPosition(), target.getAdapterPosition());
                    editingOrderAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                    editingOrderAdapter.updateEditingOrder((EditingOrderAdapter.ViewHolder) viewHolder,(EditingOrderAdapter.ViewHolder) target);
                }
                return true;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) { }
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                if (viewHolder.getAdapterPosition() != 0) {
                    return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, ItemTouchHelper.DOWN | ItemTouchHelper.UP);
                }
                return 0;
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(editingOrderRecyclerView);
    }

    private void navigate() {
        Bundle storyBundle = new Bundle();
        storyBundle.putSerializable("Story", story);
        storyBundle.putString("Mode", "CREATE");
        Navigation.findNavController(fragmentView).navigate(R.id.action_fragment_editing_order_to_fragment_intro,storyBundle);
    }

}
