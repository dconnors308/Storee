package com.example.stohre.fragments;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.example.stohre.R;
import com.example.stohre.api.AddUserToStoryRequest;
import com.example.stohre.api.CreateStoryRequest;
import com.example.stohre.api.GenericResponse;
import com.example.stohre.api.APICalls;
import com.example.stohre.api.ReadOneUserResponse;
import com.example.stohre.api.ReadStoryIdRequest;
import com.example.stohre.api.ReadStoryIdResponse;
import com.example.stohre.api.APIInstance;
import com.example.stohre.objects.User;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static android.graphics.Color.GREEN;

public class NewStory extends Fragment implements SearchView.OnQueryTextListener, View.OnClickListener {

    private APICalls apiCalls;
    private TextView textView;
    private SearchView searchView;
    private MaterialButton button;
    private ProgressDialog progressDialog;
    private String memberUsername, memberId, storyName, storyId;
    private boolean memberUsernameIsValid;
    private SharedPreferences sharedPreferences;
    private User user;
    private View searchPlate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getActivity().getSharedPreferences("com.example.Stohre", MODE_PRIVATE);
        Log.v("on create view","CALLED");
        if (!sharedPreferences.getString("user", "").isEmpty()) {
            Gson gson = new Gson();
            String json = sharedPreferences.getString("user", "");
            user = gson.fromJson(json, User.class);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_story, container, false);
        textView = rootView.findViewById(R.id.new_story_text_view);
        searchView = rootView.findViewById(R.id.new_story_search_view) ;
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("start typing username...");
        int searchPlateId = searchView.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
        searchPlate = searchView.findViewById(searchPlateId);
        button = rootView.findViewById(R.id.new_story_button);
        button.setOnClickListener(this);
        progressDialog = new ProgressDialog(getActivity(),R.style.AppTheme_ProgressStyle);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        memberUsername = query;
        verifyMemberUsername(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        memberUsername = newText;
        verifyMemberUsername(newText);
        return false;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.new_story_button:
                storyName = textView.getText().toString();
                if (!TextUtils.isEmpty(storyName)) {
                    createNewStory(user.getUSER_ID(), storyName);
                    if (memberUsernameIsValid) {
                        readyStoryId(user.getUSER_ID(), storyName);
                        if (storyId != null) {
                            addMemberToStoryGroup(memberId, memberUsername, storyId, storyName);
                        }
                        else {
                            Toast.makeText(getActivity(), "unable to add user " + memberUsername, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else {
                    Toast.makeText(getActivity(), "please add at least one member to create a new story", Toast.LENGTH_SHORT).show();
                }
            break;
        }
    }

    public void verifyMemberUsername(String username) {
        apiCalls = APIInstance.getRetrofitInstance().create(APICalls.class);
        Call<ReadOneUserResponse> call = apiCalls.readOneUserByUsername(username);
        call.enqueue(new Callback<ReadOneUserResponse>() {
            @Override
            public void onResponse(Call<ReadOneUserResponse> call, Response<ReadOneUserResponse> response) {
                View searchPlate = searchView.findViewById(androidx.appcompat.R.id.search_plate);
                EditText searchTextView = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
                if (response.isSuccessful()) {
                    searchPlate.setBackgroundColor(Color.parseColor("#ffe0b2"));
                    searchTextView.setTextColor(Color.parseColor("#34515e"));
                    Toast.makeText(getActivity(), "user found!", Toast.LENGTH_SHORT).show();
                    memberUsernameIsValid = true;
                    memberUsername = response.body().getUSER_NAME();
                    memberId = response.body().getUSER_ID();
                }
                else {
                    memberUsernameIsValid = false;
                    searchPlate.setBackgroundColor(Color.parseColor("#ffe0b2"));
                    searchTextView.setTextColor(Color.parseColor("#34515e"));
                }
            }
            @Override
            public void onFailure(Call<ReadOneUserResponse> call, Throwable t) {
                Log.d("call",call.toString());
                Log.d("throwable",t.toString());
                memberUsernameIsValid = false;
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void createNewStory(String USER_ID, final String STORY_NAME) {
        CreateStoryRequest createStoryRequest = new CreateStoryRequest(USER_ID,STORY_NAME);
        apiCalls = APIInstance.getRetrofitInstance().create(APICalls.class);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("verifying username....");
        progressDialog.show();
        Call<GenericResponse> call = apiCalls.createStory(createStoryRequest);
        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "story " + STORY_NAME + " created!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                else {
                    Toast.makeText(getActivity(), STORY_NAME + " not created:(", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
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

    public void readyStoryId(String USER_ID, final String STORY_NAME) {
        ReadStoryIdRequest readStoryIdRequest = new ReadStoryIdRequest(USER_ID,STORY_NAME);
        apiCalls = APIInstance.getRetrofitInstance().create(APICalls.class);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("one moment....");
        progressDialog.show();
        Call<ReadStoryIdResponse> call = apiCalls.readStoryId(readStoryIdRequest);
        call.enqueue(new Callback<ReadStoryIdResponse>() {
            @Override
            public void onResponse(Call<ReadStoryIdResponse> call, Response<ReadStoryIdResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "story id found!", Toast.LENGTH_SHORT).show();
                    storyId = response.body().getSTORY_ID();
                    progressDialog.dismiss();
                }
                else {
                    Toast.makeText(getActivity(), "story id not found :(", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
            @Override
            public void onFailure(Call<ReadStoryIdResponse> call, Throwable t) {
                Log.d("call",call.toString());
                Log.d("throwable",t.toString());
                //memberUsernameIsValid = false;
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addMemberToStoryGroup(String USER_ID, final String USER_NAME, final String STORY_ID, final String STORY_NAME) {
        AddUserToStoryRequest addUserToStoryRequest = new AddUserToStoryRequest(STORY_ID,USER_ID);
        apiCalls = APIInstance.getRetrofitInstance().create(APICalls.class);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("adding " + USER_NAME + " to story " + STORY_NAME + "...");
        progressDialog.show();
        Call<GenericResponse> call = apiCalls.addUserToStory(addUserToStoryRequest);
        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), USER_NAME + " added to " + STORY_NAME, Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                else {
                    Toast.makeText(getActivity(), "unable to add " + USER_NAME + " to " + STORY_NAME, Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
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
}
