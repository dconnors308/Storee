package com.example.stohre.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.example.stohre.api.GenericPOSTResponse;
import com.example.stohre.objects.Story;
import com.example.stohre.objects.StoryEdit;
import com.example.stohre.objects.User;
import com.example.stohre.utilities.Utilities;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

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
    private MaterialButton okButton;
    private View fragmentView;

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
        okButton = fragmentView.findViewById(R.id.fragment_edit_story_intro_ok_button);
        introEditText.requestFocus();
        utilities.showKeyboard();
        okButton.setOnClickListener(this);
        return fragmentView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.fragment_edit_story_intro_ok_button:
                verifyInput();
            break;
        }
    }

    private void verifyInput() {
        storyIntro = introEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(storyIntro)) {
            story.setSTORY_TEXT(storyIntro);
            createFirstEdit(story);
        }
        else {
            introEditTextLayout.setError(getResources().getString(R.string.enter_an_intro));
        }
    }

    private void createFirstEdit(Story story) {
        StoryEdit storyEdit = new StoryEdit();
        storyEdit.setSTORY_ID(story.getSTORY_ID());
        storyEdit.setUSER_ID(user.getUSER_ID());
        storyEdit.setSTORY_TEXT(storyIntro);
        progressBar.setVisibility(View.VISIBLE);
        apiCalls = APIInstance.getRetrofitInstance().create(APICalls.class);
        Call<GenericPOSTResponse> call = apiCalls.createStoryEdit(storyEdit);
        call.enqueue(new Callback<GenericPOSTResponse>() {
            @Override
            public void onResponse(Call<GenericPOSTResponse> call, Response<GenericPOSTResponse> response) {
                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(fragmentView, "story has begun!", Snackbar.LENGTH_SHORT).show();
                    navigate();
                }
                else {
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(fragmentView, "unable to make first edit to story", Snackbar.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<GenericPOSTResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Snackbar.make(fragmentView, "failure" , Snackbar.LENGTH_SHORT).show();
                Log.d("call",call.toString());
                Log.d("throwable",t.toString());
            }
        });
    }

    private void navigate() {
        introEditText.clearFocus();
        Bundle storyBundle = new Bundle();
        storyBundle.putSerializable("Story", story);
        Navigation.findNavController(fragmentView).navigate(R.id.action_fragment_edit_story_intro_fragment_stories,storyBundle);
    }

}