package com.findact.Adapters;

import android.content.Context;
import android.net.Uri;
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

import com.findact.APIs.PostModel;
import com.findact.DisplayActivityFragment;
import com.findact.Firebase.FirebaseDBHelper;
import com.findact.ProfileFragment;
import com.findact.R;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PostListItemAdapter extends RecyclerView.Adapter<PostListItemAdapter.PostListItemViewHolder> {


    private Context context;
    private ArrayList<PostModel> postModelArrayList;
//    private ArrayList<GameModel> gameModelArrayList;
//    private ArrayList<MovieModel> movieModelArrayList;
//    private ArrayList<ActivityModel> activityModelArrayList;
//    private ArrayList<UserData> userModelArrayList;
    private String TAG = "PostListItemAdapter";

//    public PostListItemAdapter(Context context, ArrayList<GameModel> gameModelArrayList,
//                               ArrayList<MovieModel> movieModelArrayList,
//                               ArrayList<ActivityModel> activityModelArrayList,
//                               ArrayList<UserData> userModelArrayList){
//
//        this.context = context;
//        this.gameModelArrayList = gameModelArrayList;
//        this.movieModelArrayList = movieModelArrayList;
//        this.activityModelArrayList = activityModelArrayList;
//        this.userModelArrayList = userModelArrayList;
//
//    }

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

        CustomListener customListener = new CustomListener(postModel);
        postListItemViewHolder.postCardview.setOnClickListener(customListener);
        postListItemViewHolder.userCardView.setOnClickListener(customListener);

        Date date = new Date(postModel.getShareDate());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm' 'dd.MM.yyyy");

        postListItemViewHolder.username.setText(postModel.getUserModel().getUsername());
        Picasso.get().load(postModel.getUserModel().getProfilePictureUri()).into(postListItemViewHolder.userPicture);
        postListItemViewHolder.shareDate.setText(simpleDateFormat.format(date));
        switch (postModel.getModelType()) {
            case "Game":

                Picasso.get().load(Uri.parse("https://images.igdb.com/igdb/image/upload/t_cover_big/"+postModel.getGameModel().getImageId()+".jpg")).into(postListItemViewHolder.postImage);

                postListItemViewHolder.postTitle.setText(postModel.getGameModel().getName());
                postListItemViewHolder.date.setText(postModel.getGameModel().getReleaseDate());
                if(postModel.getGameModel().getRating() != null) {
                    postListItemViewHolder.rating.setText(new DecimalFormat("##.#").format(postModel.getGameModel().getRating()));
                }else {
                    postListItemViewHolder.rating.setText(postModel.getGameModel().getRating().toString());
                }
                postListItemViewHolder.category.setText(postModel.getModelType());
                break;
            case "Movie":

                Picasso.get().load(Uri.parse("http://image.tmdb.org/t/p/w185/"+postModel.getMovieModel().getPoster_path())).into(postListItemViewHolder.postImage);

                postListItemViewHolder.postTitle.setText(postModel.getMovieModel().getTitle());
                postListItemViewHolder.date.setText(postModel.getMovieModel().getRelease_date());
                if(postModel.getMovieModel().getVote_average() != null) {
                    postListItemViewHolder.rating.setText(new DecimalFormat("##.#").format(Double.parseDouble(postModel.getMovieModel().getVote_average())));
                }else {
                    postListItemViewHolder.rating.setText(postModel.getMovieModel().getVote_average());
                }
                postListItemViewHolder.category.setText(postModel.getModelType());

                break;
            case "Activity":

                Picasso.get().load(postModel.getActivityModel().getImageUri()).into(postListItemViewHolder.postImage);
                postListItemViewHolder.postTitle.setText(postModel.getActivityModel().getName());
                postListItemViewHolder.date.setText(postModel.getActivityModel().getDate());
                postListItemViewHolder.rating.setText(new DecimalFormat("##.#").format(0));
                postListItemViewHolder.category.setText(postModel.getModelType());

                break;
        }
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
        TextView shareDate;
        CardView postCardview;
        CardView userCardView;


        PostListItemViewHolder(@NonNull View view) {
            super(view);

            userPicture = view.findViewById(R.id.list_item_home_page_user_iv);
            username = view.findViewById(R.id.list_item_home_page_username_tv);
            postImage = view.findViewById(R.id.list_item_home_page_post_iv);
            postTitle = view.findViewById(R.id.list_item_home_page_post_title);
            date = view.findViewById(R.id.list_item_home_page_release_date_tv);
            rating = view.findViewById(R.id.list_item_home_page_rating_tv);
            category = view.findViewById(R.id.list_item_home_page_post_category_tv);
            shareDate = view.findViewById(R.id.list_item_home_page_post_share_date_tv);
            postCardview = view.findViewById(R.id.list_item_home_page_cv);
            userCardView = view.findViewById(R.id.list_item_home_page_user_cv);

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
                switch (postModel.getModelType()) {
                    case "Game":
                        bundle.putBundle("GameData",postModel.getGameModel().GameDataToBundle());
                        displayActivityFragment.setArguments(bundle);
                        displayActivityFragment.setInitMode(DisplayActivityFragment.INIT_MODE_GAME_ACTIVITY);
                        break;
                    case "Movie":
                        bundle.putBundle("MovieData",postModel.getMovieModel().MovieDataToBundle());
                        displayActivityFragment.setArguments(bundle);
                        displayActivityFragment.setInitMode(DisplayActivityFragment.INIT_MODE_MOVIE_ACTIVITY);
                        break;
                    case "Activity":
                        bundle.putBundle("ActivityData",postModel.getActivityModel().activityDataToBundle());
                        displayActivityFragment.setArguments(bundle);
                        displayActivityFragment.setInitMode(DisplayActivityFragment.INIT_MODE_GROUP_ACTIVITY);
                        break;
                }
                FragmentManager fragmentManager = ((FragmentActivity)context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.main_frame,displayActivityFragment);
                fragmentTransaction.commit();
            }
            if (view.getId() == R.id.list_item_home_page_user_cv){
                profileFragment = new ProfileFragment();
                Bundle bundle = new Bundle();
                bundle.putBundle("UserData",postModel.getUserModel().UserDatatoBundle());
                bundle.putInt("RequestStatus",postModel.getRequestStatus());
                profileFragment.setInitMode(ProfileFragment.INIT_MODE_FRIEND_PROFILE_PAGE);
                profileFragment.setArguments(bundle);
                if(postModel.getUserModel().getUuidString().equals(FirebaseDBHelper.getInstance().getCurrentUser())){
                    profileFragment.setInitMode(ProfileFragment.INIT_MODE_MY_PROFILE_PAGE);
                }else {
                    profileFragment.setInitMode(ProfileFragment.INIT_MODE_FRIEND_PROFILE_PAGE);
                }
                FragmentManager fragmentManager = ((FragmentActivity)context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.main_frame, profileFragment);
                fragmentTransaction.commit();
            }
        }
    }
}
