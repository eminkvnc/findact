package com.example.emin.findact.APIs;

import android.os.AsyncTask;
import android.util.Log;
import com.example.emin.findact.OnTaskCompletedListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class IGDbAPI {

    private static String TAG = "IGDbAPI";


    private String fields = "&fields=name,genres.name,platforms.alternative_name,first_release_date,summary,cover.image_id,game_modes.name,total_rating,videos.video_id,popularity";
    private String searchUrl = "https://api-v3.igdb.com/games/?search="; //+game_name+fields
    private String user_key = "user-key";
    private String key = "2bd597197d5320a9a882cac6123f52eb";
    private String content_type = "Content-Type";
    private String dataFormat = "application/json; charset=UTF-8";

    private ArrayList<String> game_mode_name_list;
    private ArrayList<String> genre_list;
    private ArrayList<String> platform_list;

    public void search(String game_name, final ArrayList<GameModel> gameModelArrayList, final OnTaskCompletedListener listener) {

        gameModelArrayList.clear();
        DownloadData downloadData = new DownloadData(gameModelArrayList, listener);
        downloadData.execute(searchUrl+game_name + fields);

    }


    public class DownloadData extends AsyncTask<String, Void, String> {

        private ArrayList<GameModel> gameModelArrayList;
        private OnTaskCompletedListener listener;

        DownloadData(ArrayList<GameModel> gameModelArrayList, OnTaskCompletedListener listener) {
            this.gameModelArrayList = gameModelArrayList;
            this.listener = listener;
        }

        @Override
        protected String doInBackground(String... strings) {

            String result = "";
            URL url;
            HttpURLConnection httpURLConnection;

            try {
                url = new URL(strings[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestProperty(user_key, key);
                httpURLConnection.setRequestProperty(content_type, dataFormat);

                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                int data = inputStreamReader.read();

                while (data > 0) {
                    char ch = (char) data;
                    result += ch;
                    data = inputStreamReader.read();
                }
                Log.d(TAG, "doInBackground: " + result);
                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONArray jsonArray = new JSONArray(s);
                for (int i = 0; i < jsonArray.length(); i++) {
                    game_mode_name_list = new ArrayList<>();
                    genre_list = new ArrayList<>();
                    platform_list = new ArrayList<>();

                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    // for video_id

                    String video_id;
                    if (jsonObject.has("videos")) {
                        String videos = jsonObject.getString("videos");
                        JSONArray videoArray = new JSONArray(videos);

                        JSONObject videoObject = videoArray.getJSONObject(0);
                        video_id = videoObject.getString("video_id");

                    } else {
                        video_id = null;
                    }


                    // for image_id
                    String cover;
                    String image_id;
                    if (jsonObject.has("cover")) {
                        cover = jsonObject.getString("cover");
                        JSONObject imageObject = new JSONObject(cover);
                        image_id = imageObject.getString("image_id");
                    } else {
                        image_id = null;
                    }


                    // for game mode list
                    if (jsonObject.has("game_modes")) {
                        String game_modes = jsonObject.getString("game_modes");
                        JSONArray game_modes_Array = new JSONArray(game_modes);
                        for (int x = 0; x < game_modes_Array.length(); x++) {
                            JSONObject game_modes_Object = game_modes_Array.getJSONObject(x);
                            if (game_modes_Object.has("name")) {
                                String game_mode_name = game_modes_Object.getString("name");
                                game_mode_name_list.add(game_mode_name);
                            }
                        }
                    } else {
                        game_mode_name_list.add(null);
                    }


                    // for genre list

                    if (jsonObject.has("genres")) {
                        String genres = jsonObject.getString("genres");
                        JSONArray genreArray = new JSONArray(genres);
                        for (int x = 0; x < genreArray.length(); x++) {
                            JSONObject genresObject = genreArray.getJSONObject(x);
                            if (genresObject.has("name")) {
                                String genre_name = genresObject.getString("name");
                                genre_list.add(genre_name);
                            }
                        }
                    } else {
                        genre_list.add(null);
                    }


                    // for platform list
                    if (jsonObject.has("platforms")) {
                        String platforms = jsonObject.getString("platforms");
                        JSONArray platformsArray = new JSONArray(platforms);
                        for (int x = 0; x < platformsArray.length(); x++) {
                            JSONObject platformsObject = platformsArray.getJSONObject(x);
                            if (platformsObject.has("alternative_name")) {
                                String platform_name = platformsObject.getString("alternative_name");
                                platform_list.add(platform_name);
                            }
                        }
                    } else {
                        platform_list.add(null);
                    }


                    int gameId = jsonObject.getInt("id");
                    String gameName = jsonObject.getString("name");

                    // release date
                    long release_date_stamp;
                    String release_date;
                    if (jsonObject.has("first_release_date")) {
                        release_date_stamp = jsonObject.getLong("first_release_date");
                        Date date = new Date(release_date_stamp * 1000L);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
                        release_date = simpleDateFormat.format(date);
                    } else {
                        release_date = "NaN";
                    }

                    //
                    String summary;
                    if (jsonObject.has("summary")) {
                        summary = jsonObject.getString("summary");
                    } else {
                        summary = "NaN";
                    }


                    double rating;
                    if (jsonObject.has("total_rating")) {
                        rating = jsonObject.getDouble("total_rating");
                    } else {
                        rating = 0.0;
                    }

                    double popularity;
                    if (jsonObject.has("popularity")) {
                        popularity = jsonObject.getDouble("popularity");
                    } else {
                        popularity = 0.0;
                    }

                    GameModel gameModel = new GameModel(gameId, gameName, genre_list, release_date, summary, image_id, game_mode_name_list,
                            rating, platform_list, video_id, popularity);

                    gameModelArrayList.add(gameModel);
                }

                listener.onTaskCompleted();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
