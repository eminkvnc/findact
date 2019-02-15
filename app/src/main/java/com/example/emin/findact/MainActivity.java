package com.example.emin.findact;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    final String TAG = "MainActivity";

    public ProgressDialog progressDialog;
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

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionBarColor)));

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
        navigation.setSelectedItemId(R.id.navigation_explore);

    }


    private void setMainFragment(Fragment fragment){
                getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_frame,fragment);
                fragmentTransaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        //progressDialog.show();

        switch (item.getItemId()) {
            case R.id.navigation_home:
                //setMainFragment(homeFragment);
                Intent intent = new Intent(getApplicationContext(),GetUserDetailActivity.class);
                startActivity(intent);
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

    boolean doubleBackTab = false;

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int count = fragmentManager.getBackStackEntryCount();
        if (count > 0)//İf user is using inner screens back button works,otherwise ask to close app.
            super.onBackPressed();
        else {
            if (doubleBackTab) {
                super.onBackPressed();
                finishAffinity();
            } else {
                Toast.makeText(this, "Çıkmak için geri tuşuna iki defa basınız.", Toast.LENGTH_SHORT).show();
                doubleBackTab = true;
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackTab = false;
                    }
                }, 500);
            }
        }
    }
}
