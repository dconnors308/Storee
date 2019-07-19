package com.example.stohre.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.example.stohre.objects.Story;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditStoryFragment extends Fragment {

    private APICalls apiCalls;
    private SharedPreferences sharedPreferences;
    private ProgressBar progressBar;
    private Story story;
    private AppCompatTextView storyTitleTextView;
    private AppCompatTextView storyTextTextView;
    private TextInputEditText addSentenceEditText;
    private MaterialButton saveButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            story = (Story) getArguments().getSerializable("Story");
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_story,container,false);
        progressBar = Objects.requireNonNull(getActivity()).findViewById(R.id.progress_bar_horizontal_activity_main);
        storyTitleTextView = view.findViewById(R.id.fragment_edit_story_title_text_view);
        storyTextTextView = view.findViewById(R.id.fragment_edit_story_text_text_view);
        saveButton = view.findViewById(R.id.fragment_edit_story_save_button);
        addSentenceEditText = view.findViewById(R.id.fragment_edit_story_edit_text_view);
        if (story != null) {
            storyTitleTextView.setText(story.getSTORY_NAME());
            if (story.getSTORY_TEXT() != null) {
                storyTextTextView.setText(story.getSTORY_TEXT());
            }
        }
        return view;
    }

    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_delete_without_icon, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            if (story != null) {
                deleteStory(story);
            }
            return(true);
        }
        return(super.onOptionsItemSelected(item));
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
}
