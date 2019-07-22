package com.example.stohre.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.stohre.R;
import com.example.stohre.adapters.FriendsAdapter;
import com.example.stohre.api.APICalls;
import com.example.stohre.api.APIInstance;
import com.example.stohre.objects.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class SearchUsersDialog extends Dialog  implements View.OnClickListener, TextView.OnEditorActionListener {

    private Context context;
    private SharedPreferences sharedPreferences;
    private View view;
    private EditText usernameEditText;
    private MaterialButton okButton;
    private APICalls apiCalls;
    private ProgressBar progressBar;
    private User user;
    private String username, userId;
    private FriendsAdapter friendsAdapter;
    private ArrayList<User> friends;

    public SearchUsersDialog(@NonNull Context context, View view, User user, FriendsAdapter friendsAdapter, ArrayList<User> friends, ProgressBar progressBar) {
        super(context);
        this.context = context;
        this.view = view;
        this.user = user;
        this.friendsAdapter = friendsAdapter;
        this.friends = friends;
        this.progressBar = progressBar;
        sharedPreferences = Objects.requireNonNull(context).getSharedPreferences("com.example.stohre", MODE_PRIVATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_search_users);
        usernameEditText = findViewById(R.id.dialog_friends_username_edit_text);
        okButton = findViewById(R.id.dialog_friends_ok_button);
        okButton.setOnClickListener(this);
        usernameEditText.requestFocus();
        usernameEditText.setOnEditorActionListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_friends_ok_button:
                InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(usernameEditText.getWindowToken(),0);
                username = usernameEditText.getText().toString().trim();
                verifyMemberUsername();
                break;
            default:
                break;
        }
        dismiss();
    }

    private void verifyMemberUsername() {
        progressBar.setVisibility(View.VISIBLE);
        apiCalls = APIInstance.getRetrofitInstance().create(APICalls.class);
        Call<User> call = apiCalls.readOneUserByUsername(username);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Snackbar.make(view, username + " added!" , Snackbar.LENGTH_SHORT).show();
                    if (response.body() != null) {
                        if (friends == null) {
                            friends = new ArrayList<>();
                        }
                        username = response.body().getUSER_NAME();
                        userId = response.body().getUSER_ID();
                        User friend = new User();
                        friend.setUSER_NAME(SearchUsersDialog.this.username);
                        friend.setUSER_ID(userId);
                        friends.add(friend);
                        updateSharedPrefs();
                        friendsAdapter.notifyDataSetChanged();
                    }

                    progressBar.setVisibility(View.GONE);
                    dismiss();
                }
                else {
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(view, username + " does not exist" , Snackbar.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Snackbar.make(view, "failure" , Snackbar.LENGTH_SHORT).show();
                Log.d("call",call.toString());
                Log.d("throwable",t.toString());
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void updateSharedPrefs() {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(friends);
        prefsEditor.putString("friends", json);
        prefsEditor.apply();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(actionId == EditorInfo.IME_ACTION_DONE){
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(usernameEditText.getWindowToken(),0);
            username = usernameEditText.getText().toString().trim();
            verifyMemberUsername();
            return true;
        }
        return false;
    }
}
