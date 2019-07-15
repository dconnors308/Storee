package com.example.stohre.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.Scene;
import androidx.transition.TransitionManager;

import com.example.stohre.R;
import com.example.stohre.adapters.MembersAdapter;
import com.example.stohre.api.AddUserToStoryRequest;
import com.example.stohre.api.CreateStoryRequest;
import com.example.stohre.api.GenericResponse;
import com.example.stohre.api.APICalls;
import com.example.stohre.api.ReadOneUserResponse;
import com.example.stohre.api.ReadStoryIdResponse;
import com.example.stohre.api.APIInstance;
import com.example.stohre.databinding.FragmentNewStoryBinding;
import com.example.stohre.objects.Story;
import com.example.stohre.objects.User;
import com.example.stohre.utilities.Utilities;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class NewStory extends Fragment implements View.OnClickListener {

    private APICalls apiCalls;
    private SharedPreferences sharedPreferences;
    private ProgressBar progressBar;
    private FragmentNewStoryBinding fragmentNewStoryBinding;
    private ActionMode actionMode;
    private EditText storyNameEditText, memberUsernameEditText, introEditText;
    private RecyclerView membersRecyclerView;
    private SelectionTracker<Long> selectionTracker;
    private MembersAdapter membersAdapter;
    private MaterialButton processButton;
    private FloatingActionButton addMemberButton;
    private ViewGroup rootScene;
    private Scene memberScene, introScene;
    private AutoTransition autoTransition;
    private String memberUsername, memberId, storyName, storyId, introText;
    private boolean storyCreated;
    private ArrayList<User> members;
    private User user;
    private Utilities utilities;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        utilities = new Utilities(getActivity());
        sharedPreferences = getActivity().getSharedPreferences("com.example.stohre", MODE_PRIVATE);
        if (!sharedPreferences.getString("user", "").isEmpty()) {
            Gson gson = new Gson();
            String json = sharedPreferences.getString("user", "");
            user = gson.fromJson(json, User.class);
        }
        if (savedInstanceState != null) {
            if (selectionTracker != null) {
                selectionTracker.onRestoreInstanceState(savedInstanceState);
            }
        }
        super.onCreate(savedInstanceState);
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (selectionTracker != null) {
            selectionTracker.onSaveInstanceState(outState);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentNewStoryBinding = FragmentNewStoryBinding.inflate(inflater, container, false);
        utilities.showKeyboard();
        progressBar = getActivity().findViewById(R.id.progress_bar_horizontal_activity_main);
        rootScene = fragmentNewStoryBinding.getRoot().findViewById(R.id.fragment_new_story_top_view);
        storyNameEditText = rootScene.findViewById(R.id.fragment_new_story_title_edit_text);
        storyNameEditText.requestFocus();
        memberUsernameEditText = rootScene.findViewById(R.id.fragment_new_story_add_member_edit_text);
        processButton = fragmentNewStoryBinding.fragmentNewStoryNextButton;
        processButton.setOnClickListener(this);
        memberScene = Scene.getSceneForLayout(rootScene, R.layout.fragment_new_story_members, getActivity());
        introScene = Scene.getSceneForLayout(rootScene, R.layout.fragment_new_story_intro, getActivity());
        autoTransition = new AutoTransition();

        return fragmentNewStoryBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.fragment_new_story_next_button:
                if (processButton.getText().toString().equals("Next")) {
                    if (storyCreated) {
                        if (members.isEmpty()) {
                            Snackbar.make(rootScene, "add a co-author" , Snackbar.LENGTH_SHORT).show();
                        }
                        else {
                            for (User member: members) {
                                addMemberToStoryGroup(member.getUSER_ID(), member.getUSER_NAME(), storyId, storyName);
                            }
                            transitionToIntroScene();
                        }
                    }
                    else {
                        storyName = storyNameEditText.getText().toString();
                        if (!TextUtils.isEmpty(storyName)) {
                            createNewStory(user.getUSER_ID(), storyName);
                        }
                        else {
                            TextInputLayout textInputLayout = rootScene.findViewById(R.id.new_story_title_edit_text_layout);
                            textInputLayout.setError("enter a title");
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
                        TextInputLayout textInputLayout = rootScene.findViewById(R.id.fragment_new_story_intro_edit_text_layout);
                        textInputLayout.setError("enter an intro");
                    }
                }
                break;
            case R.id.fragment_new_story_add_member_button:
                boolean duplicateUser = false;
                memberUsername = memberUsernameEditText.getText().toString().trim();
                if (members == null) {
                    verifyMemberUsername(memberUsername);
                }
                else {
                    for (User member: members) {
                        if (member.getUSER_NAME().equals(memberUsername)) {
                            duplicateUser = true;
                            Snackbar.make(rootScene, memberUsername + " already added" , Snackbar.LENGTH_SHORT).show();
                        }
                    }
                    if (!duplicateUser) {
                        if (memberUsername.equals(user.getUSER_NAME())) {
                            Snackbar.make(rootScene, "self already added" , Snackbar.LENGTH_SHORT).show();
                        }
                        else {
                            verifyMemberUsername(memberUsername);
                        }
                    }
                }
                break;
        }
    }

    private ActionMode.Callback actionModeCallbacks = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.action_mode_delete, menu);
            return true;
        }
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    Snackbar.make(rootScene, "delete clicked!" , Snackbar.LENGTH_SHORT).show();
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
        }
    };

    public void createNewStory(final String USER_ID, final String STORY_NAME) {
        CreateStoryRequest createStoryRequest = new CreateStoryRequest(USER_ID,STORY_NAME);
        apiCalls = APIInstance.getRetrofitInstance().create(APICalls.class);
        progressBar.setVisibility(View.VISIBLE);
        Call<GenericResponse> call = apiCalls.createStory(createStoryRequest);
        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if (response.isSuccessful()) {
                    storyName = STORY_NAME;
                    readyStoryId(USER_ID,STORY_NAME);
                }
                else {
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(rootScene, STORY_NAME + " not created:(" , Snackbar.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                Log.d("call",call.toString());
                Log.d("throwable",t.toString());
                progressBar.setVisibility(View.GONE);
                Snackbar.make(rootScene, "failure" , Snackbar.LENGTH_SHORT).show();
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
                    storyCreated = true;
                    storyId = response.body().getSTORY_ID();
                    transitionToMemberScene();
                    progressBar.setVisibility(View.GONE);
                }
                else {
                    Snackbar.make(rootScene, "failure" , Snackbar.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(Call<ReadStoryIdResponse> call, Throwable t) {
                Log.d("call",call.toString());
                Log.d("throwable",t.toString());
                progressBar.setVisibility(View.GONE);
                Snackbar.make(rootScene, "failure" , Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void transitionToMemberScene() {
        TransitionManager.go(memberScene, autoTransition);
        addMemberButton = rootScene.findViewById(R.id.fragment_new_story_add_member_button);
        addMemberButton.setOnClickListener(this);
        memberUsernameEditText = rootScene.findViewById(R.id.fragment_new_story_add_member_edit_text);
        memberUsernameEditText.requestFocus();
        //utilities.showKeyboard();
        membersRecyclerView = rootScene.findViewById(R.id.fragment_new_story_members_recycler_view);
        members = new ArrayList<>();
        membersAdapter = new MembersAdapter(members);
        membersRecyclerView.setAdapter(membersAdapter);
        selectionTracker = new SelectionTracker.Builder<>("members", membersRecyclerView,
                new MembersAdapter.KeyProvider(membersRecyclerView.getAdapter()),
                new MembersAdapter.DetailsLookup(membersRecyclerView),
                StorageStrategy.createLongStorage()).withSelectionPredicate(new MembersAdapter.Predicate()).build();
        membersAdapter.setSelectionTracker(selectionTracker);
        selectionTracker.addObserver(new SelectionTracker.SelectionObserver() {
            @Override
            public void onSelectionChanged() {
                super.onSelectionChanged();
                if (selectionTracker.hasSelection() && actionMode == null) {
                    actionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(actionModeCallbacks);
                } else if (!selectionTracker.hasSelection() && actionMode != null) {
                    actionMode.finish();
                    actionMode = null;
                }
            }
        });
    }

    private void transitionToIntroScene() {
        TransitionManager.go(introScene, autoTransition);
        introEditText = rootScene.findViewById(R.id.fragment_new_story_intro_edit_text);
        introEditText.requestFocus();
        utilities.showKeyboard();
        processButton.setText("Finish");
    }

    public void verifyMemberUsername(final String username) {
        apiCalls = APIInstance.getRetrofitInstance().create(APICalls.class);
        Call<ReadOneUserResponse> call = apiCalls.readOneUserByUsername(username);
        call.enqueue(new Callback<ReadOneUserResponse>() {
            @Override
            public void onResponse(Call<ReadOneUserResponse> call, Response<ReadOneUserResponse> response) {
                if (response.isSuccessful()) {
                    Snackbar.make(rootScene, memberUsername + " added!" , Snackbar.LENGTH_SHORT).show();
                    memberUsername = response.body().getUSER_NAME();
                    memberId = response.body().getUSER_ID();
                    User member = new User();
                    member.setUSER_NAME(memberUsername);
                    member.setUSER_ID(memberId);
                    members.add(member);
                    membersAdapter.notifyDataSetChanged();
                    memberUsernameEditText.setText("");
                    memberUsernameEditText.requestFocus();
                }
                else {
                    Snackbar.make(rootScene, memberUsername + " does not exist" , Snackbar.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ReadOneUserResponse> call, Throwable t) {
                Snackbar.make(rootScene, "failure" , Snackbar.LENGTH_SHORT).show();
                Log.d("call",call.toString());
                Log.d("throwable",t.toString());
            }
        });
    }

    public void addMemberToStoryGroup(final String USER_ID, final String USER_NAME, final String STORY_ID, final String STORY_NAME) {
        progressBar.setVisibility(View.VISIBLE);
        AddUserToStoryRequest addUserToStoryRequest = new AddUserToStoryRequest(USER_ID,STORY_ID);
        apiCalls = APIInstance.getRetrofitInstance().create(APICalls.class);
        Call<GenericResponse> call = apiCalls.addUserToStory(addUserToStoryRequest);
        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                }
                else {
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(rootScene, "unable to add " + USER_NAME + " to " + STORY_NAME , Snackbar.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Snackbar.make(rootScene, "failure" , Snackbar.LENGTH_SHORT).show();
                Log.d("call",call.toString());
                Log.d("throwable",t.toString());
            }
        });
    }

    public void updateStory(Story story) {
        progressBar.setVisibility(View.VISIBLE);
        apiCalls = APIInstance.getRetrofitInstance().create(APICalls.class);
        Call<GenericResponse> call = apiCalls.updateStory(story);
        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(rootScene, "story created!", Snackbar.LENGTH_SHORT).show();
                    Navigation.findNavController(getActivity(),R.id.main_content).navigateUp();
                }
                else {
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(rootScene, "unable to finalize story :(", Snackbar.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Snackbar.make(rootScene, "failure" , Snackbar.LENGTH_SHORT).show();
                Log.d("call",call.toString());
                Log.d("throwable",t.toString());
            }
        });
    }

}
