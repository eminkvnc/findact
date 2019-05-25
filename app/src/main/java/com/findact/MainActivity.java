package com.findact;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.findact.APIs.IGDbAPI;
import com.findact.APIs.TMDbAPI;
import com.findact.Firebase.FirebaseDBHelper;
import com.findact.Firebase.UserData;
import com.findact.RoomDatabase.User;
import com.findact.RoomDatabase.UserDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    public static boolean isOnline;
    final String TAG = "MainActivity";
    public static String displayingFragment = "";
    public ProgressDialog progressDialog;
    HomeFragment homeFragment;
    FindFragment findFragment;
    ExploreFragment exploreFragment;
    ProfileFragment profileFragment;
    FirebaseUser firebaseUser;
    FirebaseDBHelper firebaseDBHelper;
    User user;

    public static NetworkStateBroadcastReciever reciever;

    TMDbAPI tmDbAPI;
    IGDbAPI igdbAPI;

    public static ArrayList<String> movieGenreList;
    public static ArrayList<String> gameGenreList;
    public static ArrayList<String> gameModeList;
    public static int id[];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(MainActivity.this);
        homeFragment = new HomeFragment();
        findFragment = new FindFragment();
        exploreFragment = new ExploreFragment();
        profileFragment = new ProfileFragment();

        gameGenreList = new ArrayList<>();
        movieGenreList = new ArrayList<>();
        gameModeList = new ArrayList<>();

        igdbAPI = new IGDbAPI();
        tmDbAPI = new TMDbAPI();

        id = new int[1];
        firebaseDBHelper = FirebaseDBHelper.getInstance();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionBarColor)));

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
        navigation.setSelectedItemId(R.id.navigation_home);
        progressDialog.show();

        NetworkStateBroadcastReciever.NetworkStateListener networkListener = new NetworkStateBroadcastReciever.NetworkStateListener() {
            @Override
            public void OnNetworkConnected() {
                isOnline = true;
            }

            @Override
            public void OnNetworkDisconnected() {
                isOnline = false;
            }
        };
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        reciever = new NetworkStateBroadcastReciever(networkListener);
        registerReceiver(reciever,intentFilter);
        reciever.onReceive(this,null);
        if(isOnline){
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

            tmDbAPI.getGenres(movieGenreList,null );
            igdbAPI.getGenres(gameGenreList,gameModeList ,null );
            firebaseDBHelper.getCurrentUserIntId(id, new OnTaskCompletedListener() {
                @Override
                public void onTaskCompleted() {

                }
            });
        } else {
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.toast_check_internet_connection), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }


    }


    private void setMainFragment(Fragment fragment){
                //getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.addToBackStack(null);
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

        if (displayingFragment.equals(DisplayActivityFragment.TAG )|| displayingFragment.equals(SettingsFragment.TAG))//İf user is using inner screens back button works,otherwise ask to close app.
            super.onBackPressed();
        else {
            if (doubleBackTab) {
                super.onBackPressed();
                finishAffinity();
            } else {
                Toast.makeText(this, getResources().getText(R.string.toast_tap_twice_to_exit), Toast.LENGTH_SHORT).show();
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(reciever);
    }

    public static void setDisplayingFragment(String displayingFragment) {
        MainActivity.displayingFragment = displayingFragment;
    }

}
