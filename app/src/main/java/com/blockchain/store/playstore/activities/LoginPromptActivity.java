package com.blockchain.store.playstore.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.blockchain.store.playstore.R;
import com.blockchain.store.playstore.crypto.CryptoUtils;
import com.blockchain.store.playstore.utilities.data.ClipboardUtils;

import io.ethmobile.ethdroid.KeyManager;

public class LoginPromptActivity extends AppCompatActivity {

    private KeyManager keyManager;
    private EditText importUserButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_prompt);

        setupView();
        setupKeyManager();

        try {
            if (keyManager.getAccounts().size() > 0)
                LoadNewUserWelcomeActivity(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {}

    public void LoadNewUserWelcomeActivity(View view) {
        Intent myIntent = new Intent(getApplicationContext(), NewUserWelcomeActivity.class);
        startActivityForResult(myIntent, 0);
    }

    public void setupView() {
        importUserButton = (EditText) findViewById(R.id.ImportUserButton);
        importUserButton.setOnClickListener(importAddressFromClipboard);
    }

    protected void setupKeyManager() {
        keyManager = CryptoUtils.setupKeyManager(getFilesDir().getAbsolutePath());
    }

    private View.OnClickListener importAddressFromClipboard = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                ClipboardUtils.importKeyFromClipboard(getApplicationContext(), keyManager.getKeystore());
                showImportSuccessfulAlert();
                goToFeaturedAppsPage(null);

            } catch (Exception e) {
                showImportFailedAlert();
                e.printStackTrace();
            }
        }
    };

    private void showImportSuccessfulAlert() {
        Toast.makeText(getApplicationContext(), "Import Successful!",
                Toast.LENGTH_LONG).show();
    }

    private void showImportFailedAlert() {
        Toast.makeText(getApplicationContext(), "Import Failed!",
                Toast.LENGTH_LONG).show();
    }

    public void goToFeaturedAppsPage(View view) {
        Intent myIntent=new Intent(getApplicationContext(), MainMenuActivity.class );
        startActivityForResult(myIntent,0);
    }
}
