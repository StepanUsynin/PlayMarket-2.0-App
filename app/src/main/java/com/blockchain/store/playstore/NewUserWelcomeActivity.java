package com.blockchain.store.playstore;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.ethereum.geth.Address;
import org.ethereum.geth.BigInt;
import org.ethereum.geth.Transaction;

import io.ethmobile.ethdroid.KeyManager;

public class NewUserWelcomeActivity extends AppCompatActivity {

    public CryptoUtils crypto;
    private KeyManager keyManager;

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

    @Override
    public void onBackPressed() {}

    protected void setupView() {
        AddressTextView = (TextView) findViewById(R.id.AddressTextView);
        AddressTextView.setOnClickListener(copyAddressToClipboard);
    }

    private View.OnClickListener copyAddressToClipboard = new View.OnClickListener() {
        public void onClick(View v) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("address", etherAddress);
            clipboard.setPrimaryClip(clip);

            showCopiedAlert();
        }
    };

    private void showCopiedAlert() {
        Toast.makeText(getApplicationContext(), "Address Copied!",
                Toast.LENGTH_LONG).show();
    }

    protected void setupKeyManager() {
        try {
             keyManager = CryptoUtils.setupKeyManager(datadir);

            if (keyManager.getAccounts().isEmpty()) {
                keyManager.newAccount("Test");

                etherAddress = keyManager.getAccounts().get(0).getAddress().getHex();

                Log.d("Ether", etherAddress);
                AddressTextView.setText(etherAddress);

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

    private void goToFeaturedAppsPage(View view) {
        Intent myIntent=new Intent(getApplicationContext(),AppListActivity.class );
        startActivityForResult(myIntent,0);
    }
}
