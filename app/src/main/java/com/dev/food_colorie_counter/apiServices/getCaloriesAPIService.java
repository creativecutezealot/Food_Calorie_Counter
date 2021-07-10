package com.dev.food_colorie_counter.apiServices;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.util.List;

public class getCaloriesAPIService extends AsyncTask<Void, Void, String> {

    @SuppressLint("StaticFieldLeak")
    private Context mContext;
    private String response = "";
    private String TAG = getClass().getSimpleName();
    private String url;
    private File imageFile;
//    private LoadingDialog loadingDialog;
    private OnResultReceived mListner;

    public interface OnResultReceived {
        void onResult(String result);
    }

    public getCaloriesAPIService(Context mContext, String url, File imageFile, OnResultReceived mListner) {
        this.mContext = mContext;
        this.url = url;
        this.imageFile = imageFile;
        this.mListner = mListner;
    }

//    public void show() {
//        loadingDialog = new LoadingDialog(mContext, false);
//    }

//    private void hide() {
//        loadingDialog.hide();
//    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
//        show();
    }

    @Override
    protected String doInBackground(Void... unsued) {
        try {
            Log.e("URL", url);
            MultipartUtility multipart = new MultipartUtility(url, "UTF-8");
//            Log.e("lgr", Preferences.getValue_String(mContext, Preferences.Token));

            multipart.addFilePart("FILE", imageFile);

            List<String> responseArr = multipart.finish();
            Log.e(TAG, "SERVER REPLIED:");
            for (String line : responseArr) {
                Log.e(TAG, "Upload Files Response:::" + line);
                response = line;
            }

            Log.e(TAG, " Response: " + response);
        } catch (Exception e) {
            Log.e(TAG, "Error:... " + e.getMessage());
            response = "";
        }
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
//        hide();
        if (mListner != null) mListner.onResult(result);
    }

}
