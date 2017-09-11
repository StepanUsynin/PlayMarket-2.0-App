package com.blockchain.store.playstore;

import android.os.Build;
import android.util.Log;

/**
 * Created by samsheff on 11/09/2017.
 */

public class BuildUtils {

    public static boolean shouldUseContentUri() {

        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static boolean isXaomi() {
        return Build.BRAND == "Xiaomi";
    }

    public static boolean isFalseReleaseVersion() {
        return Build.VERSION.RELEASE.contains("7.0") || Build.VERSION.RELEASE.contains("6.0");
    }

    public static void printPhoneInfo() {
        Log.d("Device", "Manufactuer: " + Build.MANUFACTURER);
        Log.d("Device", "Brand: " + Build.BRAND);
        Log.d("Device", "Model: " + Build.MODEL);
        Log.d("Device", "Android Version: " + Build.VERSION.RELEASE);
        Log.d("Device", "Android SDK Level: " + Build.VERSION.SDK_INT);
    }
}
