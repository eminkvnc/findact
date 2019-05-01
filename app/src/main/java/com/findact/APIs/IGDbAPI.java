package com.findact.APIs;

import android.os.AsyncTask;

import com.findact.OnTaskCompletedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;


public class IGDbAPI {

    private static String genreUrl = "https://api-v3.igdb.com/genres/";
    private static String modeUrl = "https://api-v3.igdb.com/game_modes/";
    private static String gameUrl = "https://api-v3.igdb.com/games/";
    private static String fields = "fields name,genres.name,platforms.alternative_name,first_release_date,summary,cover.image_id,game_modes.name,total_rating,videos.video_id,popularity;";
    private static String search = "search "; // request atarken ; koymayı  unutma
    private static String whereGenres = "where genres = "; // request atarken where Stringleri birleştirilerek kullanılacak
    private static String whereMode = " game_modes = ";
    private static String limit_offset = "limit 50;offset "; // request atarken ; koymayı unutma
    private static String order = "sort popularity desc;";

    private static String whereId = "where id = "; // request atarken sonuna ; koy

    private static String user_key = "user-key";
    private static String key = "2bd597197d5320a9a882cac6123f52eb";
    private static String content_type = "Content-Type";
    private static String dataFormat = "application/json; charset=UTF-8";

    private static ArrayList<String> game_mode_name_list;
    private static ArrayList<String> genre_list;
    private static ArrayList<String> platform_list;

    // request atarken genre ve mode id'leri kullanılıyor
    private static HashMap<String,Integer> genreHashMap = new HashMap<String, Integer>(){{
        put("Simulator", 13);put("Indie", 32);
        put("Tactical", 24);put("Quiz/Trivia", 26);
        put("Fighting", 4);put("Strategy", 15);
        put("Adventure", 31);put("Role-playing (RPG)", 12);
        put("Shooter", 5);put("Music", 7);  }};
    private static HashMap<String,Integer> modeHashMap = new HashMap<String,Integer>(){{
        put("Co-operative", 3);
        put("Single player", 1  );
        put("Multiplayer", 2);
        put("Massively Multiplayer Online (MMO)", 5);
        put("Split screen", 4);}};

    private static int requestCount;


    public void getGenres(ArrayList<String> genreList, ArrayList<String> modeList, OnTaskCompletedListener onTaskCompletedListener){
        GetGenreList getGenreList = new GetGenreList(genreList, onTaskCompletedListener);
        getGenreList.execute(genreUrl);
        GetGenreList getModeList = new GetGenreList(modeList, null);
        getModeList.execute(modeUrl);
    }

    public void searchGame(String game_name, final ArrayList<GameModel> gameModelArrayList, final OnTaskCompletedListener listener) {
        requestCount = 2;
        gameModelArrayList.clear();
        String params = fields + search + "\""+game_name+"\";";
        DownloadData downloadData = new DownloadData(gameModelArrayList, listener, params);
        downloadData.execute(gameUrl);

    }

    public void getGamesById(ArrayList<Integer> idList, final ArrayList<GameModel> gameModelArrayList, final OnTaskCompletedListener listener){
        gameModelArrayList.clear();
        String ids = "";
        if (idList.size() < 50){
            for (int i = 0; i < idList.size(); i++){
                ids += idList.get(i);
                if (idList.size()-1 != i){
                    ids += ",";
                }
            }
        }
        String params = fields + whereId + "("+ids+");"+ limit_offset + "0";
        DownloadData downloadData = new DownloadData(gameModelArrayList, listener, params);
        downloadData.execute(gameUrl);
    }

    public void searchByGenreAndModeName(ArrayList<String> selectedGenres, ArrayList<String> selectedMode,final ArrayList<GameModel> gameModelArrayList,final OnTaskCompletedListener listener){

        String params = null;
        String genres = "";
        String mode;

        // Genre seçilmiş ise
        if (selectedGenres.size() > 0){
            for (int i = 0; i < selectedGenres.size(); i++){
                genres +=genreHashMap.get(selectedGenres.get(i));
                if(i != selectedGenres.size()-1){
                    genres += ",";
                }
            }
            // mode seçilmiş ise
            if (selectedMode.size() > 0){
                mode = modeHashMap.get(selectedMode.get(0)).toString();
                params = fields + whereGenres + "("+genres+")&" + whereMode +mode+";";
            } else{
                params = fields + whereGenres + "("+genres+");";
            }
        }
        // genre seçilmemiş ise
        else {
            // mode seçilmiş ise
            if (selectedMode.size() > 0){
                mode = modeHashMap.get(selectedMode.get(0)).toString();
                params = fields + "where "+ whereMode +mode+";";
            }
        }

        gameModelArrayList.clear();
        requestCount = 0;
        for (int i =0; i <=3; i++){
            String newParams = params + limit_offset + (i*50) + ";" + order;
            DownloadData downloadData = new DownloadData(gameModelArrayList,listener, newParams );
            downloadData.execute(gameUrl);
        }
    }


