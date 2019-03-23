package com.findact.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.findact.R;
import com.findact.RoomDatabase.Post;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class OfflinePostListItemAdapter extends RecyclerView.Adapter<OfflinePostListItemAdapter.OfflinePostListItemViewHolder> {

    private Context context;
    private List<Post> postList;

    public OfflinePostListItemAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = new ArrayList<>(postList);
    }

    @NonNull
    @Override
    public OfflinePostListItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_home_page,viewGroup,false );
        return new OfflinePostListItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfflinePostListItemViewHolder offlinePostListItemViewHolder, int i) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm' 'dd.MM.yyyy");

        ArrayList<Long> shareDateList = new ArrayList<>();

        for (int a = 0; a < postList.size(); a++){
            shareDateList.add(postList.get(a).getLogDate());
        }

        Collections.sort(shareDateList, Collections.<Long>reverseOrder());


        for (int p = 0; p < postList.size(); p++){
            if (shareDateList.get(i).equals(postList.get(p).getLogDate())){
                Post post = postList.get(p);

                Bitmap postImage = BitmapFactory.decodeByteArray(post.getPicturePath(),0 ,post.getPicturePath().length );
                offlinePostListItemViewHolder.postImage.setImageBitmap(postImage);

                Bitmap userImage = BitmapFactory.decodeByteArray(post.getSenderImage(),0 ,post.getSenderImage().length );
                offlinePostListItemViewHolder.userPicture.setImageBitmap(userImage);

                Date date = new Date(post.getLogDate());
                offlinePostListItemViewHolder.shareDate.setText(simpleDateFormat.format(date));
                offlinePostListItemViewHolder.category.setText(post.getActivityType());
                offlinePostListItemViewHolder.postTitle.setText(post.getTitle());
                offlinePostListItemViewHolder.username.setText(post.getSenderName());
                offlinePostListItemViewHolder.date.setText(post.getReleaseDate());
                offlinePostListItemViewHolder.rating.setText(new DecimalFormat("##.#").format(post.getRating()));

            }

        }

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }



    class OfflinePostListItemViewHolder extends RecyclerView.ViewHolder {
        ImageView userPicture;
        TextView username;
        ImageView postImage;
        TextView postTitle;
        TextView date;
        TextView rating;
        TextView category;
        TextView shareDate;

        OfflinePostListItemViewHolder(@NonNull View view) {
            super(view);
            userPicture = view.findViewById(R.id.list_item_home_page_user_iv);
            username = view.findViewById(R.id.list_item_home_page_username_tv);
            postImage = view.findViewById(R.id.list_item_home_page_post_iv);
            postTitle = view.findViewById(R.id.list_item_home_page_post_title);
            date = view.findViewById(R.id.list_item_home_page_release_date_tv);
            rating = view.findViewById(R.id.list_item_home_page_rating_tv);
            category = view.findViewById(R.id.list_item_home_page_post_category_tv);
            shareDate = view.findViewById(R.id.list_item_home_page_post_share_date_tv);
        }
    }
}
