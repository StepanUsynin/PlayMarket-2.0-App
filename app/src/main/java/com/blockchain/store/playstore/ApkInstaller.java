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

    public static String APK_MIME_TYPE = "application/vnd.android.package-archive";
    public static String APK_FILENAME = "app.apk";

    @Override
    protected Void doInBackground(String... arg0) {
        try {
            URL url = new URL(arg0[0]);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);


            File outputFile;
            String filePath;

            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            } else {
                filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath();
            }

            File file = new File(filePath);
            file.mkdirs();
            outputFile = new File(file, APK_FILENAME);

            if(outputFile.exists()){
                outputFile.delete();
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
            intent.setDataAndType(getUriForApk(outputFile), APK_MIME_TYPE);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);


        } catch (Exception e) {
            Log.e("APK", "Install error! " + e.getMessage());
        }

        isDownloading = false;

        return null;
    }

    private Uri getUriForApk(File file) {
        if (shouldUseContentUri()) {
            return Uri.parse("content://" + file);
        } else {
            return Uri.parse("file://" + file);
        }
    }

    public static boolean shouldUseContentUri() {
        return android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }
}
