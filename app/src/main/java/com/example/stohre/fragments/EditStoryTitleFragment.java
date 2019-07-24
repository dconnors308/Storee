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

public class EditStoryTitleFragment extends Fragment implements View.OnClickListener {

    private Utilities utilities;
    private APICalls apiCalls;
    private SharedPreferences sharedPreferences;
    private ProgressBar progressBar;
    private TextInputEditText titleEditText;
    private TextInputLayout titleEditTextLayout;
    private MaterialButton okButton;
    private String storyName;
    private User user;
    private Story story;
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
        super.onCreate(savedInstanceState);
    }

    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_edit_story_title,container,false);
        titleEditText = fragmentView.findViewById(R.id.fragment_edit_story_title_edit_text);
        titleEditTextLayout = fragmentView.findViewById(R.id.fragment_edit_story_title_edit_text_layout);
        okButton = fragmentView.findViewById(R.id.fragment_edit_story_title_ok_button);
        titleEditText.requestFocus();
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
            case R.id.fragment_edit_story_title_ok_button:
                verifyInput();
            break;
        }
    }

    private void verifyInput() {
        storyName =  titleEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(storyName)) {
            createNewStory(storyName);
        }
        else {
            titleEditTextLayout.setError(getResources().getString(R.string.enter_a_title));
        }
    }

    private void createNewStory(String storyName) {
        story = new Story(user.getUSER_ID(),storyName);
        story.setACTIVE_EDITOR_NUM("1");
        apiCalls = APIInstance.getRetrofitInstance().create(APICalls.class);
        progressBar.setVisibility(View.VISIBLE);
        Call<GenericPOSTResponse> call = apiCalls.createStory(story);
        call.enqueue(new Callback<GenericPOSTResponse>() {
            @Override
            public void onResponse(Call<GenericPOSTResponse> call, Response<GenericPOSTResponse> response) {
                if (response.isSuccessful()) {
                    readyStoryId(story.getUSER_ID(),story.getSTORY_NAME());
                }
                else {
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(fragmentView, story.getSTORY_NAME() + " not created:(" , Snackbar.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<GenericPOSTResponse> call, Throwable t) {
                Log.d("call",call.toString());
                Log.d("throwable",t.toString());
                progressBar.setVisibility(View.GONE);
                Snackbar.make(fragmentView, "failure" , Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void readyStoryId(final String USER_ID, final String STORY_NAME) {
        apiCalls = APIInstance.getRetrofitInstance().create(APICalls.class);
        Call<Story> call = apiCalls.readStoryId(USER_ID,STORY_NAME);
        call.enqueue(new Callback<Story>() {
            @Override
            public void onResponse(Call<Story> call, Response<Story> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        story.setSTORY_ID(response.body().getSTORY_ID());
                        navigate();
                    }
                }
                else {
                    Snackbar.make(fragmentView, "failure" , Snackbar.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(Call<Story> call, Throwable t) {
                Log.d("call",call.toString());
                Log.d("throwable",t.toString());
                progressBar.setVisibility(View.GONE);
                Snackbar.make(fragmentView, "failure" , Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void navigate() {
        titleEditText.clearFocus();
        Bundle storyBundle = new Bundle();
        storyBundle.putSerializable("Story", story);
        Navigation.findNavController(fragmentView).navigate(R.id.action_fragment_edit_story_title_to_fragment_friends,storyBundle);
    }

}