package com.findact;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.findact.APIs.ExploreModel;
import com.findact.APIs.GameModel;
import com.findact.APIs.GoogleRecAPI;
import com.findact.APIs.IGDbAPI;
import com.findact.APIs.MovieModel;
import com.findact.APIs.PostModel;
import com.findact.APIs.TMDbAPI;
import com.findact.Adapters.ExplorelistItemAdapter;
import com.findact.Firebase.EventLog;
import com.findact.Firebase.FirebaseDBHelper;
import com.findact.RoomDatabase.Post;
import com.findact.RoomDatabase.UserDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ExploreFragment extends Fragment {
    private View v;
    public static String TAG = "ExploreFragment";
    private ArrayList<ExploreModel> exploreModelArrayList;
    private ArrayList<Integer> recIdsArrayList;
    private ArrayList<MovieModel> movieModelArrayList;
    private ArrayList<GameModel> gameModelArrayList;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private ExplorelistItemAdapter explorelistItemAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private IGDbAPI igDbAPI;
    private TMDbAPI tmDbAPI;
    private GoogleRecAPI googleRecAPI;
    FirebaseDBHelper firebaseDBHelper;

    int id[];
    String movieGenres[];
    String gameGenres[];
    ArrayList<String> gameGenreList;
    ArrayList<String> movieGenreList;
    public ExploreFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.setDisplayingFragment(ExploreFragment.TAG);
        setHasOptionsMenu(true);
        progressDialog = new ProgressDialog(getContext());
        exploreModelArrayList = new ArrayList<>();
        movieModelArrayList = new ArrayList<>();
        gameModelArrayList = new ArrayList<>();
        igDbAPI = new IGDbAPI();
        tmDbAPI = new TMDbAPI();
        googleRecAPI = new GoogleRecAPI();
        recIdsArrayList = new ArrayList<>();
        firebaseDBHelper = new FirebaseDBHelper();
        id = new int[1];
        movieGenres = new String[1];
        gameGenres = new String[1];
        gameGenreList = new ArrayList<>();
        movieGenreList = new ArrayList<>();

        if (MainActivity.isOnline){

            if (MainActivity.id[0] == 0){
                firebaseDBHelper.getInitialUserLog(gameGenres, movieGenres, new OnTaskCompletedListener() {
                    @Override
                    public void onTaskCompleted() {
                        String splitGameGenre[] = gameGenres[0].split(",");
                        gameGenreList.addAll(Arrays.asList(splitGameGenre));
                        String splitMovieGenre[] = movieGenres[0].split(",");
                        movieGenreList.addAll(Arrays.asList(splitMovieGenre));
                        hasNoVote(gameGenreList,movieGenreList);
                    }
                });
            } else {
                recIdsArrayList.clear();
                progressDialog.show();
                googleRecAPI.getRecommendations(MainActivity.id[0], recIdsArrayList,"movie", new OnTaskCompletedListener() {
                    @Override
                    public void onTaskCompleted() {
                        googleRecAPI.getRecommendations(MainActivity.id[0], recIdsArrayList, "game", new OnTaskCompletedListener() {
                            @Override
                            public void onTaskCompleted() {
                                if (!recIdsArrayList.isEmpty()) {
                                    refreshData(recIdsArrayList);
                                }
                            }
                        });

                    }
                });
            }
//            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//                @Override
//                public void onRefresh() {
//                    refreshData(recIdsArrayList);
//                    swipeRefreshLayout.setRefreshing(false);
//                }
//            });
        } else {
            Toast.makeText(getContext(), "Check your internet connection", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.setDisplayingFragment(ExploreFragment.TAG);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(R.string.title_explore);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_explore,container,false);
        recyclerView = v.findViewById(R.id.fragment_explore_rv);
        swipeRefreshLayout = v.findViewById(R.id.fragment_explore_srl);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2 ));
        explorelistItemAdapter = new ExplorelistItemAdapter(exploreModelArrayList,getContext() );
        recyclerView.setAdapter(explorelistItemAdapter);

        return v;
    }

    private void refreshData(ArrayList<Integer> recIdsArrayList){
        if(MainActivity.isOnline){
            if (!progressDialog.isShowing()){
                progressDialog.show();
            }
            tmDbAPI.searchMovieByID(recIdsArrayList,exploreModelArrayList, new OnTaskCompletedListener() {
                @Override
                public void onTaskCompleted() {
                    explorelistItemAdapter.notifyDataSetChanged();

                }
            });
            igDbAPI.getGamesById(recIdsArrayList, exploreModelArrayList, new OnTaskCompletedListener() {
                @Override
                public void onTaskCompleted() {
                    explorelistItemAdapter.notifyDataSetChanged();
                }
            });

            progressDialog.dismiss();

        }else {
            progressDialog.dismiss();
            Toast.makeText(getContext(), "Check your internet connection", Toast.LENGTH_SHORT).show();
        }

    }

    private void hasNoVote(final ArrayList<String> gameGenreList, ArrayList<String> movieGenreList){
        if (MainActivity.isOnline){
            progressDialog.show();
            exploreModelArrayList.clear();
            tmDbAPI.searchMovieByGenreForExplore(movieGenreList, exploreModelArrayList, new OnTaskCompletedListener() {
                @Override
                public void onTaskCompleted() {
                    igDbAPI.searchByGenreForExplore(gameGenreList, exploreModelArrayList, new OnTaskCompletedListener() {
                        @Override
                        public void onTaskCompleted() {
                            Collections.shuffle(exploreModelArrayList);
                            explorelistItemAdapter.notifyDataSetChanged();
                            progressDialog.dismiss();
                        }
                    });
                }

            });

        }
    }
}