    public static class DownloadData extends AsyncTask<String, Void, String> {

        private ArrayList<GameModel> gameModelArrayList;
        private final OnTaskCompletedListener listener;
        private String params;

        DownloadData(ArrayList<GameModel> gameModelArrayList, OnTaskCompletedListener listener, String params) {
            this.gameModelArrayList = gameModelArrayList;
            this.listener = listener;
            this.params = params;
        }


        @Override
        protected String doInBackground(String... strings) {

            StringBuilder result = new StringBuilder();
            URL url;
            HttpsURLConnection httpsURLConnection = null;
            try {


                url = new URL(strings[0]);
                httpsURLConnection = (HttpsURLConnection) url.openConnection();
                httpsURLConnection.setRequestProperty(user_key, key);
                httpsURLConnection.setRequestProperty(content_type, dataFormat);
                httpsURLConnection.setRequestMethod("POST");
                httpsURLConnection.setDoOutput(true);
                httpsURLConnection.setInstanceFollowRedirects(false);

                OutputStreamWriter writer = new OutputStreamWriter(httpsURLConnection.getOutputStream(),"UTF-8");
                writer.write(params);
                writer.close();

                InputStream inputStream;

                if (httpsURLConnection.getResponseCode() == HttpsURLConnection.HTTP_OK){
                    inputStream = httpsURLConnection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    String data = bufferedReader.readLine();

                    while (data != null){

                        result.append(data);
                        data = bufferedReader.readLine();
                    }
                    return result.toString();
                }else{
                    return null;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally {
                assert httpsURLConnection != null;
                httpsURLConnection.disconnect();
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
        private OnTaskCompletedListener listener;

        public GetGenreList(ArrayList<String> genreList, OnTaskCompletedListener onTaskCompletedListener) {
            this.genreList = genreList;
            this.listener = onTaskCompletedListener;
        }

        @Override
        protected String doInBackground(String... strings) {

            URL url;
            HttpsURLConnection httpsURLConnection = null;
            StringBuilder result= new StringBuilder();

            try {
                url = new URL(strings[0]);
                httpsURLConnection = (HttpsURLConnection) url.openConnection();
                httpsURLConnection.setRequestProperty(user_key, key);
                httpsURLConnection.setRequestProperty(content_type, dataFormat);
                httpsURLConnection.setRequestMethod("POST");
                httpsURLConnection.setDoOutput(true);
                httpsURLConnection.setInstanceFollowRedirects(false);

                OutputStreamWriter writer = new OutputStreamWriter(httpsURLConnection.getOutputStream(),"UTF-8");
                writer.write("fields name;");
                writer.close();

                InputStream inputStream;

                if (httpsURLConnection.getResponseCode() == HttpsURLConnection.HTTP_OK){
                    inputStream = httpsURLConnection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    String data = bufferedReader.readLine();

                    while (data != null){

                        result.append(data);
                        data = bufferedReader.readLine();
                    }
                    return result.toString();
                }else{
                    return null;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally {
                assert httpsURLConnection != null;
                httpsURLConnection.disconnect();
            }
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
            if(listener != null){
                listener.onTaskCompleted();
            }
        }
    }

    private static GameModel parseJSonToGameModel(JSONObject jsonObject) {
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
                video_id = "NaN";
            }


            // for image_id
            String cover;
            String image_id;
            if (jsonObject.has("cover")) {
                cover = jsonObject.getString("cover");
                JSONObject imageObject = new JSONObject(cover);
                image_id = imageObject.getString("image_id");
            } else {
                image_id = "NaN";
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

            gameModel = new GameModel("game"+gameId, gameId, gameName, genre_list, release_date, summary, image_id, game_mode_name_list,
                    rating, platform_list, video_id, popularity);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return gameModel;
    }

}