package com.example.emin.findact.APIs;

import android.os.AsyncTask;

import com.example.emin.findact.OnTaskCompletedListener;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;


public class IGDbAPI {

    String fields = "fields=name,genres.name,platforms.alternative_name,first_release_date,summary,cover.image_id,game_modes.name,total_rating,videos.video_id,popularity";
    String searchUrl = "https://api-v3.igdb.com/games/?search="; //+game_name+fields

    String limit_offset = "&limit=50&offset=";
    String order = "&order=popularity:desc";

    private static String user_key = "user-key";
    private static String key = "2bd597197d5320a9a882cac6123f52eb";
    private static String content_type = "Content-Type";
    private static String dataFormat = "application/json; charset=UTF-8";

    private static ArrayList<String> game_mode_name_list;
    private static ArrayList<String> genre_list;
    private static ArrayList<String> platform_list;

    private static int requestCount;


    public void getGenres(ArrayList<String> genreList, ArrayList<String> modeList){
        String genreurl = "https://api-v3.igdb.com/genres/?fields=name";
        String modeurl = "https://api-v3.igdb.com/game_modes/?fields=name";
        GetGenreList getGenreList = new GetGenreList(genreList);
        getGenreList.execute(genreurl);
        GetGenreList getModeList = new GetGenreList(modeList);
        getModeList.execute(modeurl);
    }

    public void searchGame(String game_name, final ArrayList<GameModel> gameModelArrayList, final OnTaskCompletedListener listener) {
        requestCount = 2;
        gameModelArrayList.clear();
        DownloadData downloadData = new DownloadData(gameModelArrayList, listener);
        downloadData.execute(searchUrl + game_name + "&" + fields);

    }

    public void searchByGenreAndModeName(ArrayList<String> selectedGenres, ArrayList<String> selectedMode,final ArrayList<GameModel> gameModelArrayList,final OnTaskCompletedListener listener){

        String genres;
        String mode;
        String url;
        String filterGenre = "&filter[genres.name][any]=";
        String filterMode = "&filter[game_modes.name][eq]=";



        // Genre seçilmiş ise
        if (selectedGenres.size()>0){
            genres = selectedGenres.get(0);
            for (int i = 1; i < selectedGenres.size(); i++){
                genres = genres+"\",\""+selectedGenres.get(i);
            }
            // mode seçilmiş ise
            if (selectedMode.size() > 0){
                String[] splitmode = selectedMode.get(0).split(" ");
                mode = splitmode[0];
                if (splitmode.length>1){
                    for (int i = 1; i < splitmode.length; i++){
                        mode = mode+"+"+splitmode[i];
                    }
                }
                url = "https://api-v3.igdb.com/games/?"+fields+filterGenre+"\""+genres+"\""+filterMode+"\""+mode+"\"";

            } else {    // mode seçilmemiş ise
                url = "https://api-v3.igdb.com/games/?"+fields+filterGenre+"\""+genres+"\"";
            }
        } else {    // genre seçilmemiş ise
            if (selectedMode.size() > 0){
                // seçilen mode "Co-operative" ise url ekleme. Çünkü request hata döndürüyor.
                if (selectedMode.get(0).equals("Co-operative")){
                    url = "https://api-v3.igdb.com/games/?"+fields;
                } else {    // seçilen mode "Co-operative" den farklı ise
                    String[] splitmode = selectedMode.get(0).split(" ");
                    mode = splitmode[0];
                    if (splitmode.length>1){
                        for (int i = 1; i < splitmode.length; i++){
                            mode = mode+"+"+splitmode[i];
                        }
                    }
                    url = "https://api-v3.igdb.com/games/?"+fields+filterMode+"\""+mode+"\"";
                }

            } else {    // mode seçilmemiş ise (bu zaten kullanılmayacak çünkü find fragmentta genre veya mode listeleri boş değilse arama yapabilecek)
                url = "https://api-v3.igdb.com/games/?"+fields;
            }
        }

        gameModelArrayList.clear();
        requestCount = 0;
        // Co-operative içeren requestler çalışmıyor.
        for (int i =0; i <=3; i++){
            DownloadData downloadData = new DownloadData(gameModelArrayList,listener );
            downloadData.execute(url + limit_offset + (i*50) + order);
        }
    }


    public static class DownloadData extends AsyncTask<String, Void, String> {

        private ArrayList<GameModel> gameModelArrayList;
        private final OnTaskCompletedListener listener;

        DownloadData(ArrayList<GameModel> gameModelArrayList, OnTaskCompletedListener listener) {
            this.gameModelArrayList = gameModelArrayList;
            this.listener = listener;
        }

        @Override
        protected String doInBackground(String... strings) {

            String result = "";
            URL url;
            HttpsURLConnection httpsURLConnection;
            try {

                url = new URL(strings[0]);
                httpsURLConnection = (HttpsURLConnection) url.openConnection();
                httpsURLConnection.setRequestProperty(user_key, key);
                httpsURLConnection.setRequestProperty(content_type, dataFormat);
                httpsURLConnection.setRequestProperty("Accept", dataFormat);

                InputStream inputStream;

                if (httpsURLConnection.getResponseCode() == HttpsURLConnection.HTTP_OK){
                    inputStream = httpsURLConnection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    String data = bufferedReader.readLine();

                    while (data != null){

                        result += data;
                        data = bufferedReader.readLine();
                    }
//                    inputStream.close();
//                    httpsURLConnection.disconnect();
                    return result;
                }else{
                    return null;
                }

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
            requestCount++;
            try {
                if (s != null) {
                    JSONArray jsonArray = new JSONArray(s);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        GameModel gameModel = parseJSonToGameModel(jsonArray.getJSONObject(i));
                        gameModelArrayList.add(gameModel);
                    }
                    if(requestCount >= 2){
                        listener.onTaskCompleted();
                    }
                }
            } catch(JSONException e){
                e.printStackTrace();
            }

        }

    }

    public class GetGenreList extends AsyncTask<String,Void,String>{

        private ArrayList<String> genreList;

        public GetGenreList(ArrayList<String> genreList) {
            this.genreList = genreList;
        }

        @Override
        protected String doInBackground(String... strings) {

            URL url;
            HttpURLConnection connection;
            String result="";

            try {
                url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty(user_key, key);
                connection.setRequestProperty(content_type,dataFormat );

                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String data = bufferedReader.readLine();

                while (data != null){

                    result += data;
                    data = bufferedReader.readLine();
                }

                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONArray jsonArray = new JSONArray(s);
                for (int i = 0; i<jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String genreName = jsonObject.getString("name");

                    genreList.add(genreName);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static GameModel parseJSonToGameModel(JSONObject jsonObject) {
        GameModel gameModel = null;
        try {

            game_mode_name_list = new ArrayList<>();
            genre_list = new ArrayList<>();
            platform_list = new ArrayList<>();

            int gameId = jsonObject.getInt("id");
            String gameName = jsonObject.getString("name");

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

            gameModel = new GameModel(gameId, gameName, genre_list, release_date, summary, image_id, game_mode_name_list,
                    rating, platform_list, video_id, popularity);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return gameModel;
    }

}
