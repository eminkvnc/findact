package com.example.emin.findact;

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

import com.example.emin.findact.Firebase.FirebaseAsyncTask;
import com.example.emin.findact.Firebase.FirebaseDBHelper;
import com.example.emin.findact.Firebase.UserData;
import com.example.emin.findact.RoomDatabase.User;
import com.example.emin.findact.RoomDatabase.UserDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

public class ProfileFragment extends Fragment implements View.OnClickListener {


    public static final int INIT_MODE_MY_PROFILE_PAGE = 0;
    public static final int INIT_MODE_FRIEND_PROFILE_PAGE = 1;
    private String TAG = "ProfileFragment";
    private int initMode;
    private View v;


    public static UsersListDialog listDialog;
    ProgressDialog progressDialog;
    ImageView profilePictureImageView;
    ImageView addFriendImageView;
    ImageView requestsImageView;
    TextView nameTextView, ageTextView, cityTextView, followersTextView, followingTextView;
    RecyclerView activitiesRecyclerView;
    ArrayList<UserData> friendsArrayList;
    ArrayList<Integer> statusList;
    FirebaseDBHelper firebaseDBHelper;

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
        firebaseDBHelper = FirebaseDBHelper.getInstance();
        progressDialog = new ProgressDialog(getContext());
        listDialog = UsersListDialog.getInstance();
        if(initMode == INIT_MODE_FRIEND_PROFILE_PAGE) {
            userData = new UserData(getArguments().getBundle("UserData"));
            currentUsername = userData.getUsername();
            if(userData.getUsername().equals(firebaseDBHelper.getCurrentUser())){
                initMode = INIT_MODE_MY_PROFILE_PAGE;
                currentUsername = firebaseDBHelper.getCurrentUser();
            }
            requestStatus = getArguments().getInt("RequestStatus");

        }
        else{
            currentUsername= firebaseDBHelper.getCurrentUser();
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        switch (initMode){
            case INIT_MODE_MY_PROFILE_PAGE:
                actionBar.setTitle(user.getUsername());
                break;
            case INIT_MODE_FRIEND_PROFILE_PAGE:
                actionBar.setTitle(userData.getUsername());
                break;
        }

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

                //create activity fragment

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

        activitiesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        activitiesRecyclerView.setVisibility(View.GONE);

        addFriendImageView.setOnClickListener(this);
        requestsImageView.setOnClickListener(this);
        followersTextView.setOnClickListener(this);
        followingTextView.setOnClickListener(this);

        switch (initMode){
            case INIT_MODE_MY_PROFILE_PAGE:
                setHasOptionsMenu(true);
                addFriendImageView.setVisibility(View.GONE);
                requestsImageView.setVisibility(View.VISIBLE);
                settingsFragment = new SettingsFragment();
                user = UserDatabase.getInstance(getContext()).getUserDao().getDatas();
                Log.d("onCreateView", "onCreateView: "+ user.getFirstname()+ user.getCity() +user.getBirthday());
                nameTextView.setText(user.getFirstname()+" "+ user.getLastname());
                cityTextView.setText(user.getCity());
                String birthday = user.getBirthday();
                String [] birthdaySplit = birthday.split("/");
                ageTextView.setText(birthdaySplit[0]+"/"+birthdaySplit[1]);
                try{
                    File file = new File("/data/user/0/com.example.emin.findact/app_imageDir","profile.jpg" );
                    Bitmap b = BitmapFactory.decodeStream(new FileInputStream(file));
                    profilePictureImageView.setImageBitmap(b);
                } catch (Exception e){
                    e.printStackTrace();
                }
                break;
           case INIT_MODE_FRIEND_PROFILE_PAGE:
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
                   case FirebaseDBHelper.FRIEND_REQUEST_STATUS_WAITING:
                       addFriendImageView.setImageResource(R.drawable.undo_foolow_request);
                       break;
               }
               Picasso.get().load(userData.getProfilePictureUri()).into(profilePictureImageView);
               nameTextView.setText(userData.getFirstname()+" "+userData.getLastname());
               cityTextView.setText(userData.getCity());
               String [] birthdaySplit1 = userData.getBirthdate().split("/");
               ageTextView.setText(birthdaySplit1[0]+"/"+birthdaySplit1[1]);
               break;
       }
        return v;
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getContext(), LoginActivity.class);
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
                        firebaseDBHelper.sendFollowRequest(userData.getUsername());
                        addFriendImageView.setImageResource(R.drawable.undo_foolow_request);
                        requestStatus = FirebaseDBHelper.FRIEND_REQUEST_STATUS_WAITING;
                        break;
                    case FirebaseDBHelper.FRIEND_REQUEST_STATUS_WAITING:
                        firebaseDBHelper.undoFollowRequest(userData.getUsername());
                        addFriendImageView.setImageResource(R.drawable.send_follow_request);
                        requestStatus = FirebaseDBHelper.FRIEND_REQUEST_STATUS_UNFOLLOWED;
                        break;
                    case FirebaseDBHelper.FRIEND_REQUEST_STATUS_ACCEPTED:
                        firebaseDBHelper.unfollowUser(userData.getUsername());
                        addFriendImageView.setImageResource(R.drawable.send_follow_request);
                        requestStatus = FirebaseDBHelper.FRIEND_REQUEST_STATUS_UNFOLLOWED;
                        break;
                }

                break;

            case R.id.fragment_profile_following_tv:
                progressDialog.show();
                firebaseDBHelper.getFollowing(currentUsername, friendsArrayList, statusList);
                showListDialog("Following");
                break;

            case R.id.fragment_profile_followers_tv:
                progressDialog.show();
                firebaseDBHelper.getFollowers(currentUsername, friendsArrayList, statusList);
                showListDialog("Followers");
                break;


            case R.id.fragment_profile_requests_iv:
                progressDialog.show();
                firebaseDBHelper.getFollowRequests(friendsArrayList, statusList);
                showListDialog("Requests");
                break;
        }
    }

    private void showListDialog(final String dialogTitle){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Bundle bundle = new Bundle();
                bundle.putString("Title",dialogTitle);
                Bundle followersArrayListBundle = new Bundle();
                for(int i = 0; i < friendsArrayList.size(); i++){
                    followersArrayListBundle.putBundle(String.valueOf(i),friendsArrayList.get(i).UserDatatoBundle());

                }
                bundle.putBundle("UserDataArrayList",followersArrayListBundle);
                bundle.putIntegerArrayList("StatusArrayList",statusList);
                progressDialog.dismiss();
                listDialog.setArguments(bundle);
                listDialog.show(getActivity().getSupportFragmentManager(),"dialog");
            }
        },800);
    }
    public static void dismissListDialog(){
        if(listDialog != null && listDialog.isAdded()) {
            listDialog.dismiss();
        }
    }
}
