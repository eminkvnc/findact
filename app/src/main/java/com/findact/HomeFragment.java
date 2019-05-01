package com.findact;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.findact.APIs.PostModel;
import com.findact.Adapters.OfflinePostListItemAdapter;
import com.findact.Adapters.PostListItemAdapter;
import com.findact.Firebase.FirebaseDBHelper;
import com.findact.RoomDatabase.Post;
import com.findact.RoomDatabase.UserDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeFragment extends Fragment {

    public static String TAG = "HomeFragment";
    private View v;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PostListItemAdapter postListItemAdapter;
    private OfflinePostListItemAdapter offlinePostListItemAdapter;
    private ProgressDialog progressDialog;
    private ArrayList<PostModel> postModelArrayList;

    private ArrayList<Post> postArrayList;

    private FirebaseDBHelper firebaseDBHelper;

    public HomeFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.setDisplayingFragment(HomeFragment.TAG);
        setHasOptionsMenu(true);
        progressDialog = new ProgressDialog(getContext());
        postModelArrayList = new ArrayList<>();
        postArrayList = new ArrayList<>();
        firebaseDBHelper = FirebaseDBHelper.getInstance();
        refreshData();
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.setDisplayingFragment(HomeFragment.TAG);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(R.string.title_home);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_home,container,false);
        recyclerView = v.findViewById(R.id.home_fragment_rv);
        swipeRefreshLayout = v.findViewById(R.id.home_fragment_srl);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if (MainActivity.isOnline){
            postListItemAdapter = new PostListItemAdapter(getContext(),postModelArrayList);
            recyclerView.setAdapter(postListItemAdapter);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshData();
                    swipeRefreshLayout.setRefreshing(false);
                }

            });
        } else {

            List<Post> postList = UserDatabase.getInstance(getContext()).getPostDao().getData();
            postArrayList = new ArrayList<>(postList);
            offlinePostListItemAdapter = new OfflinePostListItemAdapter(getContext(),postArrayList);
            recyclerView.setAdapter(offlinePostListItemAdapter);
        }

        return v;
    }

    private void refreshData(){
        if(MainActivity.isOnline){
            progressDialog.show();
            firebaseDBHelper.getPosts(getContext(),postModelArrayList, new OnTaskCompletedListener() {

                @Override
                public void onTaskCompleted() {

                    Collections.sort(postModelArrayList, new Comparator<PostModel>() {
                        @Override
                        public int compare(PostModel postModel, PostModel t1) {
                            return t1.getShareDate().intValue() - postModel.getShareDate().intValue();
                        }
                    });
                    postListItemAdapter.notifyDataSetChanged();
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                }
            });
        }else {
            Toast.makeText(getContext(), "Check your internet connection", Toast.LENGTH_SHORT).show();
        }

    }
}
