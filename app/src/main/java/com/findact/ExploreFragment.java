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
import com.findact.APIs.IGDbAPI;
import com.findact.APIs.MovieModel;
import com.findact.APIs.PostModel;
import com.findact.APIs.TMDbAPI;
import com.findact.Adapters.ExplorelistItemAdapter;
import com.findact.RoomDatabase.Post;
import com.findact.RoomDatabase.UserDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ExploreFragment extends Fragment {
    private View v;
    public static String TAG = "ExploreFragment";
    private ArrayList<ExploreModel> exploreModelArrayList;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private ExplorelistItemAdapter explorelistItemAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private IGDbAPI igDbAPI;
    private TMDbAPI tmDbAPI;

    public ExploreFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.setDisplayingFragment(ExploreFragment.TAG);
        setHasOptionsMenu(true);
        progressDialog = new ProgressDialog(getContext());
        exploreModelArrayList = new ArrayList<>();
        igDbAPI = new IGDbAPI();
        tmDbAPI = new TMDbAPI();
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
        if (MainActivity.isOnline){
            explorelistItemAdapter = new ExplorelistItemAdapter(exploreModelArrayList,getContext() );
            recyclerView.setAdapter(explorelistItemAdapter);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshData();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        } else {

        }

        return v;
    }

    private void refreshData(){
        if(MainActivity.isOnline){
            progressDialog.show();
//            tmDbAPI.searchMovieByID(, , );
        }else {
            Toast.makeText(getContext(), "Check your internet connection", Toast.LENGTH_SHORT).show();
        }

    }
}
