package com.example.emin.findact;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.emin.findact.RoomDatabase.User;
import com.example.emin.findact.RoomDatabase.UserDatabase;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileInputStream;

public class ProfileFragment extends Fragment {


    public static final int INIT_MODE_MY_PROFILE_PAGE = 0;
    public static final int INIT_MODE_FRIEND_PROFILE_PAGE = 1;

    private int initMode;
    private View v;

    ImageView profilePicture;
    TextView name, age, city;

    private SettingsFragment settingsFragment;
    User user;

    public static ProfileFragment getInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(user.getUsername());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.actionbar_for_profile,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.profile_fragment_actionbar_activities){

            // go activities activity or fragment

        } else if (item.getItemId() == R.id.profile_fragment_actionbar_friends){

            // go friends activity or fragment

        } else if (item.getItemId() == R.id.profile_fragment_actionbar_settings){
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.main_frame,settingsFragment);
            fragmentTransaction.commit();
        } else if (item.getItemId() == R.id.profile_fragment_actionbar_logout){
            signOut();
        } else if (item.getItemId() == R.id.profile_fragment_actionbar_edit_profile){
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.main_frame,settingsFragment);
            fragmentTransaction.commit();
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_profile,container,false);

        ImageView

//        ImageView settingsIconImageView = v.findViewById(R.id.fragment_profile_settings_or_add_iv);
        profilePicture = v.findViewById(R.id.fragment_profile_picture_iv);
        name = v.findViewById(R.id.fragment_profile_name_tv);
        age = v.findViewById(R.id.fragment_profile_age_tv);
        city = v.findViewById(R.id.fragment_profile_city_tv);

        settingsFragment = new SettingsFragment();

        user = UserDatabase.getInstance(getContext()).getUserDao().getDatas();

        Log.d("onCreateView", "onCreateView: "+ user.getFirstname()+ user.getCity() +user.getBirthday());

        name.setText(user.getFirstname()+" "+ user.getLastname());
        city.setText(user.getCity());

        String birthday = user.getBirthday();
        String [] birthdaySplit = birthday.split("/");
        age.setText(birthdaySplit[0]+"/"+birthdaySplit[1]);

        try{
            File file = new File("/data/user/0/com.example.emin.findact/app_imageDir","profile.jpg" );
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(file));
            profilePicture.setImageBitmap(b);
        } catch (Exception e){
            e.printStackTrace();
        }

//        switch (initMode){
////            case INIT_MODE_MY_PROFILE_PAGE:
////                settingsIconImageView.setImageResource(R.drawable.ic_settings);
////                settingsIconImageView.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View v) {
////                        new Thread(new Runnable() {
////                            @Override
////                            public void run() {
////                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
////                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
////                                fragmentTransaction.addToBackStack(null);
////                                fragmentTransaction.replace(R.id.main_frame,settingsFragment);
////                                fragmentTransaction.commit();
////                            }
////                        }).start();
////                    }
////                });
////                break;
//            case INIT_MODE_FRIEND_PROFILE_PAGE:
//                settingsIconImageView.setImageResource(R.drawable.ic_add_friend);
//                break;
//        }
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

}
