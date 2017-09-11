package com.blockchain.store.playstore;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by samsheff on 23/08/2017.
 */

public class ApkInstaller extends AsyncTask<String,Void,Void> {
    private Context context;
    public void setContext(Context contextf){
        context = contextf;
    }

    public boolean successful = true;
    public boolean isDownloading = true;

    @Override
    protected Void doInBackground(String... arg0) {
        try {
            URL url = new URL(arg0[0]);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);


            File outputFile;
            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                String PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();

                File file = new File(PATH);
                file.mkdirs();
                outputFile = new File(file, "app.apk");

                if(outputFile.exists()){
                    outputFile.delete();
                }
            } else {
                String PATH = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath();
                File file = new File(PATH);
                file.mkdirs();
                outputFile = new File(file, "app.apk");

                if(outputFile.exists()){
                    outputFile.delete();
                }
            }

            HttpDownloadUtility downloader = new HttpDownloadUtility();
            try {
                if (!downloader.downloadFile(arg0[0], outputFile)) {
                    successful = false;
                    isDownloading = false;
                    return null;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                intent.setDataAndType(Uri.parse("content://" + outputFile), "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.parse("file://" + outputFile), "application/vnd.android.package-archive");
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);


        } catch (Exception e) {
            Log.e("APK", "Install error! " + e.getMessage());
        }

        isDownloading = false;

        return null;
    }
}
