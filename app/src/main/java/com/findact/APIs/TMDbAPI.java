package com.findact.APIs;

import android.os.AsyncTask;
import android.util.EventLog;
import android.util.Log;
import com.findact.OnTaskCompletedListener;
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
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

public class TMDbAPI {

    private static int requestCount;


    private static HashMap<Integer,String> genre_list = new HashMap<Integer, String>(){{put(28, "Action");put(12,"Adventure");
        put(16,"Animation");put(35,"Comedy" );put(80,"Crime");
        put(99, "Documentary");put(18,"Drama" );put(10751,"Family" );
        put(14,"Fantasy" );put(36,"History" );put(27,"Horror" );put(10402,"Music" );
        put(9648,"Mystery" );put(10749,"Romance" );put(878,"Science Fiction");
        put(10770,"TV Movie" );put(53,"Thriller");put(10752,"War" );put(37,"Western");}};

    public static HashMap<String,Integer> genreIDList = new HashMap<String,Integer >(){{put("Action",28);put("Adventure",12);
        put("Animation",16);put("Comedy",35 );put("Crime",80);
        put("Documentary",99);put("Drama",18 );put("Family",10751 );
        put("Fantasy",14 );put("History",36 );put("Horror",27 );put("Music",10402 );
        put("Mystery",9648 );put("Romance",10749 );put("Science Fiction",878);
        put("TV Movie",10770 );put("Thriller",53);put("War",10752 );put("Western",37);}};



    // METHODS
    public void getGenres(ArrayList<String> genreList, OnTaskCompletedListener onTaskCompletedListener){
        String url = "https://api.themoviedb.org/3/genre/movie/list?api_key=fd50bae5852bf6c2e149317a6e885416";
        GetMovieGenres getMovieGenres = new GetMovieGenres(genreList,onTaskCompletedListener);
        getMovieGenres.execute(url);
    }

    public void searchMovie (String movieName, ArrayList<MovieModel> movieModelArrayList, OnTaskCompletedListener listener){

        requestCount = 9;

        String searchUrl = "https://api.themoviedb.org/3/search/movie?api_key=fd50bae5852bf6c2e149317a6e885416&sort_by=popularity.desc&query="; // +movieName

        movieModelArrayList.clear();
        DownloadData downloadData = new DownloadData(movieModelArrayList, listener);
        String url = searchUrl + movieName;
        downloadData.execute(url);
    }

     public void searchMovieByGenre(ArrayList<String> selectedMovieIds, ArrayList<MovieModel> movieModelArrayList, OnTaskCompletedListener listener){
        // edit paging

        String ids;
        ids = String.valueOf(genreIDList.get(selectedMovieIds.get(0)));
        for (int i = 1; i< selectedMovieIds.size(); i++){
            ids = ids+","+String.valueOf(genreIDList.get(selectedMovieIds.get(i)));
        }
         requestCount = 0;
        movieModelArrayList.clear();
        for (int i = 1; i <= 10; i++){
            DownloadData downloadData1 = new DownloadData(movieModelArrayList,listener );
            String searchUrl = "https://api.themoviedb.org/3/discover/movie?api_key=fd50bae5852bf6c2e149317a6e885416&sort_by=popularity.desc&page="+i+"&with_genres=";
            downloadData1.execute(searchUrl+ids);
        }
    }

    public void searchMovieByGenreForExplore(ArrayList<String> selectedMovieIds, ArrayList<ExploreModel> exploreModelArrayList, OnTaskCompletedListener listener){

        requestCount = 0;
        String ids;
        ids = String.valueOf(genreIDList.get(selectedMovieIds.get(0)));
        for (int i = 1; i< selectedMovieIds.size(); i++){
            ids = ids+"%2C"+String.valueOf(genreIDList.get(selectedMovieIds.get(i)));
        }
        for (int i = 1; i <= 5; i++){
            DownloadRecData downloadRecData = new DownloadRecData(listener, "byGenre",exploreModelArrayList );
            String searchUrl = "https://api.themoviedb.org/3/discover/movie?api_key=fd50bae5852bf6c2e149317a6e885416&sort_by=popularity.desc&page="+i+"&with_genres=";
            downloadRecData.execute(searchUrl+ids);
        }
    }

    public void searchMovieByID(ArrayList<Integer> movieIds,ArrayList<ExploreModel> exploreModelArrayList, OnTaskCompletedListener listener ){
        requestCount = 0;
        for(int i = 0; i < movieIds.size(); i++){
            DownloadRecData downloadRecData = new DownloadRecData(listener,"byId",exploreModelArrayList );
            String searchUrl ="https://api.themoviedb.org/3/movie/"+movieIds.get(i)+"?api_key=fd50bae5852bf6c2e149317a6e885416";
            downloadRecData.execute(searchUrl);
        }
    }


