package com.example.stohre.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.example.stohre.R;
import com.example.stohre.api.GetDataService;
import com.example.stohre.api.ReadOneUserResponse;
import com.example.stohre.api.RetrofitClientInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewStory extends Fragment implements SearchView.OnQueryTextListener, View.OnClickListener {

    private GetDataService service;
    private SearchView searchView;
    private Button button;
    private ProgressDialog progressDialog;
    private String memberUsername;
    private boolean memberUsernameIsValid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_story, container, false);
        searchView = rootView.findViewById(R.id.new_story_search_view) ;
        searchView.setOnQueryTextListener(this);
        button = rootView.findViewById(R.id.new_story_button);
        button.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        memberUsername = query;
        verifyUsername(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.create_user_name_button:
                if (!TextUtils.isEmpty(memberUsername) && memberUsernameIsValid) {

                }
                else {
                    Toast.makeText(getActivity(), "please enter a username", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void verifyUsername(String username) {
        service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("verifying username....");
        progressDialog.show();
        Call<ReadOneUserResponse> call = service.readOneUserByUsername(username);
        call.enqueue(new Callback<ReadOneUserResponse>() {
            @Override
            public void onResponse(Call<ReadOneUserResponse> call, Response<ReadOneUserResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "user found!", Toast.LENGTH_SHORT).show();
                    memberUsernameIsValid = true;
                    progressDialog.dismiss();
                }
                else {
                    Toast.makeText(getActivity(), "user does not exist :(", Toast.LENGTH_SHORT).show();
                    memberUsernameIsValid = false;
                    progressDialog.dismiss();
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
}
