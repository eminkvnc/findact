package com.example.emin.findact;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DisplayActivityFragment extends Fragment {

    public static final int INIT_MODE_MOVIE_ACTIVITY = 0;
    public static final int INIT_MODE_GAME_ACTIVITY = 1;
    public static final int INIT_MODE_GROUP_ACTIVITY = 2;

    private int initMode;
    private View v;

    public DisplayActivityFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        switch (initMode){
            case INIT_MODE_MOVIE_ACTIVITY:
                v = inflater.inflate(R.layout.fragment_display_movie_activity,container,false);
                break;
            case INIT_MODE_GAME_ACTIVITY:
                v = inflater.inflate(R.layout.fragment_display_game_activity,container,false);
                break;
            case INIT_MODE_GROUP_ACTIVITY:
                v = inflater.inflate(R.layout.fragment_display_group_activity,container,false);
                break;
            default:
                v = null;
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
