package com.example.emin.findact.APIs;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.emin.findact.Adapters.MovieListItemAdapter;
import com.example.emin.findact.ProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class TMDbAPI {

    String searchUrl = "https://api.themoviedb.org/3/search/movie?api_key=fd50bae5852bf6c2e149317a6e885416&query="; // +movieName

    static String[] genreList = {"Action", "Adventure", "Animation", "Comedy", "Crime","Documentary", "Drama","Family","Fantasy","History",
            "Horror","Music","Mystery","Romance","Sci-Fi","TV-Movie","Thriller","War","Western"};
    static Integer[] genreIdList = {28,12,16,35,80,99,18,10751,14,36,27,10402,9648,10749,878,10770,53,10752,37};

    private static String TAG = "TMDbAPI";

    public void searchMovie (String movieName, ArrayList<MovieModel> movieModelArrayList){

        movieModelArrayList.clear();
        DownloadData downloadData = new DownloadData(movieModelArrayList);
        String url = searchUrl + movieName;
        downloadData.execute(url);
    }

    private static class DownloadData extends AsyncTask<String, Void, String>{

        private ArrayList<MovieModel> mMovieModelArrayList;

        DownloadData(ArrayList<MovieModel> movieModelArrayList){
            this.mMovieModelArrayList = movieModelArrayList;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

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
                Log.d(TAG, "doInBackground: "+ result);
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
                JSONObject jsonObject = new JSONObject(s);
                String results = jsonObject.getString("results");

                JSONArray jsonArray = new JSONArray(results);

                for (int i = 0; i < jsonArray.length(); i++){
                    JSONObject count = (JSONObject) jsonArray.get(i);

                    String language = count.getString("original_language");

                    if (language.contains("tr")|| language.contains("en")){

                        int movieId = count.getInt("id");
                        String title = count.getString("original_title");
                        String release_date = count.getString("release_date");
                        String poster_path = count.getString("poster_path");
                        Double vote_average = count.getDouble("vote_average");
                        String overview = count.getString("overview");
                        String genreIds = count.getString("genre_ids");

                        ArrayList<String> genre = new ArrayList<>();
                        JSONArray jsonArray1 = new JSONArray(genreIds);

                        if (jsonArray1.length() == 0){
                            genre.add("NaN");
                        } else{
                            for (int j = 0; j < jsonArray1.length(); j++){
                                int count2 = (int) jsonArray1.get(j);
                                for (int m = 0; m < genreIdList.length; m++){
                                    if (genreIdList[m] == count2){
                                        genre.add(genreList[m]);
                                    }
                                }
                            }
                        }

                        String [] date = release_date.split("-");
                        release_date = date[2]+"."+date[1]+"."+date[0];

                        MovieModel movieModel = new MovieModel(movieId, title, release_date ,genre ,vote_average.toString() , poster_path,overview, language);
                        mMovieModelArrayList.add(movieModel);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
