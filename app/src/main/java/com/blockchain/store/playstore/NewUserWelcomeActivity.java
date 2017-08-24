package com.blockchain.store.playstore;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

import io.ethmobile.ethdroid.KeyManager;
import org.ethereum.geth.Account;

public class NewUserWelcomeActivity extends AppCompatActivity {

    public CryptoUtils crypto;
    public List<Account> accounts;

    private String datadir;
    private String etherAddress;
    private TextView AddressTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user_welcome);

        setupView();
        setDatadir();
        setupCryptoUtils();
        setupKeyManager();
    }

    protected void setupView() {
        AddressTextView = (TextView) findViewById(R.id.AddressTextView);
    }

    protected void setupKeyManager() {
        crypto.initKeyManager(datadir);

        try {
            crypto.keyManager.newAccount("Test");
            etherAddress = crypto.getLastAddress();

            Log.d("Ether", etherAddress);
            AddressTextView.setText(etherAddress);
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
}
