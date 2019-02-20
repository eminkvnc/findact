package com.example.emin.findact;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

import com.example.emin.findact.APIs.MovieModel;
import com.example.emin.findact.APIs.TMDbAPI;
import com.example.emin.findact.Adapters.MovieListItemAdapter;
import com.example.emin.findact.Firebase.FirebaseDBHelper;
import com.example.emin.findact.Firebase.UserData;

import java.util.ArrayList;

public class FindFragment extends Fragment {

    private View v;
    DisplayActivityFragment displayActivityFragment;
    ArrayList<UserData> userDataArrayList;
    ArrayList<Integer> requestStatus;
    MovieListItemAdapter movieListItemAdapter;
    ProgressDialog progressDialog;
    EditText searchEditText;
    FirebaseDBHelper firebaseDBHelper;
    TMDbAPI tmDbAPI;
    ArrayList<MovieModel> movieModelArrayList;
    RecyclerView searchRecylerView;

    public static FindFragment getInstance() {
        return new FindFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userDataArrayList = new ArrayList<>();
        requestStatus = new ArrayList<>();
        progressDialog = new ProgressDialog(getContext());
        firebaseDBHelper = FirebaseDBHelper.getInstance();
        tmDbAPI = new TMDbAPI();
        movieModelArrayList = new ArrayList<>();

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

        v = inflater.inflate(R.layout.fragment_find, container, false);
        Button movieButton = v.findViewById(R.id.movie_btn);
        Button gameButton = v.findViewById(R.id.game_btn);
        Button groupButton = v.findViewById(R.id.group_btn);
        searchEditText = v.findViewById(R.id.fragment_find_search_et);
        ImageView searchImageView = v.findViewById(R.id.fragment_find_search_iv);
        searchRecylerView = v.findViewById(R.id.fragment_find_rv);



        searchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchParameter = searchEditText.getText().toString();
                if (!searchParameter.equals("")) {
                    progressDialog.show();
                    movieModelArrayList.clear();
                    //userDataArrayList.clear();
                    //firebaseDBHelper.searchUser(searchEditText.getText().toString(), userDataArrayList, requestStatus);
                    tmDbAPI.searchMovie(getContext(),searchParameter, movieModelArrayList);

                    movieListItemAdapter = new MovieListItemAdapter(getContext(), movieModelArrayList);
                    searchRecylerView.setAdapter(movieListItemAdapter);
                    searchRecylerView.setLayoutManager(new LinearLayoutManager(getContext()));

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            movieListItemAdapter.notifyDataSetChanged();
//                            searchRecylerView.setAdapter(findAdapter);
//                            searchRecylerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            progressDialog.dismiss();
                        }
                    }, 1000);
                }

            }
        });

        movieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                displayActivityFragment.setInitMode(DisplayActivityFragment.INIT_MODE_MOVIE_ACTIVITY);
                setFindFragment(displayActivityFragment);
            }
        });
        gameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayActivityFragment.setInitMode(DisplayActivityFragment.INIT_MODE_GAME_ACTIVITY);
                setFindFragment(displayActivityFragment);
            }
        });
        groupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayActivityFragment.setInitMode(DisplayActivityFragment.INIT_MODE_GROUP_ACTIVITY);
                setFindFragment(displayActivityFragment);
            }
        });


        return v;
    }

    private void setFindFragment(Fragment fragment) {

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }

}






