package com.example.stohre.fragments.story_content_viewing;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.selection.Selection;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.stohre.MainActivity;
import com.example.stohre.R;
import com.example.stohre.adapters.StoriesAdapter;
import com.example.stohre.api.APICalls;
import com.example.stohre.api.APIInstance;
import com.example.stohre.database.DatabaseHelper;
import com.example.stohre.database.DatabaseModel;
import com.example.stohre.databinding.FragmentStoriesBinding;
import com.example.stohre.objects.Story;
import com.example.stohre.objects.User;
import com.example.stohre.workers.NotificationRefresher;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class StoriesFragment extends Fragment implements SearchView.OnQueryTextListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private APICalls service;
    private SharedPreferences sharedPreferences;
    private DatabaseHelper databaseHelper;
    private ProgressBar progressBar;
    private FragmentStoriesBinding fragmentStoriesBinding;
    private TextView notificationDot;
    private StoriesAdapter storiesAdapter;
    private ArrayList<Story> stories;
    private SelectionTracker<Long> selectionTracker;
    private User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            if (selectionTracker != null) {
                selectionTracker.onRestoreInstanceState(savedInstanceState);
            }
        }
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("com.example.stohre", MODE_PRIVATE);
        progressBar = getActivity().findViewById(R.id.progress_bar_horizontal_activity_main);
        fragmentStoriesBinding = FragmentStoriesBinding.inflate(inflater, container, false);
        fragmentStoriesBinding.fragmentStoriesAddButton.setOnClickListener(this);
        fragmentStoriesBinding.fragmentStoriesSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.secondaryColor));
        fragmentStoriesBinding.fragmentStoriesSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.primaryColor);
        fragmentStoriesBinding.fragmentStoriesSwipeRefreshLayout.setOnRefreshListener(this);
        if (!sharedPreferences.getString("user", "").isEmpty()) {
            Gson gson = new Gson();
            String json = sharedPreferences.getString("user", "");
            user = gson.fromJson(json, User.class);
        }
        return fragmentStoriesBinding.getRoot();
    }

    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (selectionTracker != null) {
            selectionTracker.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_stories, menu);
        MenuItem notificationMenuItem = menu.findItem(R.id.action_notifications);
        View notificationsView = menu.findItem(R.id.action_notifications).getActionView();
        notificationsView.setOnClickListener(v -> onOptionsItemSelected(notificationMenuItem));
        notificationDot = notificationsView.findViewById(R.id.notificationCounterTextView);
        loadStories();
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_notifications) {
            navigateToNotifications();
            return(true);
        }
        return(super.onOptionsItemSelected(item));
    }

    @Override
    public void onRefresh() {
        loadStories();
    }

    private void loadStories() {
        if (user != null) {
            readStoriesByUserId();
            NotificationRefresher notificationRefresher = new NotificationRefresher(getContext());
            notificationRefresher.readNotificationByUserId(user);
            if (unreadNotifications()) {
                notificationDot.setVisibility(View.VISIBLE);
            }
        }
    }

    private void readStoriesByUserId() {
        progressBar.setVisibility(View.VISIBLE);
        service = APIInstance.getRetrofitInstance().create(APICalls.class);
        Call<com.example.stohre.objects.Stories> call = service.readStoriesByUserId(user.getUSER_ID());
        call.enqueue(new Callback<com.example.stohre.objects.Stories>() {
            @Override
            public void onResponse(Call<com.example.stohre.objects.Stories> call, Response<com.example.stohre.objects.Stories> response) {
                Log.v("RESPONSE_CODE", String.valueOf(response.code()));
                Log.v("BODY", String.valueOf(response.body()));
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        stories = response.body().getStories();
                        configureRecyclerView(stories);

                    }
                    progressBar.setVisibility(View.GONE);
                }
                else {
                    progressBar.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(Call<com.example.stohre.objects.Stories> call, Throwable t) {
                Log.d("call",call.toString());
                Log.d("throwable",t.toString());
                progressBar.setVisibility(View.GONE);
                Snackbar.make(fragmentStoriesBinding.getRoot(), "failure" , Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void configureRecyclerView(ArrayList<Story> stories) {
        storiesAdapter = new StoriesAdapter(stories, user, getContext());
        fragmentStoriesBinding.fragmentStoriesRecyclerView.setAdapter(storiesAdapter);
        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.fall_down_animation);
        fragmentStoriesBinding.fragmentStoriesRecyclerView.setLayoutAnimation(animationController);
        selectionTracker = new SelectionTracker.Builder<>("my_selection", fragmentStoriesBinding.fragmentStoriesRecyclerView,
                new StoriesAdapter.KeyProvider(fragmentStoriesBinding.fragmentStoriesRecyclerView.getAdapter()),
                new StoriesAdapter.DetailsLookup(fragmentStoriesBinding.fragmentStoriesRecyclerView),
                StorageStrategy.createLongStorage()).withSelectionPredicate(new StoriesAdapter.Predicate()).build();
        storiesAdapter.setSelectionTracker(selectionTracker);
        selectionTracker.addObserver(new SelectionTracker.SelectionObserver() {
            @Override
            public void onSelectionChanged() {
                super.onSelectionChanged();
                if (selectionTracker.hasSelection()) {
                    Bundle storyBundle = new Bundle();
                    storyBundle.putSerializable("Story", getSelectedStory());
                    MainActivity mainActivity = (MainActivity) getActivity();
                    Objects.requireNonNull(mainActivity).navController.navigate(R.id.fragment_story, storyBundle);
                }
            }
        });
        fragmentStoriesBinding.fragmentStoriesSwipeRefreshLayout.setRefreshing(false);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean onQueryTextSubmit(String text) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String text) {
        storiesAdapter.getFilter().filter(text);
        return true;
    }

    private Story getSelectedStory() {
        Story story = null;
        Selection<Long> settingsSelection = selectionTracker.getSelection();
        for (Long settingSelectionId : settingsSelection) {
            story = stories.get(settingSelectionId.intValue());
            Log.i("STORY", story.getSTORY_NAME());
        }
        return story;
    }

    private ArrayList<Story> getSelectedStories() {
        Selection<Long> selection = selectionTracker.getSelection();
        for (Long settingSelectionId : selection) {
            Story story = this.stories.get(settingSelectionId.intValue());
            Log.i("STORY", story.getSTORY_NAME());
        }
        return stories;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fragment_stories_add_button) {
            Bundle storyBundle = new Bundle();
            storyBundle.putString("Mode","CREATE");
            MainActivity mainActivity = (MainActivity) getActivity();
            Objects.requireNonNull(mainActivity).navController.navigate(R.id.action_fragment_stories_to_navigation_new_story, storyBundle);
        }
    }

    private void navigateToNotifications() {
        MainActivity mainActivity = (MainActivity) getActivity();
        Objects.requireNonNull(mainActivity).navController.navigate(R.id.action_fragment_stories_to_fragment_notifications);
    }

    private boolean unreadNotifications() {
        boolean unread = false;
        databaseHelper = new DatabaseHelper(getActivity());
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String[] projection = {DatabaseModel.NOTIFICATIONS.NOTIFICATION_ID, DatabaseModel.NOTIFICATIONS.NOTIFICATION_TYPE, DatabaseModel.NOTIFICATIONS.NOTIFICATION_TEXT, DatabaseModel.NOTIFICATIONS.NOTIFICATION_STATUS};
        String selection = DatabaseModel.NOTIFICATIONS.NOTIFICATION_STATUS + " =?";
        String[] selectionArgs = { "UNREAD" };
        String sortOrder = DatabaseModel.NOTIFICATIONS.NOTIFICATION_ID + " ASC";
        Cursor cursor = db.query(
                DatabaseModel.NOTIFICATIONS.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
        if (cursor.getCount() > 0) {
            unread = true;
        }
        cursor.close();
        databaseHelper.close();
        return unread;
    }

}
