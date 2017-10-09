package com.blockchain.store.playmarket.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.blockchain.store.playmarket.R;
import com.blockchain.store.playmarket.crypto.CryptoUtils;
import com.blockchain.store.playmarket.utilities.data.ClipboardUtils;

import io.ethmobile.ethdroid.KeyManager;

public class LoginPromptActivity extends AppCompatActivity {

    private KeyManager keyManager;
    private EditText importUserButton;

    private Activity activity;

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
        activity = this;
        importUserButton = (EditText) findViewById(R.id.ImportUserButton);
        importUserButton.setOnClickListener(importAddressFromClipboard);
    }

    protected void setupKeyManager() {
        keyManager = CryptoUtils.setupKeyManager(getFilesDir().getAbsolutePath());
    }

    private View.OnClickListener importAddressFromClipboard = new View.OnClickListener() {
        public void onClick(View v) {
            final Dialog d = new Dialog(activity);
            d.setContentView(R.layout.password_prompt_dialog);

            final EditText passwordText = (EditText) d.findViewById(R.id.passwordText);

            d.show();

            TextView addFundsBtn = (TextView) d.findViewById(R.id.continueButton);
            addFundsBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try {
                        ClipboardUtils.importKeyFromClipboard(getApplicationContext(), keyManager.getKeystore(), passwordText.getText().toString());
                        showImportSuccessfulAlert();
                        d.dismiss();
                        goToFeaturedAppsPage(null);
                    } catch (Exception e) {
                        showImportFailedAlert();
                        e.printStackTrace();
                    }
                }
            });


            Button close_btn = (Button) d.findViewById(R.id.close_button);
            close_btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    d.dismiss();
                }
            });
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
