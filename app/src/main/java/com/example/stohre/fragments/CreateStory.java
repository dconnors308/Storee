package com.example.stohre.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.stohre.MainActivity;
import com.example.stohre.R;
import com.example.stohre.objects.User;

public class CreateStory extends Fragment {
    private static final String USER_ARG_KEY = "USER_ARG_KEY";

    public static CreateStory newInstance(User user) {
        CreateStory createStoryFragment = new CreateStory();
        Bundle bundle = new Bundle();
        bundle.putSerializable(USER_ARG_KEY, user);
        createStoryFragment.setArguments(bundle);
        return createStoryFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity) getActivity()).getSupportActionBar();
        return inflater.inflate(R.layout.fragment_create_story, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflater.inflate(R.menu.contact_menu_search, menu);
        //MenuItem searchItem = menu.findItem(R.id.action_search);
        super.onCreateOptionsMenu(menu,inflater);
    }

}