    // ASYNCTASK
    private static class GetMovieGenres extends AsyncTask<String,Void,String>{
        private ArrayList<String> genreList;
        private OnTaskCompletedListener listener;

        private GetMovieGenres(ArrayList<String> genreList, OnTaskCompletedListener onTaskCompletedListener) {
            this.genreList = genreList;
            this.listener = onTaskCompletedListener;
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
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);


                String data = bufferedReader.readLine();
                while (data != null) {
                    result += data;
                    data = bufferedReader.readLine();
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
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("genres");

                for (int i = 0; i < jsonArray.length(); i++){
                    String genreName = jsonArray.getJSONObject(i).getString("name");
                    //int genreID = jsonArray.getJSONObject(i).getInt("id");
                    genreList.add(genreName);
                    //genre_list.put(genreID,genreName );
                }

                Log.d("onPostExecute", "onPostExecute: "+genre_list.size());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(listener != null){
                listener.onTaskCompleted();
            }
        }
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
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);


                String data = bufferedReader.readLine();
                while (data != null) {
                    result += data;
                    data = bufferedReader.readLine();
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
            requestCount++;
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
                        String popularity = count.getString("popularity");

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

                        MovieModel movieModel = new MovieModel("movie"+movieId, movieId, title, release_date, genre, vote_average.toString(),popularity, poster_path, overview, language);
                        mMovieModelArrayList.add(movieModel);
                    }
                }
                if(requestCount == 10){
                    listener.onTaskCompleted();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private static class DownloadRecData extends AsyncTask<String,Void,String> {

        private ArrayList<ExploreModel> exploreModelArrayList;
        private OnTaskCompletedListener listener;
        private String searchType;

        DownloadRecData(OnTaskCompletedListener listener, String searchType, ArrayList<ExploreModel> exploreModelArrayList) {

            this.listener = listener;
            this.searchType = searchType;
            this.exploreModelArrayList = exploreModelArrayList;
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            URL url;
            HttpURLConnection connection;

            try {
                url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                if (connection.getResponseCode() == 200) {

                    InputStream inputStream = connection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    String data = bufferedReader.readLine();
                    while (data != null) {
                        result += data;
                        data = bufferedReader.readLine();
                    }
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
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            int movieId;
            String title;
            String release_date;
            String popularity;
            String poster_path;
            String language;
            String genreIds;
            String overview;
            Double vote_average;
            ArrayList<String> genre = new ArrayList<>();
            requestCount++;

            try {

                JSONObject jsonObject = new JSONObject(s);

                if (searchType.equals("byGenre")) {

                    String results = jsonObject.getString("results");

                    JSONArray jsonArray = new JSONArray(results);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject count = (JSONObject) jsonArray.get(i);

                        language = count.getString("original_language");

                        if (language.contains("tr") || language.contains("en")) {

                            movieId = count.getInt("id");
                            title = count.getString("original_title");
                            release_date = count.getString("release_date");
                            poster_path = count.getString("poster_path");
                            vote_average = count.getDouble("vote_average");
                            overview = count.getString("overview");
                            genreIds = count.getString("genre_ids");
                            popularity = count.getString("popularity");

                            JSONArray jsonArray1 = new JSONArray(genreIds);
                            genre.clear();
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
                            MovieModel movieModel = new MovieModel("movie" + movieId, movieId, title, release_date, genre, vote_average.toString(), popularity, poster_path, overview, language);
                            exploreModelArrayList.add(new ExploreModel(null, movieModel, "Movie"));
                        }

                    }

                } else {

                    language = jsonObject.getString("original_language");

                    if (language.contains("tr") || language.contains("en")) {

                        movieId = jsonObject.getInt("id");
                        title = jsonObject.getString("original_title");
                        release_date = jsonObject.getString("release_date");
                        poster_path = jsonObject.getString("poster_path");
                        vote_average = jsonObject.getDouble("vote_average");
                        overview = jsonObject.getString("overview");
                        genreIds = jsonObject.getString("genres");

                        popularity = jsonObject.getString("popularity");

                        JSONArray jsonArray1 = new JSONArray(genreIds);

                        if (jsonArray1.length() == 0) {
                            genre.add("NaN");
                        } else {
                            for (int j = 0; j < jsonArray1.length(); j++) {
                                JSONObject object = (JSONObject) jsonArray1.get(j);
                                int id = object.getInt("id");
                                genre.add(genre_list.get(id));
                            }
                        }

                        if (release_date.equals("")) {
                            release_date = "NaN";
                        } else {
                            String[] date = release_date.split("-");
                            release_date = date[2] + "." + date[1] + "." + date[0];
                        }

                        MovieModel movieModel = new MovieModel("movie" + movieId, movieId, title, release_date, genre, vote_average.toString(), popularity, poster_path, overview, language);
                        exploreModelArrayList.add(new ExploreModel(null, movieModel, "Movie"));
                    }
                }
                if (requestCount == 5){
                    listener.onTaskCompleted();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}
