package com.example.emin.findact.APIs;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class IGDbAPI {

    private static String TAG = "IGDAPI";


    private String fields = "&fields=name,genres.name,platforms.alternative_name,first_release_date,summary,cover.image_id,game_modes.name,similar_games.name,total_rating,videos.video_id";
    private String searchUrl = "https://api-v3.igdb.com/games/?search="; //+game_name+fields
    private String user_key = "user-key";
    private String key = "2bd597197d5320a9a882cac6123f52eb";
    private String content_type = "Content-Type";
    private String dataFormat = "application/json; charset=UTF-8";

    private ArrayList<String> game_mode_name_list;
    private ArrayList<String> genre_list;
    private ArrayList<String> platform_list;

    public void searchGame(String game_name, final ArrayList<GameModel> gameModelArrayList){

    gameModelArrayList.clear();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(searchUrl+game_name+fields)
                .addHeader(user_key, key)
                .addHeader(content_type, dataFormat)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: "+e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String data = response.body().string();
                Log.d(TAG, "onResponse: "+data);
                try {
                    JSONArray jsonArray = new JSONArray(data);
                    for (int i = 0; i < jsonArray.length(); i++){
                        game_mode_name_list = new ArrayList<>();
                        genre_list = new ArrayList<>();
                        platform_list = new ArrayList<>();

                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        // for video_id
                        String videos = jsonObject.getString("videos");
                        JSONArray videoArray = new JSONArray(videos);
                        JSONObject videoObject = videoArray.getJSONObject(0); // for ile yapılabilir.
                        String video_id = videoObject.getString("video_id");

                        // for image_id
                        String cover = jsonObject.getString("cover");
                        JSONObject imageObject = new JSONObject(cover);
                        String image_id = imageObject.getString("image_id");

                        // for game mode list
                        String game_modes = jsonObject.getString("game_modes");
                        JSONArray game_modes_Array = new JSONArray(game_modes);
                        for (int x = 0; x < game_modes_Array.length(); x++){
                            JSONObject game_modes_Object = game_modes_Array.getJSONObject(x);
                            if (game_modes_Object.has("name")){
                                String game_mode_name = game_modes_Object.getString("name");
                                game_mode_name_list.add(game_mode_name);
                            }
                        }

                        // for genre list
                        String genres = jsonObject.getString("genres");
                        JSONArray genreArray = new JSONArray(genres);
                        for (int x = 0; x < genreArray.length(); x++){
                            JSONObject genresObject = genreArray.getJSONObject(x);
                            if (genresObject.has("name")){
                                String genre_name = genresObject.getString("name");
                                genre_list.add(genre_name);
                            }
                        }

                        // for platform list
                        String platforms = jsonObject.getString("platforms");
                        JSONArray platformsArray = new JSONArray(platforms);
                        for (int x = 0; x < platformsArray.length(); x++){
                            JSONObject platformsObject = platformsArray.getJSONObject(x);
                            if (platformsObject.has("alternative_name")){
                                String platform_name = platformsObject.getString("alternative_name");
                                platform_list.add(platform_name);
                            }
                        }

                        int gameId = jsonObject.getInt("id");
                        String game_name = jsonObject.getString("name");
                        // release date
                        Long release_date_stamp = jsonObject.getLong("first_release_date");
                        Date date = new Date(release_date_stamp*1000L);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
                        String release_date = simpleDateFormat.format(date);
                        //
                        String summary = jsonObject.getString("summary");
                        Double rating = jsonObject.getDouble("total_rating");


                        GameModel gameModel = new GameModel(gameId,game_name, genre_list, release_date, summary, image_id, game_mode_name_list,
                                rating ,platform_list, video_id);

                        gameModelArrayList.add(gameModel);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
