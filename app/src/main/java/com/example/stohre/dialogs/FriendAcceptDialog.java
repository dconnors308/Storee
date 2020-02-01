package com.example.stohre.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.stohre.R;
import com.example.stohre.api.APICalls;
import com.example.stohre.api.APIInstance;
import com.example.stohre.objects.Friend;
import com.example.stohre.objects.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class FriendAcceptDialog extends Dialog  implements View.OnClickListener {

    private Context context;
    private APICalls apiCalls;
    private SharedPreferences sharedPreferences;
    private View view;
    private Friend friend;
    private User user;
    private MaterialButton acceptButton,declineButton;
    private TextView textView;
    private ProgressBar progressBar;
    private String otherUserName;

    public FriendAcceptDialog(@NonNull Context context, View view, User user, Friend friend, ProgressBar progressBar) {
        super(context);
        this.context = context;
        this.view = view;
        this.friend = friend;
        this.user = user;
        this.progressBar = progressBar;
        sharedPreferences = Objects.requireNonNull(context).getSharedPreferences("com.example.stohre", MODE_PRIVATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_friend_accept);
        textView = findViewById(R.id.dialog_friend_accept_text_view);
        if (friend.getREQUESTER_USER_ID().equals(user.getUSER_ID())) {
            otherUserName = friend.getACCEPTER_USER_NAME();
        }
        else {
            otherUserName = friend.getREQUESTER_USER_NAME();
        }
        String staticText = textView.getText().toString();
        int segmentStart = staticText.length();
        int segmentEnd = segmentStart + otherUserName.length();
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(staticText);
        spannableStringBuilder.append(otherUserName);
        spannableStringBuilder.setSpan(new StyleSpan(Typeface.BOLD),segmentStart, segmentEnd, 0);
        spannableStringBuilder.append("?");
        textView.setText(spannableStringBuilder);
        acceptButton = findViewById(R.id.dialog_friend_accept_button);
        declineButton = findViewById(R.id.dialog_friend_decline_button);
        acceptButton.setOnClickListener(this);
        declineButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_friend_accept_button:
                progressBar.setVisibility(View.VISIBLE);
                friend.setCONFIRMED("1");
                friend.setLIFECYCLE("RECEIVED");
                updateStatus();
                break;
            case R.id.dialog_friend_decline_button:
                progressBar.setVisibility(View.VISIBLE);
                friend.setCONFIRMED("0");
                friend.setLIFECYCLE("RECEIVED");
                updateStatus();
                break;
            default:
                break;
        }
    }

    private void updateStatus() {
        apiCalls = APIInstance.getRetrofitInstance().create(APICalls.class);
        Log.v("FRIEND",friend.getFRIEND_ID());
        Log.v("FRIEND",friend.getCONFIRMED());
        Log.v("FRIEND",friend.getLIFECYCLE());
        Call<Friend> call = apiCalls.updateFriend(friend);
        call.enqueue(new Callback<Friend>() {
            @Override
            public void onResponse(Call<Friend> call, Response<Friend> response) {
                if (response.isSuccessful()) {
                    friend = response.body();
                    if (friend.getCONFIRMED().equals("1")) {
                        Snackbar.make(view, "You and " + otherUserName + " are now friends!", Snackbar.LENGTH_SHORT).show();
                    }
                    else if (friend.getCONFIRMED().equals("0")) {
                        Snackbar.make(view, "You have declined " + otherUserName + "'s friend request :/", Snackbar.LENGTH_SHORT).show();
                    }
                }
                else {
                    Snackbar.make(view, "unable to accept/decline :(", Snackbar.LENGTH_SHORT).show();
                    Log.d("response",response.toString());
                }
                progressBar.setVisibility(View.GONE);
                dismiss();
            }
            @Override
            public void onFailure(Call<Friend> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Snackbar.make(view, "failure" , Snackbar.LENGTH_SHORT).show();
                Log.d("call",call.toString());
                Log.d("throwable",t.toString());
                dismiss();
            }
        });
    }

}
