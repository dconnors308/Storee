package com.example.stohre.fragments;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.transition.AutoTransition;
import androidx.transition.Scene;
import androidx.transition.TransitionManager;

import com.example.stohre.MainActivity;
import com.example.stohre.R;
import com.example.stohre.api.AddUserToStoryRequest;
import com.example.stohre.api.CreateStoryRequest;
import com.example.stohre.api.GenericResponse;
import com.example.stohre.api.APICalls;
import com.example.stohre.api.ReadOneUserResponse;
import com.example.stohre.api.ReadStoryIdResponse;
import com.example.stohre.api.APIInstance;
import com.example.stohre.objects.Story;
import com.example.stohre.objects.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class NewStory extends Fragment implements View.OnClickListener {

    private APICalls apiCalls;
    private SharedPreferences sharedPreferences;
    private EditText storyNameEditText, memberUsernameEditText, introEditText;
    private MaterialButton processButton;
    private FloatingActionButton addMemberButton;
    private ViewGroup rootScene;
    private Scene memberScene, introScene;
    private AutoTransition autoTransition;
    private ProgressDialog progressDialog;
    private String memberUsername, memberId, storyName, storyId, introText;
    private boolean memberUsernameIsValid, storyCreated;
    private User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getActivity().getSharedPreferences("com.example.stohre", MODE_PRIVATE);
        if (!sharedPreferences.getString("user", "").isEmpty()) {
            Gson gson = new Gson();
            String json = sharedPreferences.getString("user", "");
            user = gson.fromJson(json, User.class);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_new_story, container, false);
        storyNameEditText = rootView.findViewById(R.id.new_story_name_edit_text);
        memberUsernameEditText = rootView.findViewById(R.id.new_story_member_edit_text);
        processButton = rootView.findViewById(R.id.new_story_next_button);
        processButton.setOnClickListener(this);
        progressDialog = new ProgressDialog(getActivity(),R.style.AppTheme_ProgressStyle);
        rootScene = rootView.findViewById(R.id.new_story_top_view);
        memberScene = Scene.getSceneForLayout(rootScene, R.layout.fragment_new_story_layout_members, getActivity());
        introScene = Scene.getSceneForLayout(rootScene, R.layout.fragment_new_story_layout_intro, getActivity());
        autoTransition = new AutoTransition();
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.new_story_next_button:
                if (processButton.getText().toString().equals("Next")) {
                    if (storyCreated) {
                        if (memberUsernameIsValid) {
                            addMemberToStoryGroup(memberId, memberUsername, storyId, storyName);
                        }
                    }
                    else {
                        storyName = storyNameEditText.getText().toString();
                        if (!TextUtils.isEmpty(storyName)) {
                            createNewStory(user.getUSER_ID(), storyName);
                        }
                        else {
                            Toast.makeText(getActivity(), "please enter a title", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else if(processButton.getText().toString().equals("Finish")) {
                    introText = introEditText.getText().toString();
                    if (!TextUtils.isEmpty(introText)) {
                        Story story = new Story(storyId,user.getUSER_ID(),storyName);
                        story.setSTORY_TEXT(introText);
                        updateStory(story);
                    }
                    else {
                        Toast.makeText(getActivity(), "please enter an intro", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    public void createNewStory(final String USER_ID, final String STORY_NAME) {
        CreateStoryRequest createStoryRequest = new CreateStoryRequest(USER_ID,STORY_NAME);
        apiCalls = APIInstance.getRetrofitInstance().create(APICalls.class);
        progressDialog.setMessage("creating story....");
        progressDialog.show();
        Call<GenericResponse> call = apiCalls.createStory(createStoryRequest);
        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if (response.isSuccessful()) {
                    storyName = STORY_NAME;
                    readyStoryId(USER_ID,STORY_NAME);
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), STORY_NAME + " not created:(", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                Log.d("call",call.toString());
                Log.d("throwable",t.toString());
                //memberUsernameIsValid = false;
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void readyStoryId(final String USER_ID, final String STORY_NAME) {
        apiCalls = APIInstance.getRetrofitInstance().create(APICalls.class);
        Call<ReadStoryIdResponse> call = apiCalls.readStoryId(USER_ID,STORY_NAME);
        call.enqueue(new Callback<ReadStoryIdResponse>() {
            @Override
            public void onResponse(Call<ReadStoryIdResponse> call, Response<ReadStoryIdResponse> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    storyCreated = true;
                    storyId = response.body().getSTORY_ID();
                    TransitionManager.go(memberScene, autoTransition);
                    addMemberButton = rootScene.findViewById(R.id.new_story_add_member_button);
                    memberUsernameEditText = rootScene.findViewById(R.id.new_story_member_edit_text);
                    addMemberButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            memberUsername = memberUsernameEditText.getText().toString();
                            verifyMemberUsername(memberUsername);
                        }
                    });
                }
                else {
                    Toast.makeText(getActivity(), "failure", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
            @Override
            public void onFailure(Call<ReadStoryIdResponse> call, Throwable t) {
                Log.d("call",call.toString());
                Log.d("throwable",t.toString());
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void verifyMemberUsername(String username) {
        apiCalls = APIInstance.getRetrofitInstance().create(APICalls.class);
        Call<ReadOneUserResponse> call = apiCalls.readOneUserByUsername(username);
        call.enqueue(new Callback<ReadOneUserResponse>() {
            @Override
            public void onResponse(Call<ReadOneUserResponse> call, Response<ReadOneUserResponse> response) {
                if (response.isSuccessful()) {
                    memberUsernameIsValid = true;
                    Toast.makeText(getActivity(), memberUsername + " found!", Toast.LENGTH_SHORT).show();
                    memberUsername = response.body().getUSER_NAME();
                    memberId = response.body().getUSER_ID();
                }
                else {
                    memberUsernameIsValid = false;
                    Toast.makeText(getActivity(), "invalid username :/", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ReadOneUserResponse> call, Throwable t) {
                memberUsernameIsValid = false;
                Toast.makeText(getActivity(), "failure", Toast.LENGTH_SHORT).show();
                Log.d("call",call.toString());
                Log.d("throwable",t.toString());
            }
        });
    }

    public void addMemberToStoryGroup(final String USER_ID, final String USER_NAME, final String STORY_ID, final String STORY_NAME) {
        progressDialog.setMessage("adding " + USER_NAME + " to story " + STORY_NAME + "...");
        progressDialog.show();
        AddUserToStoryRequest addUserToStoryRequest = new AddUserToStoryRequest(USER_ID,STORY_ID);
        apiCalls = APIInstance.getRetrofitInstance().create(APICalls.class);
        Call<GenericResponse> call = apiCalls.addUserToStory(addUserToStoryRequest);
        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    TransitionManager.go(introScene, autoTransition);
                    introEditText = rootScene.findViewById(R.id.new_story_intro_edit_text);
                    processButton.setText("Finish");
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "unable to add " + USER_NAME + " to " + STORY_NAME, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "failure", Toast.LENGTH_SHORT).show();
                Log.d("call",call.toString());
                Log.d("throwable",t.toString());
            }
        });
    }

    public void updateStory(Story story) {
        progressDialog.setMessage("finishing...");
        progressDialog.show();
        apiCalls = APIInstance.getRetrofitInstance().create(APICalls.class);
        Call<GenericResponse> call = apiCalls.updateStory(story);
        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "story created!", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(getActivity(),R.id.main_content).navigateUp();
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "unable to finalize story :(", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "failure", Toast.LENGTH_SHORT).show();
                Log.d("call",call.toString());
                Log.d("throwable",t.toString());
            }
        });
    }
}
