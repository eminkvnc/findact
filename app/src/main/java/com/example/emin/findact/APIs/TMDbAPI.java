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

import javax.net.ssl.HttpsURLConnection;

public class TMDbAPI {

    String searchUrl = "https://api.themoviedb.org/3/search/movie?api_key=fd50bae5852bf6c2e149317a6e885416&query="; // +movieName
    String posterPathUrl = "http://image.tmdb.org/t/p/w185/";   // +poster path

    private static String TAG = "TMDbAPI";


    public void searchMovie (Context context, String movieName, ArrayList<MovieModel> movieModelArrayList, RecyclerView recyclerView){
        Log.d(TAG, "searchMovie: "+movieModelArrayList.size());
        Log.d(TAG, "searchMovie: "+movieName);
        movieModelArrayList.clear();
        DownloadData downloadData = new DownloadData(context,movieModelArrayList,recyclerView);
        String url = searchUrl + movieName;
        downloadData.execute(url);

    }

    private static class DownloadData extends AsyncTask<String, Void, String>{

        private Context mContext;
        private ArrayList<MovieModel> mMovieModelArrayList;
        private RecyclerView mRecyclerView;
        MovieListItemAdapter movieListItemAdapter;
        //ProgressDialog dialog;

        DownloadData(Context context, ArrayList<MovieModel> movieModelArrayList, RecyclerView recyclerView){
            this.mContext = context;
            this.mMovieModelArrayList = movieModelArrayList;
            this.mRecyclerView = recyclerView;
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

                    String title = count.getString("original_title");
                    String release_date = count.getString("release_date");
                    String poster_path = count.getString("poster_path");
                    Double vote_average = count.getDouble("vote_average");
                    String overview = count.getString("overview");
                    String genreIds = count.getString("genre_ids");

                    String language = count.getString("original_language");
                    JSONArray jsonArray1 = new JSONArray(genreIds);

                    String genres = "";
                    for (int j = 0; j < jsonArray1.length(); j++){
                        int count2 = (int) jsonArray1.get(j);
                        genres = genres +"-"+ Integer.toString(count2);
                    }

                    MovieModel movieModel = new MovieModel(title, release_date ,genres ,vote_average.toString() , poster_path,overview);
                    mMovieModelArrayList.add(movieModel);


                }

                movieListItemAdapter = new MovieListItemAdapter(mContext, mMovieModelArrayList);
                mRecyclerView.setAdapter(movieListItemAdapter);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                movieListItemAdapter.notifyDataSetChanged();

                Log.d(TAG, "onPostExecute: ");

                /*if (dialog != null && dialog.isShowing()){
                    dialog.dismiss();
                }*/


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            /*if (dialog != null && dialog.isShowing()){
                dialog.dismiss();
            }*/
        }
    }
}
