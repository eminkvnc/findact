package com.example.emin.findact.Firebase;

import android.os.AsyncTask;
import android.util.Log;
import com.example.emin.findact.Adapters.UserListItemAdapter;
import com.example.emin.findact.ProgressDialog;


public class FirebaseAsyncTask extends AsyncTask<Void, Void, Void> {
    private String TAG = "FirebaseAsyncTask";
    private UserListItemAdapter adapter;
    private ProgressDialog progressDialog;
    private Runnable runnable;

    public FirebaseAsyncTask(UserListItemAdapter adapter, ProgressDialog progressDialog, Runnable runnable) {
        this.adapter = adapter;
        this.progressDialog = progressDialog;
        this.runnable = runnable;
    }

    @Override
    protected void onPreExecute() {
        Log.d(TAG, "onPreExecute: "+getStatus().toString());
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... voids) {

        Log.d(TAG, "doInBackground: "+getStatus().toString());
        runnable.run();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Log.d(TAG, "onPostExecute: "+getStatus().toString());
        progressDialog.dismiss();
        adapter.notifyDataSetChanged();
        super.onPostExecute(aVoid);
    }



}
