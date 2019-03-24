package com.findact;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import com.findact.Adapters.UserListItemAdapter;
import com.findact.Firebase.UserData;
import java.util.ArrayList;


public class UsersListDialog extends DialogFragment {

    static UsersListDialog dialog;

    public static UsersListDialog getInstance(){
        if(dialog == null){
            dialog = new UsersListDialog();
        }
        return dialog;
    }

    public static String TAG = "UsersListDialog";
    private String title;
    private RecyclerView recyclerView;
    private UserListItemAdapter adapter;
    private ArrayList<UserData> userDataArrayList;
    private ArrayList<String> attendeesArrayList;
    private ArrayList<Integer> requestStatusArrayList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        userDataArrayList = new ArrayList<>();
        requestStatusArrayList = new ArrayList<>();
        attendeesArrayList = new ArrayList<>();
        Bundle bundle = getArguments();
        title = bundle.getString("Title");
        attendeesArrayList = bundle.getStringArrayList("Attendees");
        Bundle userDataBundle = bundle.getBundle("UserDataArrayList");
        if(userDataBundle.isEmpty()){
            Log.d(TAG, "onCreate: bundle Empty");
        }else{
            for(int i = 0; i < userDataBundle.size(); i++){
                userDataArrayList.add(new UserData(userDataBundle.getBundle(String.valueOf(i))));
            }
            requestStatusArrayList = bundle.getIntegerArrayList("StatusArrayList");
        }

        recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UserListItemAdapter(getContext(),userDataArrayList,requestStatusArrayList, TAG);
        adapter.setInvitedUserList(attendeesArrayList);
        recyclerView.setAdapter(adapter);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setView(recyclerView);
        return builder.create();
    }
}
