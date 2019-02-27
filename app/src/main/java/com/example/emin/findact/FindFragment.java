package com.example.emin.findact;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.emin.findact.APIs.MovieModel;
import com.example.emin.findact.APIs.TMDbAPI;
import com.example.emin.findact.Adapters.MovieListItemAdapter;
import com.example.emin.findact.Adapters.UserListItemAdapter;
import com.example.emin.findact.Firebase.FirebaseAsyncTask;
import com.example.emin.findact.Firebase.FirebaseDBHelper;
import com.example.emin.findact.Firebase.UserData;
import java.util.ArrayList;

public class FindFragment extends Fragment implements View.OnClickListener, OnTaskCompletedListener {

    public static String TAG = "FindFragment";
    FirebaseDBHelper firebaseDBHelper;
    FirebaseAsyncTask searchTask;
    ProgressDialog progressDialog;
    DisplayActivityFragment displayActivityFragment;
    String selectedTab;

    RecyclerView searchPersonRecyclerView;
    RecyclerView searchMovieRecyclerView;
    RecyclerView searchGameRecyclerView;
    RecyclerView searchGroupRecyclerView;
    Button personButton;
    Button movieButton;
    Button gameButton;
    Button groupButton;
    EditText searchEditText;
    ImageView searchImageView;



    ArrayList<UserData> userDataArrayList;
    ArrayList<Integer> requestStatus;
    UserListItemAdapter findPersonAdapter;

    TMDbAPI tmDbAPI;
    ArrayList<MovieModel> movieModelArrayList;
    MovieListItemAdapter findMovieAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userDataArrayList = new ArrayList<>();
        requestStatus = new ArrayList<>();
        progressDialog = new ProgressDialog(getContext());
        firebaseDBHelper = FirebaseDBHelper.getInstance();
        tmDbAPI = new TMDbAPI();
        movieModelArrayList = new ArrayList<>();
        selectedTab = "Person";
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(R.string.title_find);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        displayActivityFragment = new DisplayActivityFragment();
        View v = inflater.inflate(R.layout.fragment_find, container, false);

        movieButton = v.findViewById(R.id.fragment_find_movie_btn);
        gameButton = v.findViewById(R.id.fragment_find_game_btn);
        groupButton = v.findViewById(R.id.fragment_find_group_btn);
        personButton = v.findViewById(R.id.fragment_find_person_btn);
        searchEditText = v.findViewById(R.id.fragment_find_search_et);
        searchImageView = v.findViewById(R.id.fragment_find_search_iv);
        searchPersonRecyclerView = v.findViewById(R.id.fragment_find_person_rv);
        searchMovieRecyclerView = v.findViewById(R.id.fragment_find_movie_rv);
        searchGameRecyclerView = v.findViewById(R.id.fragment_find_game_rv);
        searchGroupRecyclerView = v.findViewById(R.id.fragment_find_group_rv);

        findPersonAdapter = new UserListItemAdapter(getContext(), userDataArrayList, requestStatus, TAG);
        searchPersonRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        searchPersonRecyclerView.setAdapter(findPersonAdapter);

        findMovieAdapter = new MovieListItemAdapter(getContext(),movieModelArrayList);
        searchMovieRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        searchMovieRecyclerView.setAdapter(findMovieAdapter);


        searchImageView.setOnClickListener(this);
        personButton.setOnClickListener(this);
        movieButton.setOnClickListener(this);
        gameButton.setOnClickListener(this);
        groupButton.setOnClickListener(this);

        personButton.performClick();

        return v;
    }

    @Override
    public void onClick(View v) {
        final String searchParameter = searchEditText.getText().toString();
        switch (v.getId()){
            case R.id.fragment_find_person_btn:
                personButton.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                movieButton.setTextColor(Color.BLACK);
                gameButton.setTextColor(Color.BLACK);
                groupButton.setTextColor(Color.BLACK);

                searchPersonRecyclerView.setVisibility(View.VISIBLE);
                searchMovieRecyclerView.setVisibility(View.GONE);
                searchGameRecyclerView.setVisibility(View.GONE);
                searchGroupRecyclerView.setVisibility(View.GONE);

                selectedTab = "Person";
                searchImageView.performClick();

                break;

            case R.id.fragment_find_movie_btn:
                personButton.setTextColor(Color.BLACK);
                movieButton.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                gameButton.setTextColor(Color.BLACK);
                groupButton.setTextColor(Color.BLACK);

                searchPersonRecyclerView.setVisibility(View.GONE);
                searchMovieRecyclerView.setVisibility(View.VISIBLE);
                searchGameRecyclerView.setVisibility(View.GONE);
                searchGroupRecyclerView.setVisibility(View.GONE);

                selectedTab = "Movie";
                searchImageView.performClick();

                break;

            case R.id.fragment_find_game_btn:
                personButton.setTextColor(Color.BLACK);
                movieButton.setTextColor(Color.BLACK);
                gameButton.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                groupButton.setTextColor(Color.BLACK);

                searchPersonRecyclerView.setVisibility(View.GONE);
                searchMovieRecyclerView.setVisibility(View.GONE);
                searchGameRecyclerView.setVisibility(View.VISIBLE);
                searchGroupRecyclerView.setVisibility(View.GONE);

                selectedTab = "Game";
                searchImageView.performClick();

                break;

            case R.id.fragment_find_group_btn:
                personButton.setTextColor(Color.BLACK);
                movieButton.setTextColor(Color.BLACK);
                gameButton.setTextColor(Color.BLACK);
                groupButton.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                searchPersonRecyclerView.setVisibility(View.GONE);
                searchMovieRecyclerView.setVisibility(View.GONE);
                searchGameRecyclerView.setVisibility(View.GONE);
                searchGroupRecyclerView.setVisibility(View.VISIBLE);

                selectedTab = "Group";
                searchImageView.performClick();

                break;

            case R.id.fragment_find_search_iv:

                if (!searchParameter.equals("")) {
                switch (selectedTab){
                    case "Person":
                        //runnable
                        Runnable searchRunnable = new Runnable() {
                            @Override
                            public void run() {
                                firebaseDBHelper.searchUser(searchParameter, userDataArrayList, requestStatus);
                            }
                        };
                        progressDialog.show();
                        searchTask = new FirebaseAsyncTask(searchRunnable,this);
                        searchTask.execute();
                        break;
                    case "Movie":
                        progressDialog.show();
                        tmDbAPI.searchMovie(searchParameter,movieModelArrayList,this);

                        break;
                    case "Game":

                        break;
                    case "Group":

                        break;
                }
                }else{
                    userDataArrayList.clear();
                    movieModelArrayList.clear();
                    requestStatus.clear();
                    findPersonAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    @Override
    public void onTaskCompleted() {
        switch (selectedTab){
            case "Person":

                if(searchTask.isTimeout()){
                    Toast.makeText(getContext(), "Task Timeout", Toast.LENGTH_SHORT).show();
                    searchTask.cancel(true);
                    progressDialog.dismiss();
                }
                if(searchTask.isTaskComplete()){
                    Toast.makeText(getContext(), "Task Completed", Toast.LENGTH_SHORT).show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            findPersonAdapter.notifyDataSetChanged();
                            progressDialog.dismiss();
                        }
                    },1000);
                }

                break;
            case "Movie":

                Toast.makeText(getContext(), "Task Completed", Toast.LENGTH_SHORT).show();
                findMovieAdapter.notifyDataSetChanged();
                progressDialog.dismiss();

                break;
            case "Game":

                break;
            case "Group":

                break;
        }
    }
}
