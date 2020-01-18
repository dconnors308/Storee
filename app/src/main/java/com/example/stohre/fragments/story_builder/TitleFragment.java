package com.example.stohre.fragments.story_builder;

import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.daasuu.ei.Ease;
import com.daasuu.ei.EasingInterpolator;
import com.example.stohre.R;
import com.example.stohre.objects.Story;
import com.example.stohre.objects.User;
import com.example.stohre.utilities.Utilities;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class TitleFragment extends Fragment {

    private Utilities utilities;
    private SharedPreferences sharedPreferences;
    private ProgressBar progressBar;
    private TextInputEditText titleEditText;
    private TextInputLayout titleEditTextLayout;
    private MenuItem menuItemNextButton;
    private String storyName;
    private User user;
    private Story story;
    private View fragmentView;
    private String mode = "UNDEFINED";
    private Menu menu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        utilities = new Utilities(getActivity());
        sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("com.example.stohre", MODE_PRIVATE);
        progressBar = Objects.requireNonNull(getActivity()).findViewById(R.id.progress_bar_horizontal_activity_main);
        if (!sharedPreferences.getString("user", "").isEmpty()) {
            Gson gson = new Gson();
            String json = sharedPreferences.getString("user", "");
            user = gson.fromJson(json, User.class);
        }
        if (getArguments() != null) {
            story = (Story) getArguments().getSerializable("Story");
            if (!getArguments().getString("Mode").isEmpty()) {
                mode = getArguments().getString("Mode");
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        this.menu = menu;
        inflater.inflate(R.menu.menu_new_story, menu);
        menuItemNextButton = menu.findItem(R.id.action_next);
        configureMenu(menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_title,container,false);
        titleEditText = fragmentView.findViewById(R.id.fragment_edit_story_action_edit_text);
        titleEditTextLayout = fragmentView.findViewById(R.id.fragment_edit_story_title_edit_text_layout);
        titleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(!titleEditText.getText().toString().equals("")) {
                    menuItemNextButton.setVisible(true);
                }
                else {
                    menuItemNextButton.setVisible(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });
        if (story != null) {
            if (story.getSTORY_NAME() != null) {
                titleEditText.setText(story.getSTORY_NAME());
                titleEditText.setSelection(titleEditText.getText().length());
            }
        }
        titleEditText.requestFocus();
        utilities.showKeyboard();
        return fragmentView;
    }

    private void configureMenu(Menu menu) {
        if(!titleEditText.getText().toString().equals("")) {
            menuItemNextButton.setVisible(true);
        }
        Button nextButton;
        nextButton = (Button) menu.findItem(R.id.action_next).getActionView();
        nextButton.setTextSize(20);
        nextButton.setTextColor(getResources().getColor(R.color.primaryTextColor));
        nextButton.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        if (mode != null) {
            if (mode.equals("CREATE")) {
                nextButton.setText("NEXT");
            }
            else if (mode.equals("UPDATE")) {
                nextButton.setText("SAVE");
            }
        }
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

    private void processData() {
        storyName = titleEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(storyName)) {
            if (story == null) {
                story = new Story(storyName);
            }
            else {
                story.setSTORY_NAME(storyName);
            }
        }
        else {
            titleEditTextLayout.setError(getResources().getString(R.string.enter_a_title));
        }
    }

    private void navigate() {
        titleEditText.clearFocus();
        if (mode.equals("CREATE")) {
            Bundle storyBundle = new Bundle();
            storyBundle.putString("Mode","CREATE");
            storyBundle.putSerializable("Story", story);
            Navigation.findNavController(fragmentView).navigate(R.id.action_fragment_title_to_fragment_friends,storyBundle);
        }
        else if (mode.equals("UPDATE")) {
            Navigation.findNavController(fragmentView).navigateUp();
        }
    }

}