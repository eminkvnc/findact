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
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

public class TMDbAPI {

    private static HashMap<Integer,String> genre_list = new HashMap<Integer, String>(){{put(28, "Action");put(12,"Adventure");
                                    put(16,"Animation");put(35,"Comedy" );put(80,"Crime");
                                    put(99, "Documentary");put(18,"Drama" );put(10751,"Family" );
                                    put(14,"Fantasy" );put(36,"History" );put(27,"Horror" );put(10402,"Music" );
                                    put(9648,"Mystery" );put(10749,"Romance" );put(878,"Sci-Fi");
                                    put(10770,"TV-Movie" );put(53,"Thriller");put(10752,"War" );put(37,"Western");}};

    public void searchMovie (String movieName, ArrayList<MovieModel> movieModelArrayList, OnTaskCompletedListener listener){

        String searchUrl = "https://api.themoviedb.org/3/search/movie?api_key=fd50bae5852bf6c2e149317a6e885416&query="; // +movieName

        movieModelArrayList.clear();
        DownloadData downloadData = new DownloadData(movieModelArrayList, listener);
        String url = searchUrl + movieName;
        downloadData.execute(url);
    }

    private static class DownloadData extends AsyncTask<String, Void, String> {

        private ArrayList<MovieModel> mMovieModelArrayList;
        private OnTaskCompletedListener listener;


        DownloadData(ArrayList<MovieModel> movieModelArrayList, OnTaskCompletedListener listener) {

            this.mMovieModelArrayList = movieModelArrayList;
            this.listener = listener;
        }

        @Override
        protected void onPreExecute () {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground (String...strings){

            String result = "";
            URL url;
            HttpsURLConnection httpsURLConnection;

            try {
                url = new URL(strings[0]);
                httpsURLConnection = (HttpsURLConnection) url.openConnection();
                InputStream inputStream = httpsURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                int data = inputStreamReader.read();

                while (data > 0) {
                    char ch = (char) data;
                    result += ch;
                    data = inputStreamReader.read();
                }
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
        protected void onPostExecute (String s){
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                String results = jsonObject.getString("results");

                JSONArray jsonArray = new JSONArray(results);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject count = (JSONObject) jsonArray.get(i);

                    String language = count.getString("original_language");

                    if (language.contains("tr") || language.contains("en")) {

                        int movieId = count.getInt("id");
                        String title = count.getString("original_title");
                        String release_date = count.getString("release_date");
                        String poster_path = count.getString("poster_path");
                        Double vote_average = count.getDouble("vote_average");
                        String overview = count.getString("overview");
                        String genreIds = count.getString("genre_ids");

                        ArrayList<String> genre = new ArrayList<>();
                        JSONArray jsonArray1 = new JSONArray(genreIds);

                        if (jsonArray1.length() == 0) {
                            genre.add("NaN");
                        } else {
                            for (int j = 0; j < jsonArray1.length(); j++) {
                                int count2 = (int) jsonArray1.get(j);
                                genre.add(genre_list.get(count2));
                            }
                        }

                        if (release_date.equals("")) {
                            release_date = "NaN";
                        } else {
                            String[] date = release_date.split("-");
                            release_date = date[2] + "." + date[1] + "." + date[0];
                        }

                        MovieModel movieModel = new MovieModel(movieId, title, release_date, genre, vote_average.toString(), poster_path, overview, language);
                        mMovieModelArrayList.add(movieModel);
                    }
                }

                listener.onTaskCompleted();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
