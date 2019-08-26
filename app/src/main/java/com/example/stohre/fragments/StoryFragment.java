package com.example.stohre.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.stohre.R;
import com.example.stohre.api.APICalls;
import com.example.stohre.api.APIInstance;
import com.example.stohre.api.GenericPOSTResponse;
import com.example.stohre.objects.Member;
import com.example.stohre.objects.Story;
import com.example.stohre.objects.StoryEdit;
import com.example.stohre.objects.User;
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

public class StoryFragment extends Fragment implements View.OnClickListener {

    private APICalls apiCalls;
    private SharedPreferences sharedPreferences;
    private ProgressBar progressBar;
    private User user;
    private Member activeEditor;
    private Story story;
    private AppCompatTextView storyTitleTextView;
    private AppCompatTextView storyTextTextView;
    private TextInputLayout addSentenceEditTextLayout;
    private TextInputEditText addSentenceEditText;
    private AppCompatTextView activeEditorTextView;
    private MaterialButton saveButton;
    private ArrayList<StoryEdit> storyEdits;
    private ArrayList<Member> storyMembers;
    private SpannableStringBuilder spannableStringBuilder;
    private boolean isActiveSession;
    private String activeEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            story = (Story) getArguments().getSerializable("Story");
        }
        sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("com.example.stohre", MODE_PRIVATE);
        if (!sharedPreferences.getString("user", "").isEmpty()) {
            Gson gson = new Gson();
            String json = sharedPreferences.getString("user", "");
            user = gson.fromJson(json, User.class);
        }
        if (getArguments() != null) {
            story = (Story) getArguments().getSerializable("Story");
        }
        isActiveSession = false;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_story,container,false);
        progressBar = Objects.requireNonNull(getActivity()).findViewById(R.id.progress_bar_horizontal_activity_main);
        storyTitleTextView = view.findViewById(R.id.fragment_edit_story_title_text_view);
        storyTextTextView = view.findViewById(R.id.fragment_edit_story_text_text_view);
        activeEditorTextView = view.findViewById(R.id.fragment_edit_story_active_editor_text_view);
        saveButton = view.findViewById(R.id.fragment_edit_story_submit_button);
        addSentenceEditText = view.findViewById(R.id.fragment_edit_story_add_sentence_edit_text);
        addSentenceEditTextLayout = view.findViewById(R.id.fragment_edit_story_add_sentence_text_input_layout);
        if (story != null) {
            storyTitleTextView.setText(story.getSTORY_NAME());
            if (story.getEDITS() != null) {
                storyEdits = story.getEDITS();
                spannableStringBuilder = new SpannableStringBuilder();
                for(int i = 0; i < storyEdits.size(); i++) {
                    StoryEdit storyEdit = storyEdits.get(i);
                    spannableStringBuilder.append(storyEdit.getSTORY_TEXT());
                    if((i + 1 < storyEdits.size())) {
                        spannableStringBuilder.append("  ");
                    }
                }
                storyTextTextView.setText(spannableStringBuilder);
            }
            if (story.getMEMBERS() != null) {
                storyMembers = story.getMEMBERS();
                for (Member member: storyMembers) {
                    if (member.getUSER_ID().equals(user.getUSER_ID())) {
                        if (story.getACTIVE_EDITOR_NUM().equals(member.getEDITING_ORDER())) {
                            isActiveSession = true;
                        }
                    }
                    if (story.getACTIVE_EDITOR_NUM().equals(member.getEDITING_ORDER())) {
                        activeEditor = member;
                    }
                }
                if (isActiveSession) {
                    activeEditorTextView.setText("You're up!");
                    addSentenceEditTextLayout.setVisibility(View.VISIBLE);
                    saveButton.setVisibility(View.VISIBLE);
                    saveButton.setOnClickListener(this);
                }
                else {
                    activeEditorTextView.setText(activeEditor.getUSER_NAME() + " is up next.");
                    addSentenceEditTextLayout.setVisibility(View.GONE);
                    saveButton.setVisibility(View.GONE);
                }
            }
        }
        return view;
    }

    private void refreshView(Story story) {
        if (story != null) {
            this.story = story;
            isActiveSession = false;
            storyTitleTextView.setText(story.getSTORY_NAME());
            if (story.getEDITS() != null) {
                storyEdits = story.getEDITS();
                spannableStringBuilder = new SpannableStringBuilder();
                for(int i = 0; i < storyEdits.size(); i++) {
                    StoryEdit storyEdit = storyEdits.get(i);
                    spannableStringBuilder.append(storyEdit.getSTORY_TEXT());
                    if((i + 1 < storyEdits.size())) {
                        spannableStringBuilder.append("  ");
                    }
                }
                storyTextTextView.setText(spannableStringBuilder);
            }
            if (story.getMEMBERS() != null) {
                storyMembers = story.getMEMBERS();
                for (Member member: storyMembers) {
                    if (member.getUSER_ID().equals(user.getUSER_ID())) {
                        if (story.getACTIVE_EDITOR_NUM().equals(member.getEDITING_ORDER())) {
                            isActiveSession = true;
                        }
                    }
                    if (story.getACTIVE_EDITOR_NUM().equals(member.getEDITING_ORDER())) {
                        activeEditor = member;
                    }
                }
                if (isActiveSession) {
                    activeEditorTextView.setText("You're up!");
                    addSentenceEditTextLayout.setVisibility(View.VISIBLE);
                    saveButton.setVisibility(View.VISIBLE);
                    saveButton.setOnClickListener(this);
                }
                else {
                    activeEditorTextView.setText(activeEditor.getUSER_NAME() + " is up next.");
                    addSentenceEditTextLayout.setVisibility(View.GONE);
                    saveButton.setVisibility(View.GONE);
                }
            }
        }
    }

    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_story, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete) {
            if (story != null) {
                deleteStory(story);
            }
            return(true);
        }
        else if (id == R.id.action_edit_title) {
            if (story != null) {
                navigateToEditStoryTitleFragment();
            }
            return(true);
        }
        else if (id == R.id.action_search_users) {
            if (story != null) {
                navigateToFriendsFragment();
            }
            return(true);
        }
        return(super.onOptionsItemSelected(item));
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fragment_edit_story_submit_button) {
            activeEditText = addSentenceEditText.getText().toString().trim();
            if (!TextUtils.isEmpty(activeEditText)) {
                saveEdit();
            }
        }
    }

    private void readStory() {
        progressBar.setVisibility(View.VISIBLE);
        apiCalls = APIInstance.getRetrofitInstance().create(APICalls.class);
        Call<Story> call = apiCalls.readStoryByStoryId(story.getSTORY_ID());
        call.enqueue(new Callback<Story>() {
            @Override
            public void onResponse(Call<Story> call, Response<Story> response) {
                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    if (response.body() != null) {
                        refreshView(response.body());
                    }
                }
                else {
                    progressBar.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(Call<Story> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Snackbar.make(Objects.requireNonNull(getView()), "failure" , Snackbar.LENGTH_SHORT).show();
                Log.d("call",call.toString());
                Log.d("throwable",t.toString());
            }
        });
    }

    private void saveEdit() {
        StoryEdit storyEdit = new StoryEdit(story.getSTORY_ID(),user.getUSER_ID(), activeEditText);
        progressBar.setVisibility(View.VISIBLE);
        apiCalls = APIInstance.getRetrofitInstance().create(APICalls.class);
        Call<GenericPOSTResponse> call = apiCalls.createStoryEdit(storyEdit);
        call.enqueue(new Callback<GenericPOSTResponse>() {
            @Override
            public void onResponse(Call<GenericPOSTResponse> call, Response<GenericPOSTResponse> response) {
                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(getView(), "saved successfully!", Snackbar.LENGTH_SHORT).show();
                    readStory();
                }
                else {
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(getView(), "unable to save :(", Snackbar.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<GenericPOSTResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Snackbar.make(getView(), "failure" , Snackbar.LENGTH_SHORT).show();
                Log.d("call",call.toString());
                Log.d("throwable",t.toString());
            }
        });
    }

    private void deleteStory(Story story) {
        progressBar.setVisibility(View.VISIBLE);
        apiCalls = APIInstance.getRetrofitInstance().create(APICalls.class);
        Call<GenericPOSTResponse> call = apiCalls.deleteStory(story);
        call.enqueue(new Callback<GenericPOSTResponse>() {
            @Override
            public void onResponse(Call<GenericPOSTResponse> call, Response<GenericPOSTResponse> response) {
                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(Objects.requireNonNull(getView()), "story deleted!", Snackbar.LENGTH_SHORT).show();
                    Navigation.findNavController(Objects.requireNonNull(getActivity()),R.id.main_content).navigateUp();
                }
                else {
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(Objects.requireNonNull(getView()), "unable to delete story :(", Snackbar.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<GenericPOSTResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Snackbar.make(Objects.requireNonNull(getView()), "failure" , Snackbar.LENGTH_SHORT).show();
                Log.d("call",call.toString());
                Log.d("throwable",t.toString());
            }
        });
    }

    private void navigateToEditStoryTitleFragment() {
        Bundle storyBundle = new Bundle();
        storyBundle.putSerializable("Story", story);
        Navigation.findNavController(getView()).navigate(R.id.action_fragment_story_to_fragment_edit_story_title,storyBundle);
    }

    private void navigateToFriendsFragment() {
        Bundle storyBundle = new Bundle();
        storyBundle.putSerializable("Story", story);
        storyBundle.putString("Mode","UPDATE");
        Navigation.findNavController(getView()).navigate(R.id.action_fragment_story_to_fragment_friends,storyBundle);
    }

}
