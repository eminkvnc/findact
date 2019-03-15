package com.example.emin.findact.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.emin.findact.APIs.PostModel;
import com.example.emin.findact.DisplayActivityFragment;
import com.example.emin.findact.ProfileFragment;
import com.example.emin.findact.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostListItemAdapter extends RecyclerView.Adapter<PostListItemAdapter.PostListItemViewHolder> {


    private Context context;
    private ArrayList<PostModel> postModelArrayList;
    private String TAG = "PostListItemAdapter";

    public PostListItemAdapter(Context context, ArrayList<PostModel> postModelArrayList) {
        this.context = context;
        this.postModelArrayList = postModelArrayList;
    }

    @NonNull
    @Override
    public PostListItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_home_page,viewGroup,false );
        return new PostListItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostListItemViewHolder postListItemViewHolder, int i) {
        PostModel postModel = postModelArrayList.get(i);

        Picasso.get().load(postModel.getUserPicture()).into(postListItemViewHolder.userPicture);
        Picasso.get().load(postModel.getPostImageUri()).into(postListItemViewHolder.postImage);

        postListItemViewHolder.username.setText(postModel.getUsername());
        postListItemViewHolder.postTitle.setText(postModel.getPostTitle());
        postListItemViewHolder.date.setText(postModel.getDate());
        postListItemViewHolder.rating.setText(postModel.getRating());
        postListItemViewHolder.category.setText(postModel.getCategory());
        postListItemViewHolder.description.setText(postModel.getCategory());
    }

    @Override
    public int getItemCount() {
        return postModelArrayList.size();
    }

    class PostListItemViewHolder extends RecyclerView.ViewHolder {

        ImageView userPicture;
        TextView username;
        ImageView postImage;
        TextView postTitle;
        TextView date;
        TextView rating;
        TextView category;
        TextView description;
        CardView userPictureCardview;
        CardView postCardview;


        PostListItemViewHolder(@NonNull View view) {
            super(view);

            userPicture = view.findViewById(R.id.list_item_home_page_user_iv);
            username = view.findViewById(R.id.list_item_home_page_username_tv);
            postImage = view.findViewById(R.id.list_item_home_page_post_iv);
            postTitle = view.findViewById(R.id.list_item_home_page_post_title);
            date = view.findViewById(R.id.list_item_home_page_release_date_tv);
            rating = view.findViewById(R.id.list_item_home_page_rating_tv);
            category = view.findViewById(R.id.list_item_home_page_post_category_tv);
            description = view.findViewById(R.id.list_item_home_page_post_description_tv);
            userPictureCardview = view.findViewById(R.id.list_item_home_page_user_picture_cv);
            postCardview = view.findViewById(R.id.list_item_home_page_cv);

        }
    }

    class CustomListener implements View.OnClickListener{

        private PostModel postModel;
        DisplayActivityFragment displayActivityFragment;
        ProfileFragment profileFragment;

        CustomListener(PostModel postModel) {
            this.postModel = postModel;
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.list_item_home_page_cv){
                displayActivityFragment = new DisplayActivityFragment();
                Bundle bundle = new Bundle();
                bundle.putBundle("PostData",postModel.PostDataToBundle() );
                displayActivityFragment.setArguments(bundle);
                switch (postModel.getCategory()) {
                    case "Game":
                        displayActivityFragment.setInitMode(DisplayActivityFragment.INIT_MODE_GAME_ACTIVITY);
                        break;
                    case "Movie":
                        displayActivityFragment.setInitMode(DisplayActivityFragment.INIT_MODE_MOVIE_ACTIVITY);
                        break;
                    case "Group":
                        displayActivityFragment.setInitMode(DisplayActivityFragment.INIT_MODE_GROUP_ACTIVITY);
                        break;
                }
                FragmentManager fragmentManager = ((FragmentActivity)context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.main_frame,displayActivityFragment);
                fragmentTransaction.commit();
            } else if (view.getId() == R.id.list_item_home_page_user_picture_cv){
                profileFragment = new ProfileFragment();
                profileFragment.setInitMode(ProfileFragment.INIT_MODE_FRIEND_PROFILE_PAGE);
            }
        }
    }
}
