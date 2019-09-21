package com.example.stohre.fragments;

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
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.stohre.R;
import com.example.stohre.objects.Story;
import com.example.stohre.objects.User;
import com.example.stohre.utilities.Utilities;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class EditStoryTitleFragment extends Fragment implements View.OnClickListener{

    private Utilities utilities;
    private SharedPreferences sharedPreferences;
    private ProgressBar progressBar;
    private TextInputEditText titleEditText;
    private TextInputLayout titleEditTextLayout;
    private MaterialButton submitButton;
    private String storyName;
    private User user;
    private Story story;
    private View fragmentView;
    private String mode = "UNDEFINED";

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
        super.onCreate(savedInstanceState);
    }

    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_edit_story_title,container,false);
        titleEditText = fragmentView.findViewById(R.id.fragment_edit_story_action_edit_text);
        titleEditTextLayout = fragmentView.findViewById(R.id.fragment_edit_story_title_edit_text_layout);
        submitButton = fragmentView.findViewById(R.id.fragment_edit_story_submit_button);
        if (mode.equals("CREATE")) {
            submitButton.setText("NEXT");
        }
        else if (mode.equals("UPDATE")) {
            submitButton.setText("UPDATE");
        }
        submitButton.setOnClickListener(this);
        titleEditText.requestFocus();
        titleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0) {
                    submitButton.setVisibility(View.VISIBLE);
                }
                else {
                    submitButton.setVisibility(View.GONE);
                }
            }
        });
        if (story != null) {
            titleEditText.setText(story.getSTORY_NAME());
        }
        utilities.showKeyboard();
        return fragmentView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search_users, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_next) {
            processData();
            navigate();
            return(true);
        }
        return(super.onOptionsItemSelected(item));
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.fragment_edit_story_submit_button:
                processData();
                navigate();
            break;
        }
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
            Navigation.findNavController(fragmentView).navigate(R.id.action_fragment_edit_story_title_to_fragment_friends,storyBundle);
        }
        else {
            Navigation.findNavController(fragmentView).navigateUp();
        }
    }

}