package com.blockchain.store.playstore;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

import io.ethmobile.ethdroid.EthDroid;
import io.ethmobile.ethdroid.KeyManager;
import org.ethereum.geth.Account;

public class NewUserWelcomeActivity extends AppCompatActivity {

    public KeyManager keyManager;
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
        startEtherNode();
        setupKeyManager();
    }

    protected void setupView() {
        AddressTextView = (TextView) findViewById(R.id.AddressTextView);
    }

    protected void setupKeyManager() {
        keyManager = KeyManager.newKeyManager(datadir);

        try {
            keyManager.newAccount("Test");
            accounts = keyManager.getAccounts();
            etherAddress = accounts.get(accounts.size()-1).getAddress().getHex().toString();

            Log.d("Ether", etherAddress);
            AddressTextView.setText(etherAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void setDatadir() {
        datadir = getFilesDir().getAbsolutePath();
    }

    protected void startEtherNode() {
        try {
            new EthDroid.Builder(datadir)
                    .onTestnet()
                    .build()
                    .start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
