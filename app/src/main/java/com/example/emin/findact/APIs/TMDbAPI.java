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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import javax.net.ssl.HttpsURLConnection;

public class TMDbAPI {

    private String searchUrl = "https://api.themoviedb.org/3/search/movie?api_key=fd50bae5852bf6c2e149317a6e885416&query="; // +movieName
    String posterPathUrl = "http://image.tmdb.org/t/p/w185/";   // +poster path
    String genreUrl = "https://api.themoviedb.org/3/genre/movie/list?api_key=fd50bae5852bf6c2e149317a6e885416";


    // Hashmape çevir
    private String[] genreList = {"Action", "Adventure", "Animation", "Comedy", "Crime","Documentary", "Drama","Family","Fantasy","History",
            "Horror","Music","Mystery","Romance","Sci-Fi","TV-Movie","Thriller","War","Western"};
    private Integer[] genreIdList = {28,12,16,35,80,99,18,10751,14,36,27,10402,9648,10749,878,10770,53,10752,37};

    private static String TAG = "TMDbAPI";


    public void searchMovie (String movieName, ArrayList<MovieModel> movieModelArrayList, OnTaskCompletedListener onTaskCompletedListener){

        Log.d(TAG, "searchMovie: "+movieModelArrayList.size());
        Log.d(TAG, "searchMovie: "+movieName);
        movieModelArrayList.clear();
        DownloadData downloadData = new DownloadData(movieModelArrayList,onTaskCompletedListener);
        String url = searchUrl + movieName;
        downloadData.execute(url);

    }

    private class DownloadData extends AsyncTask<String, Void, String>{

        private ArrayList<MovieModel> mMovieModelArrayList;
        private OnTaskCompletedListener listener;
        //ProgressDialog dialog;



        DownloadData(ArrayList<MovieModel> movieModelArrayList, OnTaskCompletedListener listener){
            this.mMovieModelArrayList = movieModelArrayList;
            this.listener = listener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //dialog = new ProgressDialog(mContext);
            //dialog.show();
            Log.d(TAG, "onPreExecute: ");

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

                        String title = count.getString("original_title");
                        String release_date = count.getString("release_date");
                        String poster_path = count.getString("poster_path");
                        Double vote_average = count.getDouble("vote_average");
                        String overview = count.getString("overview");
                        String genreIds = count.getString("genre_ids");


                        String genres = "";
                        JSONArray jsonArray1 = new JSONArray(genreIds);

                        if (jsonArray1.length() == 0){
                            genres = "NaN";
                        } else{
                            for (int j = 0; j < jsonArray1.length(); j++){
                                int count2 = (int) jsonArray1.get(j);
                                for (int m = 0; m < genreIdList.length; m++){
                                    if (genreIdList[m] == count2){
                                        genres =  genreList[m] +", " + genres;
                                    }
                                }
                            }
                        }

                        Log.d(TAG, "onPostExecute: "+genres);
                        Log.d(TAG, "onPostExecute: "+poster_path);

                        MovieModel movieModel = new MovieModel(title, release_date ,genres ,vote_average.toString() , poster_path,overview, language);
                        mMovieModelArrayList.add(movieModel);
                    }
                }

                Log.d(TAG, "onPostExecute: ");
                listener.onTaskCompleted();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
