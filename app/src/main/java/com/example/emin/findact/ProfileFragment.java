package com.example.emin.findact;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ProfileFragment extends Fragment {


    public static final int INIT_MODE_MY_PROFILE_PAGE = 0;
    public static final int INIT_MODE_FRIEND_PROFILE_PAGE = 1;

    private int initMode;
    private View v;

    private SettingsFragment settingsFragment;


    public ProfileFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_profile,container,false);
        ImageView settingsIconImageView = v.findViewById(R.id.fragment_profile_settings_or_add_iv);
        ImageView profilePicture = v.findViewById(R.id.fragment_settings_picture_iv);


        settingsFragment = new SettingsFragment();

        switch (initMode){
            case INIT_MODE_MY_PROFILE_PAGE:
                settingsIconImageView.setImageResource(R.drawable.ic_settings);
                settingsIconImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.replace(R.id.main_frame,settingsFragment);
                                fragmentTransaction.commit();
                            }
                        }).start();
                    }
                });
                break;
            case INIT_MODE_FRIEND_PROFILE_PAGE:
                settingsIconImageView.setImageResource(R.drawable.ic_add_friend);
                break;
        }
        return v;
    }

    public int getInitMode() {
        return initMode;
    }

    public void setInitMode(int initMode) {
        this.initMode = initMode;
    }

}
