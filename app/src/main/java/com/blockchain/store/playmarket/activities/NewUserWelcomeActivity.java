package com.blockchain.store.playmarket.activities;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.blockchain.store.playmarket.R;
import com.blockchain.store.playmarket.crypto.CryptoUtils;
import com.blockchain.store.playmarket.utilities.data.ClipboardUtils;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;

import io.ethmobile.ethdroid.KeyManager;

public class NewUserWelcomeActivity extends AppCompatActivity {

    public CryptoUtils crypto;
    private KeyManager keyManager;

    private String datadir;
    private String etherAddress;
    private TextView AddressTextView;
    private TextView MainTextView;

    private String AccountManagementText = "This is the account management section.\\n\\n\\n\\nHere you can view your address and make an encrypted copy of your wallet.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user_welcome);

        setupView();
        setDatadir();
        setupCryptoUtils();
        setupKeyManager();
    }

    @Override
    public void onBackPressed() {}

    protected void setupView() {
        AddressTextView = (TextView) findViewById(R.id.AddressTextView);
        AddressTextView.setOnClickListener(copyAddressToClipboard);

        MainTextView = (TextView) findViewById(R.id.NewUserWelcomeTextView);
    }

    protected void setupViewForAccountManagement() {
        MainTextView.setText(AccountManagementText);
    }

    private View.OnClickListener copyAddressToClipboard = new View.OnClickListener() {
        public void onClick(View v) {
            ClipboardUtils.copyToClipboard(getApplicationContext(), etherAddress);
            showCopiedAlert();
        }
    };

    public void copyKeyJsonToClipboard(String password) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            for (byte b : keyManager.getKeystore().exportKey(keyManager.getAccounts().get(0), password, password)) {
                baos.write(b);
            }

            ClipboardUtils.copyToClipboard(getApplicationContext(), baos.toString("UTF-8"));
            showBackupAlert();
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    public void promptForPassword(View view) {
        final Dialog d = new Dialog(this);
        d.setContentView(R.layout.password_prompt_dialog);

        final EditText passwordText = (EditText) d.findViewById(R.id.passwordText);

        d.show();

        TextView addFundsBtn = (TextView) d.findViewById(R.id.continueButton);
        addFundsBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                copyKeyJsonToClipboard(passwordText.getText().toString());
                d.dismiss();
            }
        });


        Button close_btn = (Button) d.findViewById(R.id.close_button);
        close_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                d.dismiss();
            }
        });
    }

    public void promptForPasswordForNewAccount() {
        final Dialog d = new Dialog(this);
        d.setContentView(R.layout.password_prompt_dialog);

        final EditText passwordText = (EditText) d.findViewById(R.id.passwordText);

        d.show();

        TextView addFundsBtn = (TextView) d.findViewById(R.id.continueButton);
        addFundsBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                makeNewAccount(passwordText.getText().toString());
                d.dismiss();
            }
        });


        Button close_btn = (Button) d.findViewById(R.id.close_button);
        close_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                d.dismiss();
            }
        });
    }

    private void showCopiedAlert() {
        Toast.makeText(getApplicationContext(), "Address Copied!",
                Toast.LENGTH_LONG).show();
    }

    private void showBackupAlert() {
        Toast.makeText(getApplicationContext(), "Wallet Backup Copied!",
                Toast.LENGTH_LONG).show();
    }

    protected void makeNewAccount(String password) {
        try {
            keyManager.newAccount(password);
            etherAddress = keyManager.getAccounts().get(0).getAddress().getHex();
            Log.d("Ether", etherAddress);
            AddressTextView.setText(etherAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void setupKeyManager() {
        try {
             keyManager = CryptoUtils.setupKeyManager(datadir);

            if (keyManager.getAccounts().isEmpty()) {
                promptForPasswordForNewAccount();
            } else {

                etherAddress = keyManager.getAccounts().get(0).getAddress().getHex();
                Log.d("Ether", etherAddress);


                goToFeaturedAppsPage(null);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void setDatadir() {
        datadir = getFilesDir().getAbsolutePath();
    }

    protected void setupCryptoUtils() {
        crypto = new CryptoUtils();
    }

    public void goToFeaturedAppsPage(View view) {
        Intent myIntent=new Intent(getApplicationContext(), MainMenuActivity.class );
        startActivityForResult(myIntent,0);
    }
}
