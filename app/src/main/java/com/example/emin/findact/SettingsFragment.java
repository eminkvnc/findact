package com.example.emin.findact;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.style.TtsSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends Fragment {

    View v;

    public SettingsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_settings,container,false);
        ImageView signOutImageView = v.findViewById(R.id.fragment_settings_sign_out_iv);
        signOutImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });


        return v;
    }



    private void signOut(){

        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getContext(),LoginActivity.class);
        startActivity(intent);

    }

}
