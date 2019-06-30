package com.example.stohre.dialogs;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.stohre.R;

public class CreateUsername extends DialogFragment implements TextView.OnEditorActionListener {

    private EditText usernameEditText;

    public interface CreateUsernameDialogListener {
        void onFinishEditDialog(String inputText);
    }

    public CreateUsername() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static CreateUsername newInstance(String title) {
        CreateUsername frag = new CreateUsername();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_username, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        usernameEditText = view.findViewById(R.id.create_user_username_edit_text);
        usernameEditText.setOnEditorActionListener(this);
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Create Username");
        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field
        usernameEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            // Return input text back to activity through the implemented listener
            CreateUsernameDialogListener listener = (CreateUsernameDialogListener) getActivity();
            listener.onFinishEditDialog(usernameEditText.getText().toString());
            // Close the dialog and return back to the parent activity
            dismiss();
            return true;
        }
        return false;
    }
}