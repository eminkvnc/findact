package com.findact;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.findact.APIs.PostModel;
import com.findact.Adapters.PostListItemAdapter;
import com.findact.Firebase.FirebaseDBHelper;
import com.findact.Firebase.UserData;
import com.findact.RoomDatabase.User;
import com.findact.RoomDatabase.UserDatabase;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    public static final int INIT_MODE_MY_PROFILE_PAGE = 0;
    public static final int INIT_MODE_DEFAULT_PROFILE_PAGE = 1;
    public static String TAG = "ProfileFragment";
    private int initMode;
    private View v;

    public static UsersListDialog listDialog;
    ProgressDialog progressDialog;
    ImageView profilePictureImageView;
    ImageView addFriendImageView;
    ImageView requestsImageView;
    TextView nameTextView, ageTextView, cityTextView, followersTextView, followingTextView;
    RecyclerView activitiesRecyclerView;
    SwipeRefreshLayout activitiesSwipeRefreshLayout;
    ArrayList<UserData> friendsArrayList;
    ArrayList<Integer> statusList;
    FirebaseDBHelper firebaseDBHelper;

    private PostListItemAdapter postListItemAdapter;
    private ArrayList<PostModel> postModelArrayList;

    private SettingsFragment settingsFragment;
    User user;
    UserData userData;
    int requestStatus;
    String currentUsername;

    public static ProfileFragment getInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        friendsArrayList = new ArrayList<>();
        statusList = new ArrayList<>();
        postModelArrayList = new ArrayList<>();
        firebaseDBHelper = FirebaseDBHelper.getInstance();
        progressDialog = new ProgressDialog(getContext());
        listDialog = UsersListDialog.getInstance();
        if(initMode == INIT_MODE_DEFAULT_PROFILE_PAGE) {
            MainActivity.setDisplayingFragment(DisplayActivityFragment.TAG);
            userData = new UserData(getArguments().getBundle("UserData"));
            currentUsername = userData.getUuidString();
            if(userData.getUuidString().equals(firebaseDBHelper.getCurrentUser())){
                initMode = INIT_MODE_MY_PROFILE_PAGE;
                currentUsername = firebaseDBHelper.getCurrentUser();
            }
            requestStatus = getArguments().getInt("RequestStatus");
        }
        else{
            MainActivity.setDisplayingFragment(ProfileFragment.TAG);
            currentUsername= firebaseDBHelper.getCurrentUser();
            user = UserDatabase.getInstance(getContext()).getUserDao().getDatas(firebaseDBHelper.getCurrentUser());
            userData = user.toUserData();
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        switch (initMode){
            case INIT_MODE_MY_PROFILE_PAGE:
                MainActivity.setDisplayingFragment(ProfileFragment.TAG);
                actionBar.setTitle(user.getUsername());
                break;
            case INIT_MODE_DEFAULT_PROFILE_PAGE:
                MainActivity.setDisplayingFragment(DisplayActivityFragment.TAG);
                actionBar.setTitle(userData.getUsername());
                break;
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_profile,container,false);

        profilePictureImageView = v.findViewById(R.id.fragment_profile_picture_iv);
        requestsImageView = v.findViewById(R.id.fragment_profile_requests_iv);
        addFriendImageView = v.findViewById(R.id.fragment_profile_add_friend_iv);
        followersTextView = v.findViewById(R.id.fragment_profile_followers_tv);
        followingTextView = v.findViewById(R.id.fragment_profile_following_tv);
        nameTextView = v.findViewById(R.id.fragment_profile_name_tv);
        ageTextView = v.findViewById(R.id.fragment_profile_age_tv);
        cityTextView = v.findViewById(R.id.fragment_profile_city_tv);
        activitiesRecyclerView = v.findViewById(R.id.fragment_profile_activities_rv);
        activitiesSwipeRefreshLayout = v.findViewById(R.id.fragment_profile_activities_srl);

        activitiesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        postListItemAdapter = new PostListItemAdapter(getContext(),postModelArrayList,false);
        activitiesRecyclerView.setAdapter(postListItemAdapter);

        activitiesSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
                activitiesSwipeRefreshLayout.setRefreshing(false);
            }
        });

        addFriendImageView.setOnClickListener(this);
        requestsImageView.setOnClickListener(this);
        followersTextView.setOnClickListener(this);
        followingTextView.setOnClickListener(this);

        switch (initMode){
            case INIT_MODE_MY_PROFILE_PAGE:
                initMyProfile();
                break;
           case INIT_MODE_DEFAULT_PROFILE_PAGE:
               initDefaultProfile();
               break;
       }

       refreshData();
        return v;
    }

    private void initMyProfile(){
        setHasOptionsMenu(true);
        addFriendImageView.setVisibility(View.GONE);
        requestsImageView.setVisibility(View.VISIBLE);
        settingsFragment = new SettingsFragment();
        String fullName =user.getFirstname()+" "+ user.getLastname();
        nameTextView.setText(fullName);
        cityTextView.setText(user.getCity());
        String birthday = user.getBirthday();
        ageTextView.setText(birthday);
        try{
            File file = new File("/data/user/0/com.findact/app_imageDir",
                    firebaseDBHelper.getCurrentUser()+".jpg" );
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(file));
            profilePictureImageView.setImageBitmap(b);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initDefaultProfile(){
        setHasOptionsMenu(false);
        addFriendImageView.setVisibility(View.VISIBLE);
        requestsImageView.setVisibility(View.GONE);
        switch (requestStatus){
            case FirebaseDBHelper.FRIEND_REQUEST_STATUS_UNFOLLOWED:
                addFriendImageView.setImageResource(R.drawable.send_follow_request);
                break;
            case FirebaseDBHelper.FRIEND_REQUEST_STATUS_ACCEPTED:
                addFriendImageView.setImageResource(R.drawable.circle_cancel);
                break;
            case FirebaseDBHelper.FRIEND_REQUEST_STATUS_REQUEST_WAITING:
                addFriendImageView.setImageResource(R.drawable.undo_foolow_request);
                break;
        }
        Picasso.get().load(userData.getProfilePictureUri()).into(profilePictureImageView);
        nameTextView.setText(userData.getFirstname()+" "+userData.getLastname());
        cityTextView.setText(userData.getCity());
        ageTextView.setText(userData.getBirthdate());
    }

    public void refreshData(){
        progressDialog.show();
        firebaseDBHelper.getUserPosts(userData, postModelArrayList, new OnTaskCompletedListener() {
            @Override
            public void onTaskCompleted() {
                postListItemAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }
        });
    }

    private void signOut() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.putExtra("SignOut",true);
        startActivity(intent);
    }
    public int getInitMode() {
        return initMode;
    }
    public void setInitMode(int initMode) {
        this.initMode = initMode;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fragment_profile_add_friend_iv:

                switch(requestStatus){
                    case FirebaseDBHelper.FRIEND_REQUEST_STATUS_UNFOLLOWED:
                        firebaseDBHelper.sendFollowRequest(userData.getUuidString());
                        addFriendImageView.setImageResource(R.drawable.undo_foolow_request);
                        requestStatus = FirebaseDBHelper.FRIEND_REQUEST_STATUS_REQUEST_WAITING;
                        break;
                    case FirebaseDBHelper.FRIEND_REQUEST_STATUS_REQUEST_WAITING:
                        firebaseDBHelper.undoFollowRequest(userData.getUuidString());
                        addFriendImageView.setImageResource(R.drawable.send_follow_request);
                        requestStatus = FirebaseDBHelper.FRIEND_REQUEST_STATUS_UNFOLLOWED;
                        break;
                    case FirebaseDBHelper.FRIEND_REQUEST_STATUS_ACCEPTED:
                        firebaseDBHelper.unfollowUser(userData.getUuidString());
                        addFriendImageView.setImageResource(R.drawable.send_follow_request);
                        requestStatus = FirebaseDBHelper.FRIEND_REQUEST_STATUS_UNFOLLOWED;
                        break;
                }

                break;

            case R.id.fragment_profile_following_tv:
                progressDialog.show();
                firebaseDBHelper.getFollowing(currentUsername, friendsArrayList, statusList, new OnTaskCompletedListener() {
                    @Override
                    public void onTaskCompleted() {
                        showListDialog(getResources().getText(R.string.profile_fragment_following).toString());
                    }
                });
                break;

            case R.id.fragment_profile_followers_tv:
                progressDialog.show();
                firebaseDBHelper.getFollowers(currentUsername, friendsArrayList, statusList, new OnTaskCompletedListener() {
                    @Override
                    public void onTaskCompleted() {
                        showListDialog(getResources().getText(R.string.profile_fragment_followers).toString());
                    }
                });
                break;


            case R.id.fragment_profile_requests_iv:
                progressDialog.show();
                firebaseDBHelper.getFollowRequests(friendsArrayList, statusList, new OnTaskCompletedListener() {
                    @Override
                    public void onTaskCompleted() {
                        showListDialog(getResources().getText(R.string.profile_fragment_requests).toString());
                    }
                });
                break;
        }
    }

    private void showListDialog(final String dialogTitle){

        Bundle bundle = new Bundle();
        bundle.putString("Title",dialogTitle);
        Bundle followersArrayListBundle = new Bundle();
        if(initMode == INIT_MODE_MY_PROFILE_PAGE && dialogTitle.equals(getResources().getText(R.string.profile_fragment_followers).toString())){
            statusList.clear();
        }
        for(int i = 0; i < friendsArrayList.size(); i++){
            followersArrayListBundle.putBundle(String.valueOf(i),friendsArrayList.get(i).UserDatatoBundle());
            if(initMode == INIT_MODE_MY_PROFILE_PAGE && dialogTitle.equals(getResources().getText(R.string.profile_fragment_followers).toString())){
                statusList.add(i,FirebaseDBHelper.FRIEND_REQUEST_STATUS_NONE);
            }
        }
        bundle.putBundle("UserDataArrayList",followersArrayListBundle);
        bundle.putIntegerArrayList("StatusArrayList",statusList);
        progressDialog.dismiss();
        listDialog.setArguments(bundle);
        listDialog.show(getActivity().getSupportFragmentManager(),"dialog");

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.actionbar_for_profile,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch (item.getItemId()){
            case R.id.profile_fragment_actionbar_create_activity:

                Intent intent = new Intent(getContext(),CreateActivity.class);
                startActivity(intent);

                break;
            case R.id.profile_fragment_actionbar_settings:
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.main_frame,settingsFragment);
                fragmentTransaction.commit();
                break;
            case R.id.profile_fragment_actionbar_logout:
                signOut();
                break;
            case R.id.profile_fragment_actionbar_edit_profile:
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.main_frame,settingsFragment);
                fragmentTransaction.commit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
