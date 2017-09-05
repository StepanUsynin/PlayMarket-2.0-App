package com.blockchain.store.playstore;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.ethereum.geth.Address;
import org.ethereum.geth.BigInt;
import org.ethereum.geth.Transaction;

public class NewUserWelcomeActivity extends AppCompatActivity {

    public CryptoUtils crypto;

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
        generateTestTransaction();
        setupKeyManager();
    }

    @Override
    public void onBackPressed() {}

    protected void setupView() {
        AddressTextView = (TextView) findViewById(R.id.AddressTextView);
    }

    protected void setupKeyManager() {
        try {
            if (CryptoUtils.ethdroid.getMainAccount().getAddress().getHex().toString().length() <= 0) {
                CryptoUtils.ethdroid.getKeyManager().newAccount("Test");

                etherAddress = CryptoUtils.ethdroid.getMainAccount().getAddress().getHex().toString();

                Log.d("Ether", etherAddress);
                AddressTextView.setText(etherAddress);

            } else {

                etherAddress = CryptoUtils.ethdroid.getMainAccount().getAddress().getHex().toString();
                Log.d("Ether", etherAddress);

                goToFeaturedAppsPage(null);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generateTestTransaction() {
        BigInt value = new BigInt(0);
        value.setInt64((long) 1100000000000000.0);

        Transaction tx = new Transaction(
                3, new Address("0x5E5c1C8e03472666E0B9e218153869dCBc9c1e65"),
                value, new BigInt(200000), new BigInt((long) 30000000000.0), null);
        try {
            Transaction transaction = CryptoUtils.ethdroid.getKeyManager().getKeystore().signTxPassphrase(CryptoUtils.ethdroid.getMainAccount(), "Test", tx, new BigInt(3));

            Log.d("Ether", CryptoUtils.getRawTransaction(transaction));
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
        Intent myIntent=new Intent(getApplicationContext(),AppListActivity.class );
        startActivityForResult(myIntent,0);
    }
}
