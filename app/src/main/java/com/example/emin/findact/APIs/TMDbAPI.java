package com.example.emin.findact.APIs;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class TMDbAPI {

    String searchUrl = "https://api.themoviedb.org/3/search/movie?api_key=fd50bae5852bf6c2e149317a6e885416&query="; // +movieName
    String posterPathUrl = "http://image.tmdb.org/t/p/w185/";   // +poster path

    public void searchMovie (String movieName){
        DownloadData downloadData = new DownloadData();
        searchUrl = searchUrl + movieName;
        downloadData.execute(searchUrl);
    }

    private static class DownloadData extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {

            String result = "";
            URL url;
            HttpsURLConnection httpsURLConnection;

            try {
                url = new URL(strings[0]);
                httpsURLConnection = (HttpsURLConnection) url.openConnection();
                InputStream inputStream = httpsURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                int data = inputStreamReader.read();

                while (data > 0){
                    char ch = (char) data;
                    result += ch;
                    data = inputStreamReader.read();
                }
                Log.d("doInBackground", "doInBackground: "+ result);
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

            Log.d("onPostExecute", "onPostExecute: "+s);

            ArrayList<String> titleList = new ArrayList<>();
            ArrayList<String> release_dateList = new ArrayList<>();
            ArrayList<String> genreList = new ArrayList<>();
            ArrayList<Integer> popularityList = new ArrayList<>();
            ArrayList<Double> vote_averageList = new ArrayList<>();
            ArrayList<String> poster_pathList = new ArrayList<>();
            ArrayList<Boolean> adultList = new ArrayList<>();

            try {
                JSONObject jsonObject = new JSONObject(s);
                String results = jsonObject.getString("results");

                JSONArray jsonArray = new JSONArray(results);

                for (int i = 0; i < jsonArray.length(); i++){
                    JSONObject count = (JSONObject) jsonArray.get(i);

                    String title = count.getString("original_title");
                    String release_date = count.getString("release_date");
                    String poster_path = count.getString("poster_path");
                    int popularity = count.getInt("popularity");
                    double vote_average = count.getDouble("vote_average");
                    boolean adult = count.getBoolean("adult");

                    titleList.add(title);
                    release_dateList.add(release_date);
                    poster_pathList.add(poster_path);
                    popularityList.add(popularity);
                    vote_averageList.add(vote_average);
                    adultList.add(adult);

                    String genreIds = count.getString("genre_ids");
                    JSONArray jsonArray1 = new JSONArray(genreIds);

                    String genres = "";

                    for (int j = 0; j < jsonArray1.length(); j++){
                        int count2 = (int) jsonArray1.get(j);
                        genres = genres + Integer.toString(count2);
                    }
                    genreList.add(genres);

                    MovieModel movieModel = new MovieModel(titleList, release_dateList, genreList, popularityList,
                            vote_averageList,poster_pathList ,adultList );
//                    Log.d("onPostExecute", "onPostExecute: "+movieModel.getTitle());
                }

                Log.d("onPostExecute", "onPostExecute: "+titleList);
                Log.d("onPostExecute", "onPostExecute: "+genreList);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


}
