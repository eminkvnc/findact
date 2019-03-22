package com.example.emin.findact.Adapters;

import android.content.Context;
import android.net.Uri;
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

import com.example.emin.findact.APIs.GameModel;
import com.example.emin.findact.DisplayActivityFragment;
import com.example.emin.findact.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GameListItemAdapter extends RecyclerView.Adapter<GameListItemAdapter.GameListItemViewHolder> {

    private ArrayList<GameModel> gameModelArrayList;
    private Context context;
    private String TAG = "GameListItemAdapter";

    public GameListItemAdapter(Context context, ArrayList<GameModel> gameModelArrayList){

        this.context = context;
        this.gameModelArrayList = gameModelArrayList;
    }

    @NonNull
    @Override
    public GameListItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item_game, viewGroup,false);
        return new GameListItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameListItemViewHolder gameListItemViewHolder, int position) {

        GameModel gameModel = gameModelArrayList.get(position);
        CustomListener customListener = new CustomListener(gameModel );
        gameListItemViewHolder.cardView.setOnClickListener(customListener);

        gameListItemViewHolder.name.setText(gameModel.getName());
        gameListItemViewHolder.releaseDate.setText(gameModel.getReleaseDate());

        if (gameModel.getImageId() != null){
            Picasso.get().load(Uri.parse("https://images.igdb.com/igdb/image/upload/t_cover_big/"+gameModel.getImageId()+".jpg")).into(gameListItemViewHolder.poster);
        } else {
            gameListItemViewHolder.poster.setImageResource(R.drawable.default_game);
        }
    }

    @Override
    public int getItemCount() {
        return gameModelArrayList.size();
    }


    // View Holder
    class GameListItemViewHolder extends RecyclerView.ViewHolder {

        ImageView poster;
        TextView name;
        TextView releaseDate;
        CardView cardView;

        GameListItemViewHolder(@NonNull View view) {

            super(view);
            cardView = view.findViewById(R.id.list_item_game_cardview);
            poster = view.findViewById(R.id.list_item_game_poster);
            name = view.findViewById(R.id.list_item_game_title);
            releaseDate = view.findViewById(R.id.list_item_game_releaseDate);
        }
    }

    // Custom Listener
    class CustomListener implements View.OnClickListener{

        private GameModel gameModel;
        DisplayActivityFragment displayActivityFragment;


        CustomListener( GameModel gameModel) {

            this.gameModel = gameModel;
        }

        @Override
        public void onClick(View view) {

            if (view.getId() == R.id.list_item_game_cardview) {

                displayActivityFragment = new DisplayActivityFragment();

                Log.d(TAG, "onClick: "+gameModel.getName());
                Bundle bundle = new Bundle();
                bundle.putBundle("GameData" ,gameModel.GameDataToBundle());

                displayActivityFragment.setArguments(bundle);
                displayActivityFragment.setInitMode(DisplayActivityFragment.INIT_MODE_GAME_ACTIVITY);

                FragmentManager fragmentManager = ((FragmentActivity)context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.main_frame,displayActivityFragment);
                fragmentTransaction.commit();

            }
        }
    }
}
