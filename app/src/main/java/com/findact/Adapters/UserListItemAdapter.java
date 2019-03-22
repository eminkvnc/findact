package com.example.emin.findact.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.emin.findact.Firebase.FirebaseDBHelper;
import com.example.emin.findact.Firebase.UserData;
import com.example.emin.findact.ProfileFragment;
import com.example.emin.findact.R;
import com.example.emin.findact.UsersListDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserListItemAdapter extends RecyclerView.Adapter<UserListItemAdapter.UserListItemViewHolder> {

    private String TAG = "UserListItemAdapter";
    private String adapterCreatorTag;
    private FirebaseDBHelper firebaseDBHelper;
    private ArrayList<UserData> userDataArrayList;
    private ArrayList<Integer> requestStatus;
    private ArrayList<String> invitedUserList;
    private Context context;


    public UserListItemAdapter(Context context, ArrayList<UserData> userDataArrayList, ArrayList<Integer> requestStatus, String adapterCreatorTag) {
        this.context = context;
        this.userDataArrayList = userDataArrayList;
        this.requestStatus = requestStatus;
        this.adapterCreatorTag = adapterCreatorTag;
        firebaseDBHelper = FirebaseDBHelper.getInstance();
        invitedUserList = new ArrayList<>();
    }

    public void setInvitedUserList(ArrayList<String> invitedUserList) {
        this.invitedUserList = invitedUserList;
    }

    @NonNull
    @Override
    public UserListItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_user,viewGroup,false);
        return new UserListItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final UserListItemViewHolder userListItemViewHolder, int position) {


        UserData userData = userDataArrayList.get(position);
        CustomListener listener = new CustomListener(userListItemViewHolder,position, userData);
        userListItemViewHolder.addFriendImageView.setOnClickListener(listener);
        userListItemViewHolder.acceptFriendImageView.setOnClickListener(listener);
        userListItemViewHolder.declineFriendImageView.setOnClickListener(listener);
        userListItemViewHolder.inviteFriendImageView.setOnClickListener(listener);
        userListItemViewHolder.cardView.setOnClickListener(listener);

        Picasso.get()
                .load(userData.getProfilePictureUri())
                .resize(50,50)
                .into(userListItemViewHolder.profilePictureImageView);

        String name = userData.getFirstname() + " " + userData.getLastname();
        userListItemViewHolder.nameTextView.setText(name);
        if(requestStatus != null) {
            if (!requestStatus.isEmpty()) {
                userListItemViewHolder.acceptFriendImageView.setVisibility(View.GONE);
                userListItemViewHolder.declineFriendImageView.setVisibility(View.GONE);
                userListItemViewHolder.inviteFriendImageView.setVisibility(View.GONE);
                userListItemViewHolder.addFriendImageView.setVisibility(View.VISIBLE);
                switch (requestStatus.get(position)) {
                    case FirebaseDBHelper.FRIEND_REQUEST_STATUS_UNFOLLOWED:
                        userListItemViewHolder.addFriendImageView.setImageResource(R.drawable.send_follow_request);
                        break;
                    case FirebaseDBHelper.FRIEND_REQUEST_STATUS_REQUEST_WAITING:
                        userListItemViewHolder.addFriendImageView.setImageResource(R.drawable.undo_foolow_request);
                        break;
                    //already your friend you can remove your friends
                    case FirebaseDBHelper.FRIEND_REQUEST_STATUS_ACCEPTED:
                        userListItemViewHolder.addFriendImageView.setImageResource(R.drawable.unfollow_user);
                        break;
                    case FirebaseDBHelper.FRIEND_REQUEST_STATUS_WAITING_ANSWER:
                        userListItemViewHolder.addFriendImageView.setImageResource(R.drawable.unfollow_user);
                        userListItemViewHolder.acceptFriendImageView.setVisibility(View.VISIBLE);
                        userListItemViewHolder.declineFriendImageView.setVisibility(View.VISIBLE);
                        userListItemViewHolder.addFriendImageView.setVisibility(View.GONE);
                        userListItemViewHolder.inviteFriendImageView.setVisibility(View.GONE);
                        break;
                    case FirebaseDBHelper.FRIEND_REQUEST_STATUS_NONE:
                        userListItemViewHolder.acceptFriendImageView.setVisibility(View.GONE);
                        userListItemViewHolder.declineFriendImageView.setVisibility(View.GONE);
                        userListItemViewHolder.addFriendImageView.setVisibility(View.GONE);
                        userListItemViewHolder.inviteFriendImageView.setVisibility(View.GONE);
                        break;
                }
            }
        }
        else{
            userListItemViewHolder.acceptFriendImageView.setVisibility(View.GONE);
            userListItemViewHolder.declineFriendImageView.setVisibility(View.GONE);
            userListItemViewHolder.addFriendImageView.setVisibility(View.GONE);
            userListItemViewHolder.inviteFriendImageView.setVisibility(View.VISIBLE);
            userListItemViewHolder.cardView.setOnClickListener(null);
        }
        if(invitedUserList == null){
            userListItemViewHolder.inviteFriendImageView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return userDataArrayList.size();
    }


    class UserListItemViewHolder extends RecyclerView.ViewHolder {
        //View are initializing
        ImageView profilePictureImageView;
        TextView nameTextView;
        ImageView addFriendImageView;
        ImageView acceptFriendImageView;
        ImageView declineFriendImageView;
        ImageView inviteFriendImageView;
        CardView cardView;
        UserListItemViewHolder(View v) {
            super(v);
            profilePictureImageView = v.findViewById(R.id.list_item_user_profile_iv);
            nameTextView = v.findViewById(R.id.list_item_user_name_tv);
            addFriendImageView = v.findViewById(R.id.list_item_user_add_friend_iv);
            acceptFriendImageView = v.findViewById(R.id.list_item_user_accept_friend_iv);
            declineFriendImageView = v.findViewById(R.id.list_item_user_decline_friend_iv);
            inviteFriendImageView = v.findViewById(R.id.list_item_user_invite_iv);
            cardView = v.findViewById(R.id.list_item_user_cv);
        }
    }

    class CustomListener implements View.OnClickListener {

        private UserListItemViewHolder userListItemViewHolder;
        private int position;
        private UserData userData;

        CustomListener(UserListItemViewHolder userListItemViewHolder, int position, UserData userData) {
            this.userListItemViewHolder = userListItemViewHolder;
            this.position = position;
            this.userData = userData;
        }

        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.list_item_user_add_friend_iv:
                    switch(requestStatus.get(position)){
                        case FirebaseDBHelper.FRIEND_REQUEST_STATUS_UNFOLLOWED:
                            firebaseDBHelper.sendFollowRequest(userData.getUuidString());
                            userListItemViewHolder.addFriendImageView.setImageResource(R.drawable.undo_foolow_request);
                            requestStatus.set(position,FirebaseDBHelper.FRIEND_REQUEST_STATUS_REQUEST_WAITING);
                            break;
                        case FirebaseDBHelper.FRIEND_REQUEST_STATUS_REQUEST_WAITING:
                            firebaseDBHelper.undoFollowRequest(userData.getUuidString());
                            userListItemViewHolder.addFriendImageView.setImageResource(R.drawable.send_follow_request);
                            requestStatus.set(position,FirebaseDBHelper.FRIEND_REQUEST_STATUS_UNFOLLOWED);
                            break;
                        case FirebaseDBHelper.FRIEND_REQUEST_STATUS_ACCEPTED:
                            firebaseDBHelper.unfollowUser(userData.getUuidString());
                            if(adapterCreatorTag.equals(UsersListDialog.TAG)) {
                                userDataArrayList.remove(position);
                                requestStatus.remove(position);
                            }
                            userListItemViewHolder.addFriendImageView.setImageResource(R.drawable.send_follow_request);
                            break;
                    }
                    notifyDataSetChanged();
                    break;

                case R.id.list_item_user_accept_friend_iv:
                    firebaseDBHelper.acceptFollowRequest(userData.getUuidString());
                    userDataArrayList.remove(position);
                    requestStatus.remove(position);
                    notifyDataSetChanged();
                    break;

                case R.id.list_item_user_decline_friend_iv:
                    firebaseDBHelper.declineFollowRequest(userData.getUuidString());
                    userDataArrayList.remove(position);
                    requestStatus.remove(position);
                    notifyDataSetChanged();

                    break;

                case R.id.list_item_user_invite_iv:
                    //Firebase request yolla. Davet edilen her kullanıcının profiline etkinliği ekle.
                    invitedUserList.add(userData.getUuidString());
                    userListItemViewHolder.inviteFriendImageView.setVisibility(View.GONE);
                    break;

                case R.id.list_item_user_cv:
                    //profil sayfasına geç
                    if(UsersListDialog.getInstance().isAdded()) {
                        UsersListDialog.getInstance().dismiss();
                    }

                    ProfileFragment profileFragment = new ProfileFragment();
                    Bundle bundle = new Bundle();
                    bundle.putBundle("UserData",userData.UserDatatoBundle());
                    bundle.putInt("RequestStatus",requestStatus.get(position));
                    profileFragment.setArguments(bundle);
                    if(userData.getUuidString().equals(firebaseDBHelper.getCurrentUser())){
                        profileFragment.setInitMode(ProfileFragment.INIT_MODE_MY_PROFILE_PAGE);
                    }else {
                        profileFragment.setInitMode(ProfileFragment.INIT_MODE_FRIEND_PROFILE_PAGE);
                    }
                    FragmentManager fragmentManager = ((FragmentActivity)context).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.main_frame, profileFragment);
                    fragmentTransaction.commit();
                    break;

            }
        }
    }

}
