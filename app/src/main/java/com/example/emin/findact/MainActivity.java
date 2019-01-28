package com.example.emin.findact;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    final String TAG = "MainActivity";

    ProgressDialog progressDialog;
    HomeFragment homeFragment;
    FindFragment findFragment;
    ExploreFragment exploreFragment;
    ProfileFragment profileFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(MainActivity.this);
        homeFragment = new HomeFragment();
        findFragment = new FindFragment();
        exploreFragment = new ExploreFragment();
        profileFragment = new ProfileFragment();

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
        navigation.setSelectedItemId(R.id.navigation_home);

    }


    private void setMainFragment(final Fragment fragment){

        new Thread(new Runnable() {
            @Override
            public void run() {
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_frame,fragment);
                fragmentTransaction.commit();
            }
        }).start();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        },800);


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        progressDialog.show();

        switch (item.getItemId()) {
            case R.id.navigation_home:
                setMainFragment(homeFragment);
                return true;
            case R.id.navigation_explore:
                setMainFragment(exploreFragment);
                return true;
            case R.id.navigation_find:
                setMainFragment(findFragment);
                return true;
            case R.id.navigation_profile:
                profileFragment.setInitMode(ProfileFragment.INIT_MODE_MY_PROFILE_PAGE);
                setMainFragment(profileFragment);
                return true;
        }
        return false;
    }
}
