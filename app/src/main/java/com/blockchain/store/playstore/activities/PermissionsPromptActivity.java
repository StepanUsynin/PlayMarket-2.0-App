package com.blockchain.store.playstore.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.blockchain.store.playstore.R;
import com.blockchain.store.playstore.utilities.device.PermissionUtils;

public class PermissionsPromptActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions_prompt);

        if (PermissionUtils.storagePermissionGranted(this)) {
            goToLoginPromptActivity();
        }
    }

    public void requestPermissions(View view) {
        if (PermissionUtils.storagePermissionGranted(this)) {
            goToLoginPromptActivity();
        }

        PermissionUtils.verifyStoragePermissions(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            goToLoginPromptActivity();
        } else {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        }
    }

    public void goToLoginPromptActivity() {
        Intent myIntent = new Intent(getApplicationContext(), LoginPromptActivity.class);
        startActivityForResult(myIntent, 0);
    }
}
