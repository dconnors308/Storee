package com.example.stohre.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.stohre.R;
import com.example.stohre.api.APICalls;
import com.example.stohre.api.APIInstance;
import com.example.stohre.objects.Member;
import com.example.stohre.objects.Story;
import com.example.stohre.objects.StoryEdit;
import com.example.stohre.objects.User;
import com.example.stohre.utilities.Utilities;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class EditStoryIntroFragment extends Fragment implements View.OnClickListener {

    private Utilities utilities;
    private APICalls apiCalls;
    private SharedPreferences sharedPreferences;
    private ProgressBar progressBar;
    private User user;
    private Story story;
    private String storyIntro;
    private TextInputEditText introEditText;
    private TextInputLayout introEditTextLayout;
    private MaterialButton submitButton;
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
        fragmentView = inflater.inflate(R.layout.fragment_edit_story_intro,container,false);
        introEditText = fragmentView.findViewById(R.id.fragment_edit_story_intro_edit_text);
        introEditTextLayout = fragmentView.findViewById(R.id.fragment_edit_story_intro_edit_text_layout);
        submitButton = fragmentView.findViewById(R.id.fragment_edit_story_intro_submit_button);
        introEditText.requestFocus();
        introEditText.addTextChangedListener(new TextWatcher() {
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
        if (mode != null) {
            if (mode.equals("CREATE")) {
                submitButton.setText("CREATE STORY");
            }
            else {
                submitButton.setText("UPDATE");
            }
        }
        submitButton.setOnClickListener(this);
        utilities.showKeyboard();
        return fragmentView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.fragment_edit_story_intro_submit_button:
                processData();
                navigate();
            break;
        }
    }

    private void processData() {
        storyIntro = introEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(storyIntro)) {
            ArrayList<StoryEdit> storyEdits = new ArrayList<>();
            StoryEdit storyEdit = new StoryEdit(story.getSTORY_ID(), user.getUSER_NAME(), storyIntro);
            storyEdits.add(storyEdit);
            story.setEDITS(storyEdits);
            Gson gson = new Gson();
            String json = gson.toJson(story);
            Log.i("story",json);
            createStory();
        }
        else {
            introEditTextLayout.setError(getResources().getString(R.string.enter_an_intro));
        }
    }

    private void navigate() {
        introEditText.clearFocus();
        Navigation.findNavController(getActivity().findViewById(R.id.main_content)).navigate(R.id.fragment_stories);
    }

    private void createStory() {
        progressBar.setVisibility(View.VISIBLE);
        apiCalls = APIInstance.getRetrofitInstance().create(APICalls.class);
        Call<Story> call = apiCalls.createStory(story);
        call.enqueue(new Callback<Story>() {
            @Override
            public void onResponse(Call<Story> call, Response<Story> response) {
                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    String activeEditorUsername = "";
                    for (Member member: story.getMEMBERS()) {
                        if (member.getEDITING_ORDER().equals("2")) {
                            activeEditorUsername = member.getUSER_NAME();
                        }
                    }
                    Snackbar.make(getActivity().findViewById(R.id.main_content), story.getSTORY_NAME() + " has begun! " + activeEditorUsername + " is up next...", Snackbar.LENGTH_SHORT).show();
                    Log.d("response",response.toString());
                    navigate();
                }
                else {
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(getActivity().findViewById(R.id.main_content), "unable to make first edit to story", Snackbar.LENGTH_SHORT).show();
                    Log.d("response",response.toString());
                }
            }
            @Override
            public void onFailure(Call<Story> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Snackbar.make(getActivity().findViewById(R.id.main_content), "failure" , Snackbar.LENGTH_SHORT).show();
                Log.d("call",call.toString());
                Log.d("throwable",t.toString());
            }
        });
    }

}