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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.emin.findact.APIs.ActivityModel;
import com.example.emin.findact.APIs.GameModel;
import com.example.emin.findact.APIs.IGDbAPI;
import com.example.emin.findact.APIs.MovieModel;
import com.example.emin.findact.APIs.TMDbAPI;
import com.example.emin.findact.Adapters.ActivityListItemAdapter;
import com.example.emin.findact.Adapters.GameListItemAdapter;
import com.example.emin.findact.Adapters.MovieListItemAdapter;
import com.example.emin.findact.Adapters.UserListItemAdapter;
import com.example.emin.findact.Firebase.FirebaseAsyncTask;
import com.example.emin.findact.Firebase.FirebaseDBHelper;
import com.example.emin.findact.Firebase.UserData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class FindFragment extends Fragment implements View.OnClickListener, OnTaskCompletedListener {

    public static final String TAB_NAME_PERSON = "Person";
    public static final String TAB_NAME_MOVIE = "Movie";
    public static final String TAB_NAME_GAME = "Game";
    public static final String TAB_NAME_ACTIVITY = "Activity";

    public static String TAG = "FindFragment";
    public static String selectedTab;
    String sParam;
    FirebaseDBHelper firebaseDBHelper;
    FirebaseAsyncTask searchTask;
    ProgressDialog progressDialog;
    DisplayActivityFragment displayActivityFragment;
    String oldSearchParameter;
    HashMap<String,Boolean> firstClick;

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

    IGDbAPI igDbAPI;
    ArrayList<GameModel> gameModelArrayList;
    GameListItemAdapter findGameAdapter;

    ArrayList<ActivityModel> activityArrayList;
    ActivityListItemAdapter findActivityAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userDataArrayList = new ArrayList<>();
        requestStatus = new ArrayList<>();
        progressDialog = new ProgressDialog(getContext());
        firebaseDBHelper = FirebaseDBHelper.getInstance();
        tmDbAPI = new TMDbAPI();
        movieModelArrayList = new ArrayList<>();
        igDbAPI = new IGDbAPI();
        gameModelArrayList = new ArrayList<>();
        activityArrayList = new ArrayList<>();

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

        findGameAdapter = new GameListItemAdapter(getContext(),gameModelArrayList);
        searchGameRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        searchGameRecyclerView.setAdapter(findGameAdapter);

        findActivityAdapter = new ActivityListItemAdapter(getContext(),activityArrayList);
        searchGroupRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        searchGroupRecyclerView.setAdapter(findActivityAdapter);

        oldSearchParameter = searchEditText.getText().toString();
        firstClick = new HashMap<>();
        firstClick.put("Person",true);
        firstClick.put("Movie",true);
        firstClick.put("Game",true);
        firstClick.put("Group",true);

        searchImageView.setOnClickListener(this);
        personButton.setOnClickListener(this);
        movieButton.setOnClickListener(this);
        gameButton.setOnClickListener(this);
        groupButton.setOnClickListener(this);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                oldSearchParameter = searchEditText.getText().toString();
                firstClick.put("Person",true);
                firstClick.put("Movie",true);
                firstClick.put("Game",true);
                firstClick.put("Group",true);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
                if(!oldSearchParameter.equals(searchEditText.getText().toString()) && firstClick.get("Person")){
                    searchImageView.performClick();
                    firstClick.put("Person",false);
                }

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
                if(!oldSearchParameter.equals(searchEditText.getText().toString()) && firstClick.get("Movie")){
                    searchImageView.performClick();
                    firstClick.put("Movie",false);
                }

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
                if(!oldSearchParameter.equals(searchEditText.getText().toString()) && firstClick.get("Game")){
                    searchImageView.performClick();
                    firstClick.put("Game",false);
                }

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
                if(!oldSearchParameter.equals(searchEditText.getText().toString()) && firstClick.get("Group")){
                    searchImageView.performClick();
                    firstClick.put("Group",false);
                }

                break;

            case R.id.fragment_find_search_iv:

                String [] split = searchParameter.split(" ");
                int i = split.length;
                sParam = split[0];
                if (i > 1){
                    for (int j = 1; j < i; j++){
                        sParam = sParam + "+" + split[j];
                    }
                }
                if (!searchParameter.equals("")) {
                switch (selectedTab){
                    case "Person":
                        //runnable
                        Runnable searchUserRunnable = new Runnable() {
                            @Override
                            public void run() {
                                firebaseDBHelper.searchUser(searchParameter, userDataArrayList, requestStatus);
                            }
                        };
                        progressDialog.show();
                        searchTask = new FirebaseAsyncTask(searchUserRunnable,this);
                        searchTask.execute();
                        break;
                    case "Movie":
                        progressDialog.show();
                        tmDbAPI.searchMovie(sParam,movieModelArrayList,this);

                        break;
                    case "Game":
                        progressDialog.show();
                        igDbAPI.search(sParam, gameModelArrayList, this);
                        //igDbAPI.searchGame(sParam, gameModelArrayList,this);

                        break;
                    case "Group":
                        //runnable
                        Runnable searchActivityRunnable = new Runnable() {
                            @Override
                            public void run() {
                                firebaseDBHelper.searchActivity(searchParameter, activityArrayList);
                            }
                        };
                        progressDialog.show();
                        searchTask = new FirebaseAsyncTask(searchActivityRunnable,this);
                        searchTask.execute();
                        break;
                    default:
                        break;
                }
                }else{
                    userDataArrayList.clear();
                    movieModelArrayList.clear();
                    gameModelArrayList.clear();
                    activityArrayList.clear();
                    requestStatus.clear();

                    findGameAdapter.notifyDataSetChanged();
                    findMovieAdapter.notifyDataSetChanged();
                    findPersonAdapter.notifyDataSetChanged();
                    findActivityAdapter.notifyDataSetChanged();
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
                Toast.makeText(getContext(), "Task Completed", Toast.LENGTH_SHORT).show();
                findGameAdapter.notifyDataSetChanged();
                progressDialog.dismiss();

                break;
            case "Group":
                if(searchTask.isTimeout()){
                    Toast.makeText(getContext(), "Task Timeout", Toast.LENGTH_SHORT).show();
                    searchTask.cancel(true);
                    progressDialog.dismiss();
                }
                if(searchTask.isTaskComplete()) {
                    Toast.makeText(getContext(), "Task Completed", Toast.LENGTH_SHORT).show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            findActivityAdapter.notifyDataSetChanged();
                            progressDialog.dismiss();
                        }
                    }, 1000);
                }
                break;
        }
    }
}
