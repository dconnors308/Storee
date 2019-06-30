package com.example.stohre.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.stohre.R;
import com.example.stohre.dialogs.CreateUsername;
import com.example.stohre.objects.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;

public class Login extends Fragment implements CreateUsername.CreateUsernameDialogListener {

    private GoogleSignInClient googleSignInClient;
    private GoogleSignInAccount googleSignInAccount;
    private final int RC_SIGN_IN = 1;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getSharedPreferences("com.example.Stohre", 0).edit().clear().commit();
        sharedPreferences = getActivity().getSharedPreferences("com.example.Stohre", MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build(); //check for last sign on
        googleSignInClient = GoogleSignIn.getClient(getActivity(), googleSignInOptions);
        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(getActivity());
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //check google authentication result
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            googleSignInAccount = completedTask.getResult(ApiException.class);
            if (!sharedPreferences.getString("username", "").isEmpty()) {
                showCreateUsernameDialog();
            }
        } catch (ApiException e) {
            Log.w("SIGN IN FAILURE", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(getActivity(), "google sign in failure", Toast.LENGTH_SHORT).show();
        }
    }
    private void showCreateUsernameDialog() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        CreateUsername createUsernameDialog = CreateUsername.newInstance("Create Username");
        createUsernameDialog.show(fm, "CREATE_USERNAME_FRAGMENT_TAG");
    }

    @Override
    public void onFinishEditDialog(String username) {
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(getActivity(), "no username entered, account creation unsuccessful" + username, Toast.LENGTH_SHORT).show();
        }
        else {
            sharedPreferences.edit().putString("username", username).commit();
            User user = new User();
            user.setUSER_ID(googleSignInAccount.getId());
            user.setUSER_NAME(username);
            SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(user);
            prefsEditor.putString("user", json);
            prefsEditor.commit();
            //navigateToMainActivity(user);
        }
    }

}
