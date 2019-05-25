package com.findact.APIs;

import android.os.AsyncTask;

import com.findact.OnTaskCompletedListener;
import com.google.gson.JsonArray;

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

import javax.net.ssl.HttpsURLConnection;

public class GoogleRecAPI {

    private String recUrl = "https://findact-12b19.appspot.com/recommendation?userId=";
    private String numRec = "&numRecs=100";
    private String modelTypeMovie = "&modelType=";

    public void getRecommendations(int uuid_int, ArrayList<Integer> idArrayList, String type, OnTaskCompletedListener listener){

        String url = recUrl+uuid_int+numRec+modelTypeMovie+type;
        DownloadIds downloadIds = new DownloadIds(idArrayList,type,listener);
        downloadIds.execute(url);

    }

    public static class DownloadIds extends AsyncTask<String,Void,String>{

        private ArrayList<Integer> idArrayList;
        private final OnTaskCompletedListener listener;
        String type;


        DownloadIds(ArrayList<Integer> idArrayList,String type, OnTaskCompletedListener listener) {
            this.idArrayList = idArrayList;
            this.type = type;
            this.listener = listener;
        }


        @Override
        protected String doInBackground(String... strings) {

            String result = "";
            URL url;
            HttpURLConnection connection;

            try {
                url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                if(connection.getResponseCode() == 200){
                    InputStream inputStream = connection.getInputStream();
                    InputStreamReader reader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(reader);

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
            try {
                if (!s.equals("")){
                    JSONObject jsonObject = new JSONObject(s);
                    String articles = jsonObject.getString(type);
                    JSONArray jsonArray = new JSONArray(articles);


                    for (int i = 0; i < jsonArray.length(); i++){

                        idArrayList.add(Integer.parseInt((String) jsonArray.get(i)));
                    }
                }


                listener.onTaskCompleted();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
