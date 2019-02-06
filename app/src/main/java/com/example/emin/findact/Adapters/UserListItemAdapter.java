package com.example.emin.findact.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.emin.findact.Firebase.FirebaseDBHelper;
import com.example.emin.findact.Firebase.UserData;
import com.example.emin.findact.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserListItemAdapter extends BaseAdapter {

    FirebaseDBHelper firebaseDBHelper;
    ArrayList<UserData> userDataArrayList;
    ArrayList<String> requestStatus;
    Context context;


    public UserListItemAdapter(Context context, ArrayList<UserData> userDataArrayList, ArrayList<String> requestStatus) {
        this.context = context;
        this.userDataArrayList = userDataArrayList;
        this.requestStatus = requestStatus;
        firebaseDBHelper = FirebaseDBHelper.getInstance();
    }

    @Override
    public int getCount() {
        return userDataArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return userDataArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final UserData userData = userDataArrayList.get(position);

        View v = View.inflate(context, R.layout.list_item_user, null);
        ImageView profilePictureImageView = v.findViewById(R.id.list_item_user_profile_iv);
        TextView nameTextView = v.findViewById(R.id.list_item_user_name_tv);
        final ImageView addFriendImageView = v.findViewById(R.id.list_item_user_add_friend_iv);
        final ImageView acceptFriendImageView = v.findViewById(R.id.list_item_user_accept_friend_iv);
        //requests came from search list in findFragment
        if(requestStatus != null) {
            switch (requestStatus.get(position)) {
                case "none":
                    addFriendImageView.setImageResource(R.drawable.ic_add_friend);
                    break;
                case "waiting":
                    addFriendImageView.setImageResource(R.drawable.edit);
                    break;
                //already your friend you can remove your friends
                case "accepted":
                    addFriendImageView.setImageResource(R.drawable.delete);
                    break;
            }
            addFriendImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    switch (requestStatus.get(position)) {
                        //send a new friend request.
                        case "none":
                            firebaseDBHelper.sendFriendRequest(userData.getUsername());
                            addFriendImageView.setImageResource(R.drawable.edit);
                            requestStatus.set(position, "waiting");
                            break;
                        //undo non-accepted request.
                        case "waiting":
                            firebaseDBHelper.undoFriendRequest(userData.getUsername());
                            addFriendImageView.setImageResource(R.drawable.ic_add_friend);
                            requestStatus.set(position, "none");
                            break;
                        //already your friend. you can remove in your friends.
                        case "accepted":
                            firebaseDBHelper.removeFriend(userData.getUsername());
                            addFriendImageView.setImageResource(R.drawable.delete);
                            requestStatus.set(position, "none");
                            break;
                    }
                    notifyDataSetChanged();
                }
            });
        }
        //requests waiting for accept or decline in profileFragment.
        //no need to request status because they always in waiting status.
        else{
            acceptFriendImageView.setVisibility(View.VISIBLE);
            acceptFriendImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    firebaseDBHelper.acceptFriendRequest(userData.getUsername());
                    userDataArrayList.remove(position);
                    notifyDataSetChanged();
                }
            });
            addFriendImageView.setImageResource(R.drawable.cancel);
            addFriendImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    firebaseDBHelper.declineFriendRequest(userData.getUsername());
                    userDataArrayList.remove(position);
                    notifyDataSetChanged();
                }
            });

        }
        Picasso.get().load(userData.getProfilePictureUri()).into(profilePictureImageView);
        nameTextView.setText(userData.getFirstname() + " " + userData.getLastname());

        return v;
    }
}
