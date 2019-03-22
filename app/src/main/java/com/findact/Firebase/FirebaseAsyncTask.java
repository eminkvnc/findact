package com.example.emin.findact.Firebase;

import android.os.AsyncTask;
import android.os.Handler;

import com.example.emin.findact.OnTaskCompletedListener;


public class FirebaseAsyncTask extends AsyncTask<Void, Void, Void>{

    private Runnable runnable;
    private Runnable timeoutRunnable;
    private Handler handler;
    private boolean taskComplete =false;
    private boolean timeout =false;
    private OnTaskCompletedListener listener;

    public FirebaseAsyncTask(Runnable runnable, OnTaskCompletedListener onTaskCompletedListener) {
        this.runnable = runnable;
        this.listener = onTaskCompletedListener;
    }

    @Override
    protected void onPreExecute() {
        taskComplete = false;
        timeout = false;
        timeoutRunnable = new Runnable() {
            @Override
            public void run() {
                timeout = true;
            }
        };
        handler = new Handler();
        handler.postDelayed(timeoutRunnable,5000);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        runnable.run();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        handler.removeCallbacks(timeoutRunnable);
        taskComplete = true;
        listener.onTaskCompleted();
    }

    public boolean isTaskComplete() {
        return taskComplete;
    }

    public boolean isTimeout() {
        return timeout;
    }



}
