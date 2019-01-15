package com.example.emin.findact;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
        navigation.setSelectedItemId(R.id.navigation_home);


    }

    private void setFragment(Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_frame,fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                HomeFragment homeFragment = new HomeFragment();
                setFragment(homeFragment);
                return true;
            case R.id.navigation_explore:
                ExploreFragment exploreFragment = new ExploreFragment();
                setFragment(exploreFragment);
                return true;
            case R.id.navigation_find:
                ProfileFragment profileFragment1 = new ProfileFragment();
                profileFragment1.setInitMode(ProfileFragment.INIT_MODE_FRIEND_PROFILE_PAGE);
                setFragment(profileFragment1);
                return true;
            case R.id.navigation_profile:
                ProfileFragment profileFragment = new ProfileFragment();
                profileFragment.setInitMode(ProfileFragment.INIT_MODE_MY_PROFILE_PAGE);
                setFragment(profileFragment);
                return true;
        }
        return false;
    }
}
