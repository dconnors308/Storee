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
import com.example.stohre.api.POSTResponse;
import com.example.stohre.objects.Friend;
import com.example.stohre.objects.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class FriendRequestDialog extends Dialog  implements View.OnClickListener, TextView.OnEditorActionListener {

    private Context context;
    private SharedPreferences sharedPreferences;
    private View view;
    private EditText usernameEditText;
    private MaterialButton sendRequestButton;
    private APICalls apiCalls;
    private ProgressBar progressBar;
    private User user;
    private String accepterUsername;
    private User accepterUser;
    private FriendsAdapter friendsAdapter;
    private ArrayList<Friend> friends;
    private String friendshipStatus;

    public FriendRequestDialog(@NonNull Context context, View view, User user, FriendsAdapter friendsAdapter, ArrayList<Friend> friends, ProgressBar progressBar) {
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
        setContentView(R.layout.dialog_friend_request);
        usernameEditText = findViewById(R.id.dialog_friends_username_edit_text);
        usernameEditText.requestFocus();
        usernameEditText.setOnEditorActionListener(this);
        RxTextView.textChanges(usernameEditText)
                .debounce(3, TimeUnit.SECONDS)
                .subscribe(textChanged -> validateUsername());
        sendRequestButton = findViewById(R.id.dialog_friends_send_request_button);
        sendRequestButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_friends_send_request_button:
                if (existingRequest()) {
                    Snackbar.make(view, friendshipStatus, Snackbar.LENGTH_SHORT).show();
                }
                else {
                    sendRequest();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(actionId == EditorInfo.IME_ACTION_SEARCH){
            validateUsername();
            return true;
        }
        return false;
    }

    private void validateUsername() {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(usernameEditText.getWindowToken(),0);
        accepterUsername = usernameEditText.getText().toString().trim();
        if (!accepterUsername.isEmpty()) {
            apiCalls = APIInstance.getRetrofitInstance().create(APICalls.class);
            Call<User> call = apiCalls.readUserName(accepterUsername);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            accepterUser = response.body();
                            if (existingRequest()) {
                                Snackbar.make(view, friendshipStatus, Snackbar.LENGTH_SHORT).show();
                            }
                            else {
                                sendRequestButton.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    else {
                        Snackbar.make(view, accepterUsername + " does not exist" , Snackbar.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Snackbar.make(view, "failure" , Snackbar.LENGTH_SHORT).show();
                    Log.d("call",call.toString());
                    Log.d("throwable",t.toString());
                }
            });
        }
        else {
            Snackbar.make(view, "please enter a username", Snackbar.LENGTH_SHORT).show();
        }

    }

    private Boolean existingRequest() {
        boolean existingRequest = false;
        if (friends != null) {
            for (Friend friend: friends) {
                Log.i("USER LOGGING",user.getUSER_ID());
                if (friend.getACCEPTER_USER_ID().equals(user.getUSER_ID()) && friend.getREQUESTER_USER_ID().equals(accepterUser.getUSER_ID())) {
                    existingRequest = true;
                    if (friend.getCONFIRMED().equals("1")) {
                        friendshipStatus = "You are already friends with " + accepterUsername;
                    }
                    else {
                        friendshipStatus = "You have yet to accept " + accepterUsername + "'s request :/";
                    }
                }
                else if (friend.getACCEPTER_USER_ID().equals(accepterUser.getUSER_ID()) && friend.getREQUESTER_USER_ID().equals(user.getUSER_ID())) {
                    existingRequest = true;
                    if (friend.getCONFIRMED().equals("1")) {
                        friendshipStatus = "You are already friends with " + accepterUsername;
                    }
                    else {
                        friendshipStatus = accepterUsername + " has not accepted your request :(";
                    }
                }
            }
        }
        return existingRequest;
    }

    private void sendRequest() {
        Friend friend = new Friend(user.getUSER_ID(),accepterUser.getUSER_ID());
        apiCalls = APIInstance.getRetrofitInstance().create(APICalls.class);
        Call<POSTResponse> call = apiCalls.createFriend(friend);
        call.enqueue(new Callback<POSTResponse>() {
            @Override
            public void onResponse(Call<POSTResponse> call, Response<POSTResponse> response) {
                if (response.isSuccessful()) {
                    Snackbar.make(view,  "request sent to " + accepterUsername + "!" , Snackbar.LENGTH_SHORT).show();
                }
                else {
                    Snackbar.make(view, "unable to send friend request :(", Snackbar.LENGTH_SHORT).show();
                    Log.d("response",response.toString());
                }
                dismiss();
            }
            @Override
            public void onFailure(Call<POSTResponse> call, Throwable t) {
                Snackbar.make(view, "failure" , Snackbar.LENGTH_SHORT).show();
                Log.d("call",call.toString());
                Log.d("throwable",t.toString());
                dismiss();
            }
        });
    }

}
