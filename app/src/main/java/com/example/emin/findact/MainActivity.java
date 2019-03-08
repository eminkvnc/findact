package com.example.emin.findact;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.emin.findact.Firebase.FirebaseDBHelper;
import com.example.emin.findact.Firebase.UserData;
import com.example.emin.findact.RoomDatabase.User;
import com.example.emin.findact.RoomDatabase.UserDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    final String TAG = "MainActivity";

    public ProgressDialog progressDialog;
    HomeFragment homeFragment;
    FindFragment findFragment;
    ExploreFragment exploreFragment;
    ProfileFragment profileFragment;
    FirebaseUser firebaseUser;
    FirebaseDBHelper firebaseDBHelper;
    User user;

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
        navigation.setSelectedItemId(R.id.navigation_home);
        progressDialog.show();

        if (!isOnline()){
            Toast.makeText(getApplicationContext(), "Check your internet connection", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            navigation.setSelectedItemId(R.id.navigation_profile);
            profileFragment.setInitMode(ProfileFragment.INIT_MODE_MY_PROFILE_PAGE);
            setMainFragment(profileFragment);
        }

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String path = FirebaseDBHelper.FIREBASE_DB_CHILD_USERS+"/"+firebaseUser.getUid();
                if(!dataSnapshot.hasChild(path)){
                    progressDialog.dismiss();
                    Intent intent = new Intent(getApplicationContext(), GetUserDetailActivity.class);
                    startActivity(intent);
                }else{
                    firebaseDBHelper = FirebaseDBHelper.getInstance();
                    user = UserDatabase.getInstance(getApplicationContext()).getUserDao().getDatas(firebaseDBHelper.getCurrentUser());

                    final ArrayList<UserData> userDatas = new ArrayList<>();

                    if (user == null){
                        firebaseDBHelper.getUserData(firebaseDBHelper.getCurrentUser(),userDatas);

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                firebaseDBHelper.saveImageToLocal(getApplicationContext(),user,userDatas.get(0));
                            }
                        },500);
                        Handler handler1 = new Handler();
                        handler1.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                            }
                        },3000);
                    }
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

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

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }
}
