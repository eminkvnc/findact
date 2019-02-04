package com.example.emin.findact;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class FindFragment extends Fragment {

    private View v;
    DisplayActivityFragment displayActivityFragment;

    public FindFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(R.string.title_find);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        displayActivityFragment = new DisplayActivityFragment();

        v = inflater.inflate(R.layout.fragment_find,container,false);
        Button movieButton = v.findViewById(R.id.movie_btn);
        Button gameButton = v.findViewById(R.id.game_btn);
        Button groupButton = v.findViewById(R.id.group_btn);


        movieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                displayActivityFragment.setInitMode(DisplayActivityFragment.INIT_MODE_MOVIE_ACTIVITY);
                setFindFragment(displayActivityFragment);
            }
        });
        gameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayActivityFragment.setInitMode(DisplayActivityFragment.INIT_MODE_GAME_ACTIVITY);
                setFindFragment(displayActivityFragment);
            }
        });
        groupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayActivityFragment.setInitMode(DisplayActivityFragment.INIT_MODE_GROUP_ACTIVITY);
                setFindFragment(displayActivityFragment);
            }
        });


        return v;
    }



    private void setFindFragment(final Fragment fragment){

        new Thread(new Runnable() {
            @Override
            public void run() {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.main_frame,fragment);
                fragmentTransaction.commit();
            }
        }).start();

    }


}
