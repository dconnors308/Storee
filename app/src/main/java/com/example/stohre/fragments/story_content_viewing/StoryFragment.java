package com.example.stohre.fragments.story_content_viewing;

import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StyleSpan;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.daasuu.ei.Ease;
import com.daasuu.ei.EasingInterpolator;
import com.example.stohre.R;
import com.example.stohre.api.APICalls;
import com.example.stohre.api.APIInstance;
import com.example.stohre.api.POSTResponse;
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
    private AppCompatTextView storyTextTextView;
    private TextInputLayout addSentenceEditTextLayout;
    private TextInputEditText actionEditText;
    private AppCompatTextView activeEditorTextView;
    private MaterialButton saveButton;
    private ArrayList<StoryEdit> storyEdits;
    private ArrayList<Member> storyMembers;
    private SpannableStringBuilder spannableStringBuilder;
    private boolean isActiveSession;
    private String activeEditText;
    private View fragmentView;

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
        fragmentView = inflater.inflate(R.layout.fragment_story,container,false);
        progressBar = Objects.requireNonNull(getActivity()).findViewById(R.id.progress_bar_horizontal_activity_main);
        storyTextTextView = fragmentView.findViewById(R.id.fragment_edit_story_text_text_view);
        activeEditorTextView = fragmentView.findViewById(R.id.fragment_edit_story_active_editor_text_view);
        saveButton = fragmentView.findViewById(R.id.fragment_edit_story_submit_button);
        actionEditText = fragmentView.findViewById(R.id.fragment_edit_story_action_edit_text);
        addSentenceEditTextLayout = fragmentView.findViewById(R.id.fragment_edit_story_action_text_input_layout);
        if (story != null) {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(story.getSTORY_NAME());
            if (story.getEDITS() != null) {
                storyEdits = story.getEDITS();
                spannableStringBuilder = new SpannableStringBuilder();
                for(int i = 0; i < storyEdits.size(); i++) {
                    StoryEdit storyEdit = storyEdits.get(i);
                    spannableStringBuilder.append(storyEdit.getSTORY_TEXT());
                    if((i + 1 < storyEdits.size())) {
                        spannableStringBuilder.append(" ");
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
                    int segmentStart = 0;
                    int segmentEnd = getResources().getString(R.string.youre_up).length();
                    spannableStringBuilder = new SpannableStringBuilder();
                    spannableStringBuilder.append(getResources().getString(R.string.youre_up));
                    spannableStringBuilder.setSpan(new StyleSpan(Typeface.BOLD),segmentStart, segmentEnd, 0);
                    activeEditorTextView.setText(spannableStringBuilder);
                    addSentenceEditTextLayout.setVisibility(View.VISIBLE);
                    saveButton.setVisibility(View.VISIBLE);
                    saveButton.setOnClickListener(this);
                    doBounceAnimation(activeEditorTextView);
                }
                else {
                    int segmentStart = 0;
                    int segmentEnd = activeEditor.getUSER_NAME().length();
                    spannableStringBuilder = new SpannableStringBuilder();
                    spannableStringBuilder.append(activeEditor.getUSER_NAME());
                    spannableStringBuilder.setSpan(new StyleSpan(Typeface.BOLD),segmentStart, segmentEnd, 0);
                    spannableStringBuilder.append(" ");
                    spannableStringBuilder.append(getResources().getString(R.string.is_up_next));
                    activeEditorTextView.setText(spannableStringBuilder);
                    addSentenceEditTextLayout.setVisibility(View.GONE);
                    saveButton.setVisibility(View.GONE);
                }
            }
        }
        return fragmentView;
    }

    private void refreshView(Story story) {
        if (story != null) {
            this.story = story;
            isActiveSession = false;
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
                    activeEditorTextView.setText(getActivity().getResources().getString(R.string.youre_up));
                    addSentenceEditTextLayout.setVisibility(View.VISIBLE);
                    saveButton.setVisibility(View.VISIBLE);
                    saveButton.setOnClickListener(this);
                    doBounceAnimation(activeEditorTextView);
                }
                else {
                    int segmentStart = 0;
                    int segmentEnd = activeEditor.getUSER_NAME().length();
                    spannableStringBuilder = new SpannableStringBuilder();
                    spannableStringBuilder.append(activeEditor.getUSER_NAME());
                    spannableStringBuilder.setSpan(new StyleSpan(Typeface.BOLD),segmentStart, segmentEnd, 0);
                    spannableStringBuilder.append(" ");
                    spannableStringBuilder.append(getResources().getString(R.string.is_up_next));
                    activeEditorTextView.setText(spannableStringBuilder);
                    addSentenceEditTextLayout.setVisibility(View.GONE);
                    saveButton.setVisibility(View.GONE);
                }
            }
        }
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
            activeEditText = actionEditText.getText().toString().trim();
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
        StoryEdit storyEdit = new StoryEdit(story.getSTORY_ID(), user.getUSER_NAME(), activeEditText);
        Log.i("story edit request",storyEdit.toString());
        progressBar.setVisibility(View.VISIBLE);
        apiCalls = APIInstance.getRetrofitInstance().create(APICalls.class);
        Call<POSTResponse> call = apiCalls.createStoryEdit(storyEdit);
        call.enqueue(new Callback<POSTResponse>() {
            @Override
            public void onResponse(Call<POSTResponse> call, Response<POSTResponse> response) {
                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(getView(), "story updated!", Snackbar.LENGTH_SHORT).show();
                    readStory();
                }
                else {
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(getView(), "unable to update story :/ please try again", Snackbar.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<POSTResponse> call, Throwable t) {
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
        Call<POSTResponse> call = apiCalls.deleteStory(story);
        call.enqueue(new Callback<POSTResponse>() {
            @Override
            public void onResponse(Call<POSTResponse> call, Response<POSTResponse> response) {
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
            public void onFailure(Call<POSTResponse> call, Throwable t) {
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
        storyBundle.putString("Mode","UPDATE");
        Navigation.findNavController(fragmentView).navigate(R.id.action_fragment_story_to_fragment_edit_title,storyBundle);
    }

    private void navigateToFriendsFragment() {
        Bundle storyBundle = new Bundle();
        storyBundle.putSerializable("Story", story);
        storyBundle.putString("Mode","UPDATE");
        Navigation.findNavController(fragmentView).navigate(R.id.action_fragment_story_to_fragment_friends_edit,storyBundle);
    }

}
